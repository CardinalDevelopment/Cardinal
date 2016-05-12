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

package in.twizmwaz.cardinal.module.team;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.NameComponent;
import ee.ellytr.chat.component.formattable.LocalizedComponent;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.component.TeamComponent;
import in.twizmwaz.cardinal.event.player.PlayerChangeTeamEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.core.Core;
import in.twizmwaz.cardinal.module.objective.destroyable.Destroyable;
import in.twizmwaz.cardinal.module.objective.wool.Wool;
import in.twizmwaz.cardinal.util.Channels;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Team implements Iterable<Player> {

  private final String id;
  private final ChatColor color;
  private final ChatColor overheadColor;
  private final boolean plural;
  private final NameTagVisibility nameTagVisibility;
  private final int min;
  private final int max;
  private final int maxOverfill;

  private final Set<Player> players = Sets.newHashSet();

  private String name;

  @Override
  public String toString() {
    return "Team{id=\"" + id + "\"}";
  }

  /**
   * Attempts to add a player to a team.
   *
   * @param player  The player.
   * @param force   If the join is being forced.
   * @param message If a join message should be sent to the player.
   * @return If the attempt was successful.
   */
  public boolean addPlayer(Player player, boolean force, boolean message) {
    //TODO: blitz check
    if (!force && players.size() >= max) {
      //TODO: send team full message
      return false;
    }
    PlayerChangeTeamEvent event = new PlayerChangeTeamEvent(player, getTeam(player), this);
    Bukkit.getServer().getPluginManager().callEvent(event);
    if (message && event.getNewTeam() != null) {
      Channels.getPlayerChannel(player).sendMessage(new NameComponent(player));
      Channels.getPlayerChannel(player).sendMessage(new LocalizedComponent(ChatConstant.getConstant("team.join"),
          new TeamComponent(event.getNewTeam())));
    }
    if (!event.isCancelled()) {
      if (event.getOldTeam() != null) {
        event.getOldTeam().removePlayer(player);
      }
      players.add(player);
    }
    return !event.isCancelled() || force;
  }

  /**
   * Removes a player form the team.
   *
   * @param player The player to be removed from the team.
   */
  public void removePlayer(Player player) {
    players.remove(player);
  }

  /**
   * Used to get a snapshot of a list of players on a team.
   *
   * @return An immutable set of players.
   */
  public Set<Player> getPlayers() {
    return ImmutableSet.copyOf(players);
  }

  @Override
  public Iterator<Player> iterator() {
    return players.iterator();
  }

  /**
   * Shorthand for getting the teams of the current match.
   *
   * @return The teams from the current match.
   */
  public static Set<Team> getTeams(@NonNull Match match) {
    return Cardinal.getModule(TeamModule.class).getTeams(match);
  }

  /**
   * Gets the least full team (relative) of a set of teams.
   *
   * @param teams The set of teams.
   * @return The least full team.
   */
  public static Team getEmptiestTeam(@NonNull Set<Team> teams) {
    Team emptiestTeam = null;
    double emptiestFill = Integer.MAX_VALUE;
    for (Team team : teams) {
      double fill = (double) team.getPlayers().size() / team.getMax();
      if (fill < emptiestFill) {
        emptiestTeam = team;
        emptiestFill = fill;
      }
    }
    return emptiestTeam;
  }

  public static ChatColor getTeamColor(@NonNull Player player) {
    Team team = getTeam(player);
    return team != null ? team.getColor() : ChatColor.YELLOW;
  }

  /**
   * Returns the team that the specified player is on.
   *
   * @param player The player.
   * @return The team that the player is on.
   */
  public static Team getTeam(@NonNull Player player) {
    for (Team team : getTeams(Cardinal.getMatch(player))) {
      if (team.getPlayers().contains(player)) {
        return team;
      }
    }
    return null;
  }

  /**
   * Shorthand for getting a team by an ID.
   *
   * @param id The ID.
   * @return The team with the specified ID.
   */
  public static Team getTeamById(@NonNull Match match, @NonNull String id) {
    return Cardinal.getModule(TeamModule.class).getTeamById(match, id);
  }

  /**
   * Gets the currently observing team.
   *
   * @return The observing team.
   */
  public static Team getObservers(@NonNull Match match) {
    for (Team team : getTeams(match)) {
      if (isObservers(team)) {
        return team;
      }
    }
    return null;
  }

  /**
   * Test if a team is an observer team.
   *
   * @param team The team to be tested.
   * @return If the team is an observer team.
   */
  public static boolean isObservers(@NonNull Team team) {
    return team.getId().equals("observers");
  }

  /**
   * Gets the objectives required for a team to complete.
   *
   * @param team The team.
   * @return The team's objectives.
   */
  public static List<Objective> getTeamObjectives(@NonNull Match match, @NonNull Team team) {
    List<Objective> objectives = Lists.newArrayList();
    objectives.addAll(Objective.getObjectives(match).stream().filter(objective -> objective instanceof Core
        && !((Core) objective).getTeam().equals(team)).collect(Collectors.toList()));
    objectives.addAll(Objective.getObjectives(match).stream().filter(objective -> objective instanceof Destroyable
        && !((Destroyable) objective).getOwner().equals(team)).collect(Collectors.toList()));
    objectives.addAll(Objective.getObjectives(match).stream().filter(objective -> objective instanceof Wool
        && ((Wool) objective).getTeam().equals(team)).collect(Collectors.toList()));
    return objectives;
  }
}
