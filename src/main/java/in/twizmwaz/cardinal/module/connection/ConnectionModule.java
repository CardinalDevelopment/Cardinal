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

package in.twizmwaz.cardinal.module.connection;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.event.player.PlayerChangeGroupEvent;
import in.twizmwaz.cardinal.module.AbstractListenerModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.group.groups.GroupData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@ModuleEntry
public class ConnectionModule extends AbstractListenerModule {

  /**
   * Handles new container data when the player initially joins the server.
   *
   * @param event The event.
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerJoin(PlayerInitialSpawnEvent event) {
    Bukkit.getPluginManager().callEvent(
        new PlayerChangeGroupEvent(
            event.getPlayer(),
            new GroupData(Cardinal.getDefaultMatchThread(), null, null),
            GroupData.empty()
        )
    );
  }

  /**
   * Handles old container data when the player leaves the server.
   *
   * @param event The event.
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    Bukkit.getPluginManager().callEvent(new PlayerChangeGroupEvent(
        player,
        GroupData.of(player),
        GroupData.empty()
    ));
  }

}
