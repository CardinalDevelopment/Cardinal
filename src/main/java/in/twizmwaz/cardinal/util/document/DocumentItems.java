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

package in.twizmwaz.cardinal.util.document;

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.module.kit.type.KitItem;
import in.twizmwaz.cardinal.util.Colors;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.ItemAttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jdom2.Element;

import java.util.List;
import java.util.UUID;

public class DocumentItems {

  public static KitItem.Item getKitItem(Element element) {
    ItemStack itemStack = getItem(element);
    int slot = -1;
    String slotString = element.getAttributeValue("slot", "-1");
    if (Numbers.isNumber(slotString)) {
      slot = Integer.parseInt(slotString);
    } else {
      if (!slotString.startsWith("slot.")) {
        slotString = "slot." + slotString;
        //TODO: get an int from the slot WITHOUT unnecessary NMS.
      }
    }
    return new KitItem.Item(itemStack, slot);
  }

  public static ItemStack getItem(Element element) {
    int amount = Numbers.parseInteger(element.getAttributeValue("amount", "1"));
    short damage = element.getAttributeValue("damage") != null
        ? Numbers.parseShort(element.getAttributeValue("damage"))
        : element.getText().split(":").length > 1
        ? Numbers.parseShort(element.getText().split(":")[1], (short) 0)
        : 0;
    ItemStack itemStack = new ItemStack(Material.AIR);
    if (element.getAttribute("material") != null) {
      itemStack = new ItemStack(Material.matchMaterial(element.getAttributeValue("material")), amount, damage);
    } else if (!element.getTextTrim().equals("")) {
      itemStack = new ItemStack(Material.matchMaterial(element.getText().split(":")[0]), amount, damage);
    }
    if (itemStack.getType() == Material.POTION) {
      itemStack = Potion.fromDamage(damage).toItemStack(amount);
    }
    if (element.getName().equalsIgnoreCase("book")) {
      itemStack = new ItemStack(Material.BOOK, amount, damage);
    }
    if (element.getAttributeValue("enchantment") != null) {
      for (String raw : element.getAttributeValue("enchantment").split(";")) {
        String[] enchant = raw.split(":");
        int lvl = enchant.length > 1 ? Numbers.parseInteger(enchant[1]) : 1;
        Enchantment enchantment = Enchantment.getByName(Strings.getTechnicalName(enchant[0]));
        if (enchantment == null) {
          //TODO: NMS enchantment names
        } else {
          itemStack.addUnsafeEnchantment(Enchantment.getByName(Strings.getTechnicalName(enchant[0])), lvl);
        }
      }
    }
    ItemMeta meta = itemStack.getItemMeta();
    if (Numbers.parseBoolean(element.getAttributeValue("unbreakable"))) {
      meta.setUnbreakable(true);
    }
    if (element.getAttributeValue("name") != null) {
      meta.setDisplayName(ChatColor.translateAlternateColorCodes('`', element.getAttributeValue("name")));
    }
    if (element.getAttributeValue("lore") != null) {
      List<String> lore = Lists.newArrayList();
      for (String raw : element.getAttributeValue("lore").split("\\|")) {
        String colored = ChatColor.translateAlternateColorCodes('`', raw);
        lore.add(colored);
      }
      meta.setLore(lore);
    }
    if (element.getAttributeValue("potions") != null) {
      for (PotionEffect effect : parseEffects(element.getAttributeValue("potions"))) {
        ((PotionMeta) meta).addCustomEffect(effect, true);
      }
    }
    if (element.getAttributeValue("attributes") != null) {
      for (ItemAttributeModifier attribute : parseAttributes((element.getAttributeValue("attributes")))) {
        meta.addAttributeModifier(attribute.getModifier().getName(), attribute);
      }
    }
    for (Element attribute : element.getChildren("attribute")) {
      meta.addAttributeModifier(attribute.getText(), getAttribute(attribute));
    }
    itemStack.setItemMeta(meta);
    if (element.getName().equalsIgnoreCase("book")) {
      BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
      bookMeta.setTitle(ChatColor.translateAlternateColorCodes('`', element.getChildText("author")));
      bookMeta.setAuthor(ChatColor.translateAlternateColorCodes('`', element.getChildText("author")));
      List<String> pages = Lists.newArrayList();
      for (Element page : element.getChild("pages").getChildren("page")) {
        pages.add(ChatColor.translateAlternateColorCodes('`', page.getText()).replace("\t", ""));
      }
      bookMeta.setPages(pages);
      itemStack.setItemMeta(bookMeta);
    }
    if (element.getAttributeValue("color") != null) {
      LeatherArmorMeta leatherMeta = (LeatherArmorMeta) itemStack.getItemMeta();
      leatherMeta.setColor(Colors.convertHexToRgb(element.getAttributeValue("color")));
      itemStack.setItemMeta(leatherMeta);
    }
    
    return applyMeta(itemStack, element); //TODO: Item mods
  }

