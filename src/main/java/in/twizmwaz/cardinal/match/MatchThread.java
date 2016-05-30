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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import in.twizmwaz.cardinal.event.player.PlayerJoinMatchThreadEvent;
import in.twizmwaz.cardinal.event.player.PlayerQuitMatchThreadEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * A MatchThread is an object representing a linear string of matches.
 * This has nothing to do with a {@link java.lang.Thread}.
 */
public class MatchThread {

  private static int counter = -1;

  @Getter
  private final int id;
  private final Collection<Player> players;
  @Getter
  @Setter
  private Match currentMatch;

  public MatchThread() {
    id = counter++;
    players = Sets.newHashSet();
  }

  public void addPlayer(@NonNull Player player) {
    players.add(player);
    Bukkit.getPluginManager().callEvent(new PlayerJoinMatchThreadEvent(player, this));
  }

  public void removePlayer(@NonNull Player player) {
    players.remove(player);
    Bukkit.getPluginManager().callEvent(new PlayerQuitMatchThreadEvent(player, this));
  }

  public boolean hasPlayer(@NonNull Player player) {
    return players.contains(player);
  }

  public Collection<Player> getPlayers() {
    return ImmutableSet.copyOf(players);
  }

}
