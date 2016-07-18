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
import in.twizmwaz.cardinal.playercontainer.PlayingPlayerContainer;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Iterator;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SinglePlayerContainer implements PlayingPlayerContainer {

  @NonNull
  private final Player player;

  @Override
  public ImmutableCollection<Player> getPlayers() {
    return ImmutableSet.of(player);
  }

  @Override
  public boolean hasPlayer(Player player) {
    return this.player == player;
  }

  @Override
  public void addPlayer(Player player) {
    throw new IllegalArgumentException("Cannot add additional players to a SinglePlayerContainer");
  }

  @Override
  public void removePlayer(Player player) {
    throw new IllegalArgumentException("Cannot remove players to a SinglePlayerContainer");
  }

  @Override
  public Iterator<Player> iterator() {
    return ImmutableSet.of(player).iterator();
  }

  public static SinglePlayerContainer of(@NonNull Player player) {
    return new SinglePlayerContainer(player);
  }

}
