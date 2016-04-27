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
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemStackBuilder {

  private Material material = Material.AIR;
  private int amount = 1;
  private short durability = 0;
  private String name = null;
  private List<String> lore = Lists.newArrayList();
  private Map<Enchantment, Integer> enchantments = Maps.newHashMap();

  public ItemStackBuilder type(Material material) {
    this.material = material;
    return this;
  }

  public ItemStackBuilder amount(int amount) {
    this.amount = amount;
    return this;
  }

  public ItemStackBuilder durability(short durability) {
    this.durability = durability;
    return this;
  }

  public ItemStackBuilder name(String name) {
    this.name = name;
    return this;
  }

  public ItemStackBuilder lore(String... lore) {
    this.lore.addAll(Arrays.asList(lore));
    return this;
  }

  public ItemStackBuilder enchantment(Enchantment enchantment, int level) {
    enchantments.put(enchantment, level);
    return this;
  }

  /**
   * Uses the provided parameters to construct a new {@link ItemStack}.
   *
   * @return The completed {@link ItemStack}.
   */
  public ItemStack build() {
    ItemStack item = new ItemStack(material, amount, durability);
    ItemMeta meta = item.getItemMeta();
    if (name != null) {
      meta.setDisplayName(name);
    }
    if (!lore.isEmpty()) {
      meta.setLore(lore);
    }
    for (Enchantment enchantment : enchantments.keySet()) {
      item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment));
    }
    item.setItemMeta(meta);
    return item;
  }

}