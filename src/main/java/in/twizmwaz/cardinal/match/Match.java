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

package in.twizmwaz.cardinal.match;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import in.twizmwaz.cardinal.event.match.MatchChangeStateEvent;
import in.twizmwaz.cardinal.module.group.groups.CompetitorGroup;
import in.twizmwaz.cardinal.module.repository.LoadedMap;
import in.twizmwaz.cardinal.module.team.SinglePlayerGroup;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.module.group.groups.PlayerGroup;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Getter
public final class Match implements PlayerGroup {

  private static int matchCounter = -1;

  @Getter
  private final MatchThread matchThread;
  private final UUID uuid;
  private final LoadedMap map;
  private final World world;
  private final Set<Player> players;
  private final Set<CompetitorGroup> competitors;

  private final int matchNumber;

  private MatchState state;

  /**
   * Creates a new Match.
   *
   * @param matchThread The {@link MatchThread} that this match will occur on.
   * @param uuid        The unique id of this match.
   * @param map         The {@link LoadedMap} this match will occur on.
   */
  public Match(@NonNull MatchThread matchThread, @NonNull UUID uuid, @NonNull LoadedMap map, @NonNull World world) {
    this.matchThread = matchThread;
    this.uuid = uuid;
    this.map = map;
    this.world = world;
    players = Sets.newHashSet();
    competitors = Sets.newHashSet();
    this.matchNumber = matchCounter++;

    state = MatchState.WAITING;
  }

  public boolean isRunning() {
    return state.equals(MatchState.PLAYING);
  }

  /**
   * Sets the match state.
   *
   * @param state The state to try and set.
   * @return The final match state.
   */
  public MatchState setMatchState(MatchState state) {
    MatchChangeStateEvent event = new MatchChangeStateEvent(this, state);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      return this.state;
    } else {
      this.state = state;
      return state;
    }
  }

  /**
   * Checks if any of the player containers in the match are teams, determining if a match is FFA or not.
   *
   * @return If the match is a free-for-all.
   */
  public boolean isFfa() {
    for (CompetitorGroup container : competitors) {
      if (container instanceof Team) {
        return false;
      }
    }
    return true;
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
  public void addPlayer(@NonNull Player player) {
    players.add(player);
    if (isFfa()) {
      competitors.add(SinglePlayerGroup.of(player));
    }
  }

  @Override
  public void removePlayer(@NonNull Player player) {
    CompetitorGroup container = getPlayingContainer(player);
    players.remove(player);
    if (isFfa()) {
      competitors.remove(container);
    } else {
      container.removePlayer(player);
    }
  }

  @Override
  public Iterator<Player> iterator() {
    return players.iterator();
  }

  /**
   * Gets the {@link CompetitorGroup} of a player in the match.
   *
   * @param player The player.
   * @return The container of the player.
   */
  public CompetitorGroup getPlayingContainer(@NonNull Player player) {
    if (!players.contains(player)) {
      throw new IllegalArgumentException("Cannot get CompetitorGroup of player not in match");
    } else {
      for (CompetitorGroup container : competitors) {
        if (container.hasPlayer(player)) {
          return container;
        }
      }
      throw new IllegalStateException("Player is in match but is missing a CompetitorGroup.");
    }
  }
}