  public static ItemStack applyMeta(ItemStack itemStack, Element element) {
    for (Element enchant : element.getChildren("enchantment")) {
      String ench = enchant.getText();
      Enchantment enchantment = Enchantment.getByName(Strings.getTechnicalName(ench));
      int lvl = Numbers.parseInteger(enchant.getAttributeValue("level"), 1);
      if (enchantment == null) {
        //TODO: NMS name check
      } else {
        itemStack.addUnsafeEnchantment(enchantment, lvl);
      }
    }
    ItemMeta meta = itemStack.getItemMeta();
    for (Element effect : element.getChildren("effect")) {
      PotionEffect potionEffect = getPotion(effect);
      if (!((PotionMeta) meta).getCustomEffects().contains(potionEffect)) {
        ((PotionMeta) meta).addCustomEffect(potionEffect, true);
      }
    }
    for (Element attribute : element.getChildren("attribute")) {
      ItemAttributeModifier itemAttribute = getAttribute(attribute);
      if (!meta.getModifiedAttributes().contains(attribute.getText())) {
        meta.addAttributeModifier(attribute.getText(), itemAttribute);
      }
    }
    /* TODO: can-destroy & can-place-on, and all attributes
     * @link https://docs.oc.tc/modules/item_mods#itemmeta
     */
    itemStack.setItemMeta(meta);
    return itemStack;
  }


  private static List<PotionEffect> parseEffects(String effects) {
    List<PotionEffect> effectList = Lists.newArrayList();
    for (String effect : effects.split(";")) {
      String[] split = effect.split(":");
      PotionEffectType type = PotionEffectType.getByName(Strings.getTechnicalName(split[0]));
      //TODO: NMS Inclusion
      effectList.add(new PotionEffect(type, Numbers.parseInteger(split[1]), Numbers.parseInteger(split[2])));
    }
    return effectList;
  }


  private static List<ItemAttributeModifier> parseAttributes(String attributes) {
    List<ItemAttributeModifier> list = Lists.newArrayList();
    for (String attribute : attributes.split(";")) {
      String[] attr = attribute.split(":");
      list.add(new ItemAttributeModifier(null,
          new AttributeModifier(UUID.randomUUID(), attr[0], Numbers.parseDouble(attr[2]), getOperation(attr[1]))));
    }
    return list;
  }

  public static PotionEffect getPotion(Element potion) {
    PotionEffectType type = PotionEffectType.getByName(Strings.getTechnicalName(potion.getText()));
    if (type == null) {
      //TODO: NMS potion types
    }
    int duration = Numbers.parseInteger(potion.getAttributeValue("duration")) == Integer.MAX_VALUE
        ? Numbers.parseInteger(potion.getAttributeValue("duration"))
        : Numbers.parseInteger(potion.getAttributeValue("duration")) * 20;
    int amplifier = 0;
    boolean ambient = Numbers.parseBoolean(potion.getAttributeValue("ambient"));
    if (potion.getAttributeValue("amplifier") != null) {
      amplifier = Numbers.parseInteger(potion.getAttributeValue("amplifier")) - 1;
    }

    return new PotionEffect(type, duration, amplifier, ambient);
  }

  private static ItemAttributeModifier getAttribute(Element attribute) {
    return new ItemAttributeModifier(getEquipmentSlot(attribute.getAttributeValue("slot", "")),
        new AttributeModifier(UUID.randomUUID(), attribute.getText(),
            Double.parseDouble(attribute.getAttributeValue("amount", "0.0")),
            getOperation(attribute.getAttributeValue("operation", "add"))));
  }

  public static AttributeModifier.Operation getOperation(String operation) {
    if (Numbers.isNumber(operation)) {
      return AttributeModifier.Operation.fromOpcode(Integer.parseInt(operation));
    } else {
      switch (operation.toLowerCase()) {
        case ("base"):
          return AttributeModifier.Operation.ADD_SCALAR;
        case ("multiply"):
          return AttributeModifier.Operation.MULTIPLY_SCALAR_1;
        case ("add"):
        default:
          return AttributeModifier.Operation.ADD_NUMBER;
      }
    }
  }

  private static EquipmentSlot getEquipmentSlot(String slotName) {
    if (!slotName.startsWith("slot.")) {
      slotName = "slot." + slotName;
    }
    EquipmentSlot equipmentSlot = null;
    String[] path = slotName.split("\\.");
    if (path.length == 3) {
      if (path[1].equalsIgnoreCase("armor")) {
        equipmentSlot = EquipmentSlot.valueOf(Strings.getTechnicalName(path[2]));
      } else if (path[1].equalsIgnoreCase("weapon")) {
        if (path[2].equalsIgnoreCase("mainhand")) {
          equipmentSlot = EquipmentSlot.HAND;
        }
        if (path[2].equalsIgnoreCase("offhand")) {
          equipmentSlot = EquipmentSlot.OFF_HAND;
        }
      }
    }
    return equipmentSlot;
  }

}
