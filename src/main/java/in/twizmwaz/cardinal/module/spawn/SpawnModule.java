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
import in.twizmwaz.cardinal.event.match.MatchStartEvent;
import in.twizmwaz.cardinal.event.player.CardinalRespawnEvent;
import in.twizmwaz.cardinal.event.player.PlayerChangeTeamEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionException;
import in.twizmwaz.cardinal.module.region.RegionModule;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.module.team.TeamModule;
import in.twizmwaz.cardinal.util.ListUtil;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.ParseUtil;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.located.Located;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ModuleEntry
public class SpawnModule extends AbstractModule implements Listener {

  private Map<Match, List<Spawn>> spawns = Maps.newHashMap();

  /**
   * Default constructor to create the module.
   */
  public SpawnModule() {
    this.depends = new Class[]{TeamModule.class/*, KitModule.class, FilterModule.class*/, RegionModule.class};
    Cardinal.registerEvents(this);
  }

  @Override
  public boolean loadMatch(@NonNull Match match) {
    List<Spawn> spawns = Lists.newArrayList();
    Document document = match.getMap().getDocument();
    boolean defaultPresent = false;
    for (Element spawnsElement : document.getRootElement().getChildren("spawns")) {
      Element defaultElement = spawnsElement.getChild("default");
      if (defaultElement != null) {
        Spawn defaultSpawn = parseDefaultSpawn(match, defaultElement, spawnsElement);
        if (defaultSpawn != null) {
          spawns.add(defaultSpawn);
          defaultPresent = true;
        }
      }
    }
    if (!defaultPresent) {
      errors.add(new ModuleError(this, match.getMap(), new String[]{"No valid default spawn"}, true));
      return false;
    }
    this.spawns.put(match, spawns);
    return true;
  }

  /**
   * Gets a default spawn based on given XML elements.
   *
   * @param match    The match.
   * @param elements The given elements.
   * @return The created default spawn.
   */
  private Spawn parseDefaultSpawn(Match match, Element... elements) {
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
            new String[]{RegionModule.getRegionError(e, "region", "default spawn"),
                "Element at " + located.getLine() + ", " + located.getColumn()}, false));
        continue;
      }
      if (region == null) {
        errors.add(new ModuleError(this, match.getMap(),
            new String[]{"Invalid region specified for default spawn",
                "Element at " + located.getLine() + ", " + located.getColumn()}, false));
        continue;
      }
      if (!region.isRandomizable()) {
        errors.add(new ModuleError(this, match.getMap(),
            new String[]{"Region specified for default spawn must be randomizable",
                "Element at " + located.getLine() + ", " + located.getColumn()}, false));
        continue;
      }
      regions.add(region);
    }

    return new Spawn(true, null, safe, sequential, spread, exclusive, persistent, regions);
  }

  /**
   * Spawns the player in the appropriate default spawn location.
   *
   * @param event The event.
   */
  @EventHandler
  public void onPlayerInitialSpawn(PlayerInitialSpawnEvent event) {
    List<Region> regions = getDefaultSpawn().getRegions();
    event.setSpawnLocation(ListUtil.getRandom(regions).getRandomPoint().toLocation(
        Cardinal.getInstance().getMatchThread().getCurrentMatch().getWorld()));
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Bukkit.getPluginManager().callEvent(new CardinalRespawnEvent(event.getPlayer()));
  }

  /**
   * Spawns a player when they change teams, if the match is running or if the player just joined the server.
   *
   * @param event The event.
   */
  @EventHandler
  public void onPlayerChangeTeam(PlayerChangeTeamEvent event) {
    if (Cardinal.getInstance().getMatchThread().getCurrentMatch().isRunning()) {
      Bukkit.getPluginManager().callEvent(new CardinalRespawnEvent(event.getPlayer()));
    }
  }

  /**
   * Spawn all players when the match starts.
   *
   * @param event The event.
   */
  @EventHandler
  public void onMatchStart(MatchStartEvent event) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      Team team = Team.getTeam(player);
      if (team == null || !Team.isObservers(team)) {
        Bukkit.getPluginManager().callEvent(new CardinalRespawnEvent(player));
      }
    }
  }

  /**
   * Spawns a player appropriately.
   *
   * @param event The event.
   */
  @EventHandler
  public void onCardinalRespawn(CardinalRespawnEvent event) {
    Player player = event.getPlayer();
    Team team = Team.getTeam(player);
    if (team == null || !Team.isObservers(team)) {
      player.setGameMode(GameMode.SURVIVAL);
      if (Cardinal.getInstance().getMatchThread().getCurrentMatch().isRunning()) {
        List<Spawn> spawns = getSpawns(team);
        List<Region> regions = ListUtil.getRandom(spawns).getRegions();
        player.teleport(ListUtil.getRandom(regions).getRandomPoint().toLocation(
            Cardinal.getInstance().getMatchThread().getCurrentMatch().getWorld()));
      } else {
        List<Region> regions = getDefaultSpawn().getRegions();
        player.teleport(ListUtil.getRandom(regions).getRandomPoint().toLocation(
            Cardinal.getInstance().getMatchThread().getCurrentMatch().getWorld()));
      }
    } else {
      player.setGameMode(GameMode.CREATIVE);

      List<Region> regions = getDefaultSpawn().getRegions();
      player.teleport(ListUtil.getRandom(regions).getRandomPoint().toLocation(
          Cardinal.getInstance().getMatchThread().getCurrentMatch().getWorld()));
    }
  }

  /**
   * Gets the default spawn from the currently loaded spawns.
   *
   * @return The default spawn.
   */
  @NonNull
  public Spawn getDefaultSpawn() {
    for (Spawn spawn : spawns.get(Cardinal.getInstance().getMatchThread().getCurrentMatch())) {
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
   * @param team The team for the spawns.
   * @return The list of spawns.
   */
  private List<Spawn> getSpawns(Team team) {
    return spawns.get(Cardinal.getInstance().getMatchThread().getCurrentMatch()).stream()
        .filter(spawn -> (team == null && spawn.getTeam() == null) || spawn.getTeam().equals(team))
        .collect(Collectors.toList());
  }

}
