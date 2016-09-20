/*
 * Copyright (c) 2016, Kevin Phoenix
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package in.twizmwaz.cardinal.module.spawn;

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.event.match.MatchChangeStateEvent;
import in.twizmwaz.cardinal.event.match.MatchLoadCompleteEvent;
import in.twizmwaz.cardinal.event.player.CardinalRespawnEvent;
import in.twizmwaz.cardinal.event.player.PlayerContainerChangeStateEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.match.MatchState;
import in.twizmwaz.cardinal.match.MatchThread;
import in.twizmwaz.cardinal.module.AbstractListenerModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.id.IdModule;
import in.twizmwaz.cardinal.module.kit.Kit;
import in.twizmwaz.cardinal.module.kit.KitModule;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionModule;
import in.twizmwaz.cardinal.module.region.type.PointRegion;
import in.twizmwaz.cardinal.module.region.type.modifications.PointProviderRegion;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.module.team.TeamModule;
import in.twizmwaz.cardinal.playercontainer.PlayingPlayerContainer;
import in.twizmwaz.cardinal.util.ListUtil;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.ParseUtil;
import in.twizmwaz.cardinal.util.Players;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.Vector;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.located.Located;

import java.util.List;
import java.util.stream.Collectors;

@ModuleEntry(depends = {IdModule.class, TeamModule.class/*, KitModule.class, FilterModule.class*/, RegionModule.class})
public class SpawnModule extends AbstractListenerModule {

  @Override
  public boolean loadMatch(@NonNull Match match) {
    Document document = match.getMap().getDocument();
    boolean defaultPresent = false;
    for (Element spawnsElement : document.getRootElement().getChildren("spawns")) {
      for (Element spawnElement : spawnsElement.getChildren("spawn")) {
        Spawn spawn = parseSpawn(match, spawnElement, spawnsElement);
        if (spawn != null) {
          IdModule.get().add(match, null, spawn, true);
          if (spawn.isDefaultSpawn()) {
            defaultPresent = true;
          }
        }
      }
    }
    if (!defaultPresent) {
      errors.add(new ModuleError(this, match.getMap(), new String[]{"No valid default spawn"}, true));
      return false;
    }
    StringBuilder builder = new StringBuilder().append("Spawns loaded : ")
        .append(IdModule.get().getList(match, Spawn.class).size());
    errors.add(new ModuleError(this, match.getMap(), new String[]{builder.toString()}, false));
    return true;
  }

  /**
   * Gets a default spawn based on given XML elements.
   *
   * @param match    The match.
   * @param elements The given elements.
   * @return The created default spawn.
   */
  private Spawn parseSpawn(Match match, Element... elements) {
    String defaultValue = ParseUtil.getFirstAttribute("default", elements);
    boolean defaultSpawn = defaultValue != null && Numbers.parseBoolean(defaultValue);

    String safeValue = ParseUtil.getFirstAttribute("safe", elements);
    boolean safe = safeValue != null && Numbers.parseBoolean(safeValue);

    String sequentialValue = ParseUtil.getFirstAttribute("sequential", elements);
    boolean sequential = sequentialValue != null && Numbers.parseBoolean(sequentialValue);

    String spreadValue = ParseUtil.getFirstAttribute("spread", elements);
    boolean spread = spreadValue != null && Numbers.parseBoolean(spreadValue);

    String exclusiveValue = ParseUtil.getFirstAttribute("exclusive", elements);
    boolean exclusive = exclusiveValue != null && Numbers.parseBoolean(exclusiveValue);

    String persistentValue = ParseUtil.getFirstAttribute("persistent", elements);
    boolean persistent = persistentValue != null && Numbers.parseBoolean(persistentValue);

    Team team = null;
    if (!defaultSpawn) {
      team = Cardinal.getModule(TeamModule.class).getTeamByName(match, ParseUtil.getFirstAttribute("team", elements));
    }

    String kitValue = ParseUtil.getFirstAttribute("kit", elements);
    Kit kit = kitValue != null ? Cardinal.getModule(KitModule.class).getKit(match, kitValue) : null;

    if (kit == null && kitValue != null) {
      Located spawn = (Located) elements[0];
      errors.add(new ModuleError(this, match.getMap(),
          new String[]{"Invalid kit specified for spawn",
              "Element at " + spawn.getLine() + ", " + spawn.getColumn()}, false));
    }

    List<Region> regions = Lists.newArrayList();

    Region spawnRegion = getPointProvider(match, "region", true, elements);
    if (spawnRegion != null) {
      regions.add(spawnRegion);
    }
    for (Element regionElement : elements[0].getChildren("region")) {
      Region region = getPointProvider(match, "id", false, ParseUtil.addElement(regionElement, elements));
      if (region != null) {
        regions.add(region);
      }
    }
    if (regions.size() == 0) {
      Located located = (Located) elements[0];
      errors.add(new ModuleError(this, match.getMap(), new String[]{
          "No regions specified for spawn at line " + located.getLine() + ", column " + located.getColumn(),
      }, false));
      // Prevent errors later trying to get a spawn region from an empty list.
      return null;
    }
    return new Spawn(defaultSpawn, team, safe, sequential, spread, exclusive, persistent, kit, regions);
  }

