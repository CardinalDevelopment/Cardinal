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

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.OwnedObjective;
import in.twizmwaz.cardinal.module.objective.wool.Wool;
import in.twizmwaz.cardinal.playercontainer.PlayingPlayerContainer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//TODO: blitz check, send team full message

@Data
@AllArgsConstructor
public class Team implements PlayingPlayerContainer {

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

  @Override
  public void addPlayer(@NonNull Player player) {
    players.add(player);
  }

  @Override
  public void removePlayer(Player player) {
    players.remove(player);
  }

  @Override
  public ImmutableCollection<Player> getPlayers() {
    return ImmutableSet.copyOf(players);
  }

  @Override
  public boolean hasPlayer(@NonNull Player player) {
    return players.contains(player);
  }

  @Override
  public Iterator<Player> iterator() {
    return players.iterator();
  }

  public String getCompleteName() {
    return color + name;
  }

  /**
   * Shorthand for getting the teams of the current match.
   *
   * @return The teams from the current match.
   */
  public static List<Team> getTeams(@NonNull Match match) {
    return Cardinal.getModule(TeamModule.class).getTeams(match);
  }

  /**
   * Gets the least full team (relative) of a set of teams.
   *
   * @param teams The set of teams.
   * @return The least full team.
   */
  public static Team getEmptiestTeam(@NonNull List<Team> teams) {
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
    PlayingPlayerContainer container = Cardinal.getMatch(player).getPlayingContainer(player);
    return container instanceof Team ? ((Team) container).getColor() : ChatColor.YELLOW;
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
   * Shorthand for getting a team by it's name.
   *
   * @param name The name.
   * @return The team with the specified name.
   */
  public static Team getTeamByName(@NonNull Match match, @NonNull String name) {
    return Cardinal.getModule(TeamModule.class).getTeamByName(match, name);
  }

  /**
   * Gets the objectives required for a team to complete.
   *
   * @param team The team.
   * @return The team's objectives.
   */
  public static List<Objective> getTeamObjectives(@NonNull Match match, @NonNull Team team) {
    List<Objective> objectives = Lists.newArrayList();
    objectives.addAll(Objective.getObjectives(match).stream().filter(objective ->
        (objective instanceof OwnedObjective && !((OwnedObjective) objective).getOwner().equals(team))
        || (objective instanceof Wool && ((Wool) objective).getTeam().equals(team))).collect(Collectors.toList()));
    return objectives;
  }

  /**
   * Gets the objectives shown in the scoreboard for a team.
   *
   * @param team The team.
   * @return The team's shown objectives.
   */
  public static List<Objective> getTeamShownObjectives(@NonNull Match match, @NonNull Team team) {
    return getTeamObjectives(match, team).stream().filter(Objective::isShow).collect(Collectors.toList());
  }

}
