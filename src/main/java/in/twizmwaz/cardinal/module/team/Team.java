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
import com.google.common.collect.Sets;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.event.player.PlayerChangeTeamEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Set;

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
      //TODO: join message
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
  public static Set<Team> getTeams() {
    return Cardinal.getModule(TeamModule.class).getTeams(Cardinal.getInstance().getMatchThread().getCurrentMatch());
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
    for (Team team : getTeams()) {
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
  public static Team getTeamById(@NonNull String id) {
    return Cardinal.getModule(TeamModule.class)
        .getTeamById(Cardinal.getInstance().getMatchThread().getCurrentMatch(), id);
  }

  /**
   * Gets the currently observing team.
   *
   * @return The observing team.
   */
  public static Team getObservers() {
    for (Team team : getTeams()) {
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
}
