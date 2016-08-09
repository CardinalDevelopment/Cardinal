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

package in.twizmwaz.cardinal.module.itemrepair;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.util.Items;
import in.twizmwaz.cardinal.util.MaterialType;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.List;

@RequiredArgsConstructor
public class ItemRepairHandler implements Listener {

  private final Match match;
  private final List<MaterialType> types;

  /**
   * Checks if the item being picked up should repair with any items in the player's inventory.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    Player player = event.getPlayer();
    if (match.hasPlayer(player)) {
      Item item = event.getItem();
      ItemStack itemStack = item.getItemStack();
      MaterialData data = itemStack.getData();

      boolean repair = false;
      for (MaterialType type : types) {
        if (type.isType(data)) {
          // When an item should be repaired
          repair = true;
        }
      }

      if (repair) {
        // When an item is being repaired
        for (ItemStack content : player.getInventory().getContents()) {
          if (content != null) {
            if (Items.equalsIgnoreDurability(itemStack, content)) {
              int usesLeft = itemStack.getType().getMaxDurability() - itemStack.getDurability();
              content.setDurability((short) (content.getDurability() - usesLeft));

              event.setCancelled(true);
              item.remove();
              player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
            }
          }
        }
      }
    }
  }

}
