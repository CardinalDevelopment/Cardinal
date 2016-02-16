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

package in.twizmwaz.cardinal.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class Players {

  /**
   * @param name The player's name containing modifiers.
   * @return The matched OfflinePlayer based on the modifiers.
   */
  @Nullable
  public static OfflinePlayer match(@Nonnull String name) {
    if (name.startsWith("@")) {
      return Bukkit.getOfflinePlayer(name.substring(1));
    } else if (name.startsWith("#")) {
      return Bukkit.getOfflinePlayer(UUID.fromString(name.substring(1)));
    }
    Player player = Bukkit.getPlayer(name);
    if (player != null) {
      return player;
    }
    return Bukkit.getOfflinePlayer(name);
  }

  /**
   * @param player The player to be reset.
   */
  public static void reset(@Nonnull Player player) {
    player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    player.setExhaustion(0);
    player.setExp(0);
    player.setFireTicks(0);
    player.setFlySpeed(0.1F);
    player.setFoodLevel(20);
    player.setHealth(player.getMaxHealth());
    player.getInventory().clear();
    player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
    player.setRemainingAir(player.getMaximumAir());
    player.setSaturation(0);
    player.setTotalExperience(0);
    player.setWalkSpeed(0.2F);
  }

}
