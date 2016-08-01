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
import com.google.common.collect.Maps;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.event.match.MatchChangeStateEvent;
import in.twizmwaz.cardinal.event.match.MatchLoadCompleteEvent;
import in.twizmwaz.cardinal.event.player.CardinalRespawnEvent;
import in.twizmwaz.cardinal.event.player.PlayerContainerChangeStateEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.match.MatchState;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionException;
import in.twizmwaz.cardinal.module.region.RegionModule;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.module.team.TeamModule;
import in.twizmwaz.cardinal.playercontainer.PlayingPlayerContainer;
import in.twizmwaz.cardinal.util.ListUtil;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.ParseUtil;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.located.Located;

import java.util.List;
import java.util.Map;

@ModuleEntry(depends = {TeamModule.class/*, KitModule.class, FilterModule.class*/, RegionModule.class})
public class SpawnModule extends AbstractModule implements Listener {

  private Map<Match, List<Spawn>> spawns = Maps.newHashMap();

  /**
   * Default constructor to create the module.
   */
  public SpawnModule() {
    Cardinal.registerEvents(this);
  }

  @Override
  public boolean loadMatch(@NonNull Match match) {
    List<Spawn> spawns = Lists.newArrayList();
    Document document = match.getMap().getDocument();
    boolean defaultPresent = false;
    for (Element spawnsElement : document.getRootElement().getChildren("spawns")) {
      for (Element spawnElement : spawnsElement.getChildren("spawn")) {
        Spawn spawn = parseSpawn(match, spawnElement, spawnsElement);
        spawns.add(spawn);
        if (spawn.isDefaultSpawn()) {
          defaultPresent = true;
        }
      }
    }
    if (!defaultPresent) {
      errors.add(new ModuleError(this, match.getMap(), new String[]{"No valid default spawn"}, true));
      return false;
    }
    this.spawns.put(match, spawns);
    StringBuilder builder = new StringBuilder().append("Spawns loaded : ").append(spawns.size());
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

    List<Region> regions = Lists.newArrayList();
    List<Element> working;
    if (elements[0].getChild("regions") == null) {
      working = elements[0].getChildren();
    } else {
      working = elements[0].getChild("regions").getChildren();
    }
    for (Element regionElement : working) {
      Located located = (Located) regionElement;
      Region region;
      try {
        region = Cardinal.getModule(RegionModule.class).getRegion(match, regionElement);
      } catch (RegionException e) {
        errors.add(new ModuleError(this, match.getMap(),
            new String[]{RegionModule.getRegionError(e, "region", (defaultSpawn ? "default" : team.getName()) + " spawn"),
                "Element at " + located.getLine() + ", " + located.getColumn()}, false));
        continue;
      }
      if (region == null) {
        errors.add(new ModuleError(this, match.getMap(),
            new String[]{"Invalid region specified for a spawn",
                "Element at " + located.getLine() + ", " + located.getColumn()}, false));
        continue;
      }
      if (!region.isRandomizable()) {
        errors.add(new ModuleError(this, match.getMap(),
            new String[]{"Region specified for " + (defaultSpawn ? "default" : team.getName()) + " spawn must be randomizable",
                "Element at " + located.getLine() + ", " + located.getColumn()}, false));
        continue;
      }
      regions.add(region);
    }

    return new Spawn(defaultSpawn, team, safe, sequential, spread, exclusive, persistent, regions);
  }

  /**
   * Spawns the player in the appropriate default spawn location.
   *
   * @param event The event.
   */
  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerInitialSpawn(PlayerInitialSpawnEvent event) {
    Match match = Cardinal.getMatch(event.getPlayer());
    List<Region> regions = getDefaultSpawn(match).getRegions();
    event.setSpawnLocation(ListUtil.getRandom(regions).getRandomPoint().toLocation(match.getWorld()));
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
    if (event.getNewData().getMatchThread().getCurrentMatch().isRunning()) {
      Match match = Cardinal.getMatch(event.getPlayer());
      Spawn spawn = ListUtil.getRandom(getSpawns(match, event.getNewData().getPlaying()));
      Bukkit.getPluginManager().callEvent(new CardinalRespawnEvent(player, spawn));
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
    Player player = event.getPlayer();
    List<Region> regions = event.getSpawn().getRegions();
    player.teleport(ListUtil.getRandom(regions).getRandomPoint().toLocation(player.getWorld()));
  }

  /**
   * Gets the default spawn from the currently loaded spawns.
   *
   * @return The default spawn.
   */
  @NonNull
  public Spawn getDefaultSpawn(@NonNull Match match) {
    for (Spawn spawn : spawns.get(match)) {
      if (spawn.isDefaultSpawn()) {
        return spawn;
      }
    }
    // This should never happen as the match will not load without a default spawn.
    return null;
  }

  /**
   * Gets a list of {@link Spawn} based on a team.
   *
   * @param container The container for the spawns.
   * @return The list of spawns.
   */
  private List<Spawn> getSpawns(@NonNull Match match, @NonNull PlayingPlayerContainer container) {
    List<Spawn> results = Lists.newArrayList();

    for (Spawn spawn : spawns.get(match)) {
      if (container.equals(spawn.getTeam())) {
        results.add(spawn);
      }
    }
    return results;
  }

}
