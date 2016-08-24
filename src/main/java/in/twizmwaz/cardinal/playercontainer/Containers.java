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

package in.twizmwaz.cardinal.playercontainer;

import in.twizmwaz.cardinal.event.player.PlayerContainerChangeStateEvent;
import in.twizmwaz.cardinal.module.team.SinglePlayerContainer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Containers {

  /**
   * Handles when a player is transferred from one container data to another.
   *
   * @param player The player being transferred.
   * @param oldData The old container data.
   * @param newData The new container data.
   */
  public static void handleStateChangeEvent(Player player, PlayerContainerData oldData, PlayerContainerData newData) {
    PlayerContainerChangeStateEvent event = new PlayerContainerChangeStateEvent(player, oldData, newData);
    Bukkit.getPluginManager().callEvent(event);
    if (!event.isCancelled()) {
      switchPlayer(oldData.getMatchThread(), newData.getMatchThread(), player);
      switchPlayer(oldData.getMatch(), newData.getMatch(), player);
      if (oldData.getMatch() == newData.getMatch()) {
        switchPlayer(oldData.getPlaying(), newData.getPlaying(), player);
      } else {
        // If match is not the same, removing the player from the match already kicked the player from the team.
        addPlayer(newData.getPlaying(), player);
      }
    }
  }

  private static void switchPlayer(PlayerContainer oldContainer, PlayerContainer newContainer, Player player) {
    if (oldContainer != newContainer) {
      if (oldContainer != null && oldContainer.hasPlayer(player)) {
        oldContainer.removePlayer(player);
      }
      addPlayer(newContainer, player);
    }
  }

  private static void addPlayer(PlayerContainer newContainer, Player player) {
    if (newContainer != null && !(newContainer instanceof SinglePlayerContainer)) {
      newContainer.addPlayer(player);
    }
  }

}