  private PointProviderRegion getPointProvider(Match match, String attr, boolean allowMissing, Element... elements) {
    Located located = (Located) elements[0];
    String attribute = ParseUtil.getFirstAttribute(attr, elements);
    if (attribute == null) {
      if (!allowMissing) {
        errors.add(new ModuleError(this, match.getMap(), new String[]{
            "Missing \"" + attr + "\" attribute for spawn region",
            "Element at line " + located.getLine() + ", column " + located.getColumn()
        }, false));
      }
      return null;
    }
    Region region = Cardinal.getModule(RegionModule.class).getRegionById(match, attribute);
    if (region == null) {
      errors.add(new ModuleError(this, match.getMap(), new String[]{
          "Invalid region specified for a spawn",
          "Element at line " + located.getLine() + ", column " + located.getColumn()
      }, false));
      return null;
    }
    if (!region.isRandomizable()) {
      errors.add(new ModuleError(this, match.getMap(), new String[]{
          "Region specified for spawn spawn must be randomizable",
          "Element at line " + located.getLine() + ", column " + located.getColumn()
      }, false));
      return null;
    }

    String rawAngle = ParseUtil.getFirstAttribute("angle", elements);
    if (rawAngle != null) {
      Vector angle = Numbers.getVector(rawAngle);
      if (angle == null) {
        errors.add(new ModuleError(this, match.getMap(), new String[]{
            "Invalid angle attribute specified",
            "Element at line " + located.getLine() + ", column " + located.getColumn()
        }, false));
        return null;
      }
      return new PointProviderRegion(region, angle);
    }

    float yaw = Numbers.parseFloat(ParseUtil.getFirstAttribute("yaw", elements), 0);
    float pitch = Numbers.parseFloat(ParseUtil.getFirstAttribute("pitch", elements), 0);

    return new PointProviderRegion(region, yaw, pitch);
  }

  /**
   * Spawns the player in the appropriate default spawn location.
   *
   * @param event The event.
   */
  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerInitialSpawn(PlayerInitialSpawnEvent event) {
    Match match = Cardinal.getMatch(event.getPlayer());
    event.setSpawnLocation(getDefaultSpawn(match).getSpawnPoint());
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Spawn spawn = getDefaultSpawn(Cardinal.getMatch(event.getPlayer()));
    Bukkit.getPluginManager().callEvent(new CardinalRespawnEvent(event.getPlayer(), spawn));
  }

  /**
   * Spawns a player when they change teams, if the match is running or if the player just joined the server.
   *
   * @param event The event.
   */
  @EventHandler
  public void onPlayerChangeTeam(PlayerContainerChangeStateEvent event) {
    Player player = event.getPlayer();
    MatchThread newThread = event.getNewData().getMatchThread();
    if (newThread != null && newThread.getCurrentMatch().isRunning()) {
      Match match = newThread.getCurrentMatch();
      if (event.getNewData().getPlaying() != null) {
        Spawn spawn = ListUtil.getRandom(getSpawns(match, event.getNewData().getPlaying()));
        Bukkit.getPluginManager().callEvent(new CardinalRespawnEvent(player, spawn));
      } else {
        Bukkit.getPluginManager().callEvent(new CardinalRespawnEvent(player, getDefaultSpawn(match)));
      }
    }
  }

  /**
   * Spawn all players when the match starts.
   *
   * @param event The event.
   */
  @EventHandler
  public void onMatchStart(MatchChangeStateEvent event) {
    if (event.getState() == MatchState.PLAYING) {
      for (Player player : event.getMatch()) {
        Spawn spawn = ListUtil.getRandom(getSpawns(event.getMatch(), event.getMatch().getPlayingContainer(player)));
        Bukkit.getPluginManager().callEvent(new CardinalRespawnEvent(player, spawn));
      }
    }
  }

  /**
   * Called when a match loads. Teleports players in the thread to the new world.
   *
   * @param event The event.
   */
  @EventHandler
  public void onMatchLoad(MatchLoadCompleteEvent event) {
    event.getMatch().getMatchThread().getPlayers().forEach(player -> {
      CardinalRespawnEvent respawn = new CardinalRespawnEvent(player, getDefaultSpawn(event.getMatch()));
      Bukkit.getPluginManager().callEvent(respawn);
    });
  }

  /**
   * Spawns a player appropriately.
   *
   * @param event The event.
   */
  @EventHandler
  public void onCardinalRespawn(CardinalRespawnEvent event) {
    Players.reset(event.getPlayer());
    Players.setPlaying(event.getPlayer());
    if (event.getSpawn().getKit() != null) {
      event.getSpawn().getKit().apply(event.getPlayer(), false);
    }
    event.getPlayer().teleport(event.getSpawn().getSpawnPoint());
  }

  /**
   * Gets the default spawn from the currently loaded spawns.
   *
   * @return The default spawn.
   */
  @NonNull
  private Spawn getDefaultSpawn(@NonNull Match match) {
    for (Spawn spawn : IdModule.get().getList(match, Spawn.class)) {
      if (spawn.isDefaultSpawn()) {
        return spawn;
      }
    }
    // This should never happen as the match will not load without a default spawn. But just in case, use world spawn.
    return new Spawn(true, null, false, false, false, false, false, null,
        Lists.newArrayList(new PointRegion(match, match.getWorld().getSpawnLocation())));
  }

  /**
   * Gets the location of the default spawn.
   * @param match The match.
   */
  @NonNull
  public Location getDefaultSpawnLocation(@NonNull Match match) {
    return getDefaultSpawn(match).getSpawnPoint();
  }

  /**
   * Gets a list of {@link Spawn} based on a team. If no spawn is found, spawns without a team will be returned.
   *
   * @param container The container for the spawns.
   * @return The list of spawns.
   */
  private List<Spawn> getSpawns(@NonNull Match match, @NonNull PlayingPlayerContainer container) {
    List<Spawn> results = IdModule.get().getList(match, Spawn.class).stream()
        .filter(spawn -> container.equals(spawn.getTeam())).collect(Collectors.toList());
    if (results.size() == 0) {
      results = IdModule.get().getList(match, Spawn.class).stream().filter(
          spawn -> !spawn.isDefaultSpawn() && spawn.getTeam() == null).collect(Collectors.toList());
    }
    return results;
  }

}
