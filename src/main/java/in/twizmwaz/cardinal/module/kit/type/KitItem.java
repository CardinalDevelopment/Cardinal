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

package in.twizmwaz.cardinal.module.kit.type;

import com.google.common.collect.Sets;
import in.twizmwaz.cardinal.module.kit.Kit;
import in.twizmwaz.cardinal.util.Items;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
import java.util.Set;

@AllArgsConstructor
public class KitItem implements Kit {

  private final Set<Item> items;

  @Override
  public void apply(Player player, boolean force) {
    PlayerInventory inventory = player.getInventory();
    Set<Item> items = cloneItems(this.items);

    // Remove kit items that the player already has
    for (ItemStack item : inventory.getContents()) {
      if (item != null) {
        ItemStack itemClone = item.clone();
        for (Item item2 : items) {
          if (Items.itemsEqual(item2.getItem(), item) && !(item2.getItem().getDurability() < item.getDurability())) {
            int remove = itemClone.getAmount();
            if (item2.getItem().getAmount() < remove) {
              remove = item2.getItem().getAmount();
            }
            itemClone.setAmount(itemClone.getAmount() - remove);
            item2.getItem().setAmount(item2.getItem().getAmount() - remove);
          }
        }
      }
    }

    for (Item item : items) {
      ItemStack kitItem = item.getItem();
      if (!item.hasSlot()) {
        inventory.addItem(kitItem);
        continue;
      }
      if (force) {
        setPlayerItem(player, item.getSlot(), kitItem);
      } else {
        // Repair tools
        if (kitItem.getAmount() > 0 && kitItem.getType().getMaxDurability() > 0) {
          for (ItemStack item2 : inventory.getContents()) {
            if (item2 == null) {
              continue;
            }
            if (Items.itemsEqual(kitItem, item2) && kitItem.getDurability() < item2.getDurability()) {
              item2.setDurability(kitItem.getDurability());
              kitItem.setAmount(0);
              break;
            }
          }
        }
        // Stack items
        if (kitItem.getAmount() > 0) {
          for (ItemStack item2 : inventory.getContents()) {
            if (item2 != null && kitItem.getAmount() != 0
                && Items.itemsEqual(kitItem, item2) && item2.getAmount() < item2.getMaxStackSize()) {
              int max = Math.min(item2.getMaxStackSize() - item2.getAmount(), kitItem.getAmount());
              item2.setAmount(item2.getAmount() + max);
              kitItem.setAmount(kitItem.getAmount() - max);
            }
          }
        }
        // Put item in slot or give item
        if (kitItem.getAmount() > 0) {
          if (inventory.getItem(item.getSlot()) == null) {
            setPlayerItem(player, item.getSlot(), kitItem);
          } else {
            inventory.addItem(kitItem);
          }
        }
      }
    }

  }

  private static Set<Item> cloneItems(Collection<Item> items) {
    Set<Item> clone = Sets.newHashSet();
    for (Item item : items) {
      clone.add(item.getCopy());
    }
    return clone;
  }


  private static void setPlayerItem(Player player, int slot, ItemStack item) {
    player.getInventory().setItem(slot, item);
  }

  @Data
  public static class Item {

    private final ItemStack item;
    private final int slot;

    public boolean hasSlot() {
      return getSlot() != -1;
    }

    Item getCopy() {
      return new Item(item.clone(), this.slot);
    }

  }

}