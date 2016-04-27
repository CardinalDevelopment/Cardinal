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

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Items {

  /**
   * Creates a new book item stack.
   *
   * @param amount The quanity requested.
   * @param name The name of of the item.
   * @param author The author to be used for the book.
   * @return The completed item stack.
   */
  public static ItemStack createBook(int amount, String name, String author) {
    ItemStack item = new ItemStackBuilder().amount(amount).build();
    BookMeta meta = (BookMeta) item.getItemMeta();
    meta.setAuthor(author);
    meta.setPages(Lists.newArrayList());
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Creates a new piece of leather armor.
   *
   * @param material The material that represents the armor.
   * @param name A custom set name for the armor.
   * @param lore Custom set lore for the armor.
   * @param color The color of the armor may be null.
   * @return The completed item stack.
   */
  public static ItemStack createLeatherArmor(@NonNull Material material, String name, String[] lore, Color color) {
    ItemStack item = new ItemStackBuilder().type(material).name(name).lore(lore).build();
    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
    if (color != null) {
      meta.setColor(color);
    }
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Tests if two items are effectively the same.
   *
   * @param item1 The first item to be tested.
   * @param item2 The second item to be tested.
   * @return If the two objects are equal.
   */
  public static boolean itemsEqual(ItemStack item1, ItemStack item2) {
    if (!item1.getType().equals(item2.getType())) {
      return false;
    }
    return toMaxDurability(item1).isSimilar(toMaxDurability(item2));
  }

  /**
   * Repairs an item to maximum durability.
   *
   * @param item The item to be repaired.
   * @return The item with reset durability.
   */
  public static ItemStack toMaxDurability(ItemStack item) {
    ItemStack item2 = item.clone();
    item2.setDurability((short) 0);
    return item2;
  }

}