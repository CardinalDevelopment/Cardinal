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

import in.twizmwaz.cardinal.match.MatchThread;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public class SpawnHandler implements Listener {

  @Getter
  private final MatchThread matchThread;

  /**
   * Sets player spawn location when the first join.
   *
   * @param event The event called.
   */
  @EventHandler(priority = EventPriority.LOW)
  public void onJoin(PlayerInitialSpawnEvent event) {
    //TODO: actually find the spawn and spawn them there
    event.getSpawnLocation().setWorld(matchThread.getCurrentMatch().getWorld());
    event.getSpawnLocation().setX(0);
    event.getSpawnLocation().setY(32);
    event.getSpawnLocation().setZ(0);
  }

  /**
   * Sets up the player when they first join. Should be replaced with obs module in the future.
   *
   * @param event The event called.
   */
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    event.getPlayer().setGameMode(GameMode.CREATIVE);
    event.getPlayer().setFlying(true);
  }

}
