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

package in.twizmwaz.cardinal.module.kit.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import in.twizmwaz.cardinal.module.kit.type.KitDoubleJump;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerOnGroundEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DoubleJumpListener implements Listener {

  @Getter
  private static final Map<UUID, KitDoubleJump.DoubleJumpData> players = Maps.newHashMap();
  @Getter
  private static final List<UUID> landed = Lists.newArrayList();

  /**
   * Checks to add players in the landed map.
   *
   * @param event The event called.
   */
  @EventHandler
  public void onPlayerGround(PlayerOnGroundEvent event) {
    UUID id = event.getPlayer().getUniqueId();
    KitDoubleJump.DoubleJumpData data = players.get(id);
    if (event.getOnGround() && data != null && data.isEnabled()
        && !data.isRechargeBeforeLanding() && !landed.contains(id)) {
      landed.add(id);
    }
  }

  /**
   * Cancels the player from flying and adds to the player's velocity.
   *
   * @param event The event called.
   */
  @EventHandler
  public void onPlayerToggleFly(PlayerToggleFlightEvent event) {
    Player player = event.getPlayer();
    UUID id = player.getUniqueId();
    KitDoubleJump.DoubleJumpData data = players.get(id);
    if (data != null && data.isEnabled() && players.containsKey(player.getUniqueId())
        && player.getExp() <= 1.0f && event.isFlying()) {
      player.setAllowFlight(false);
      player.setExp(0.0f);
      event.setCancelled(true);

      Vector normal = player.getEyeLocation().getDirection();
      normal.setY(0.75 + Math.max(normal.getY() * 0.5, 0));
      normal.multiply(data.getPower() / 2);
      event.getPlayer().setVelocity(normal);

      player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 0.5f, 1.8f);
    }

  }

}
