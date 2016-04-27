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

package in.twizmwaz.cardinal.module.kit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.filter.Filter;
import in.twizmwaz.cardinal.module.filter.FilterModule;
import in.twizmwaz.cardinal.module.kit.type.KitArmor;
import in.twizmwaz.cardinal.module.kit.type.KitAttribute;
import in.twizmwaz.cardinal.module.kit.type.KitClear;
import in.twizmwaz.cardinal.module.kit.type.KitCluster;
import in.twizmwaz.cardinal.module.kit.type.KitDoubleJump;
import in.twizmwaz.cardinal.module.kit.type.KitFly;
import in.twizmwaz.cardinal.module.kit.type.KitGameMode;
import in.twizmwaz.cardinal.module.kit.type.KitHealth;
import in.twizmwaz.cardinal.module.kit.type.KitItem;
import in.twizmwaz.cardinal.module.kit.type.KitKnockback;
import in.twizmwaz.cardinal.module.kit.type.KitPotion;
import in.twizmwaz.cardinal.module.kit.type.KitWalkSpeed;
import in.twizmwaz.cardinal.util.ArmorType;
import in.twizmwaz.cardinal.util.Colors;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.Strings;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ModuleEntry
public class KitModule extends AbstractModule {

  public KitModule() {
    this.depends = new Class[]{FilterModule.class};
  }

  @Getter
  private final Map<Match, Map<String, Kit>> kits = Maps.newHashMap();

  @Override
  public boolean loadMatch(Match match) {
    kits.put(match, Maps.newHashMap());
    Map<String, Kit> matchKits = kits.get(match);
    for (Element kits : match.getMap().getDocument().getRootElement().getChildren("kits")) {
      for (Element element : kits.getChildren("kit")) {
        Map.Entry<String, Kit> entry = parseKit(match, element, true);
        matchKits.put(entry.getKey(), entry.getValue());
      }
    }

    return true;
  }

  private Map.Entry<String, Kit> parseKit(Match match, Element element, boolean forceParse) {
    if (element.getName().equalsIgnoreCase("kit") || forceParse) {
      List<Kit> kits = Lists.newArrayList();
      String name = null;
      if (element.getAttributeValue("name") != null) {
        name = element.getAttributeValue("name");
      }
      if (element.getAttributeValue("id") != null) {
        name = element.getAttributeValue("id");
      }
      for (Map.Entry<String, Kit> kitPair : this.kits.get(match).entrySet()) {
        if (kitPair.getKey().equalsIgnoreCase(name)) {
          return kitPair;
        }
      }

      boolean clear = element.getChildren("clear").size() > 0;
      boolean clearItems = element.getChildren("clear-items").size() > 0;
      if (clear || clearItems) {
        kits.add(new KitClear(clear, clearItems));
      }

      Set<KitItem.Item> items = Sets.newHashSet();
      items.addAll(element.getChildren("item").stream().map(KitModule::getKitItem).collect(Collectors.toList()));
      if (!items.isEmpty()) {
        kits.add(new KitItem(items));
      }

      Set<KitArmor.Armor> armor = Sets.newHashSet();
      List<Element> armors = Lists.newArrayList();
      Lists.newArrayList(ArmorType.values()).forEach(type -> armors.addAll(element.getChildren(type.name())));
      for (Element piece : armors) {
        ItemStack itemStack = getItem(piece);
        ArmorType type = ArmorType.valueOf(piece.getName());
        Boolean locked = Numbers.parseBoolean(element.getAttributeValue("locked"));
        armor.add(new KitArmor.Armor(itemStack, type, locked));
      }
      if (!armor.isEmpty()) {
        kits.add(new KitArmor(armor));
      }

      if (element.getChildText("game-mode") != null) {
        GameMode gameMode = GameMode.valueOf(element.getChildText("game-mode").toUpperCase());
        if (gameMode != null) {
          kits.add(new KitGameMode(gameMode));
        }
      }
      int health = Numbers.parseInteger(element.getChildText("health"), -1);
      int foodLevel = Numbers.parseInteger(element.getChildText("foodlevel"), -1);
      float saturation = Numbers.parseInteger(element.getChildText("saturation"), 0);
      if (health != -1 || foodLevel != -1 || saturation != 0) {
        kits.add(new KitHealth(health, foodLevel, saturation));
      }

      List<PotionEffect> potions = Lists.newArrayList();
      potions.addAll(element.getChildren("potion").stream().map(KitModule::getPotion).collect(Collectors.toList()));
      potions.addAll(element.getChildren("effect").stream().map(KitModule::getPotion).collect(Collectors.toList()));
      if (!potions.isEmpty()) {
        kits.add(new KitPotion(potions));
      }

      Set<AttributeModifier> attributes = Sets.newHashSet();
      attributes.addAll(element.getChildren("attribute").stream().map(attr -> new AttributeModifier(
          UUID.randomUUID(), attr.getText(),
          Double.parseDouble(attr.getAttributeValue("amount", "0.0")),
          getOperation(attr.getAttributeValue("operation", "add")))).collect(Collectors.toList()));
      if (!attributes.isEmpty()) {
        kits.add(new KitAttribute(attributes));
      }


      if (element.getChildText("walk-speed") != null) {
        kits.add(new KitWalkSpeed(Float.parseFloat(element.getChildText("walk-speed")) / 5));
      }
      if (element.getChildText("knockback-reduction") != null) {
        kits.add(new KitKnockback(Float.parseFloat(element.getChildText("knockback-reduction"))));
      }

      for (Element jump : element.getChildren("double-jump")) {
        boolean enabled = Numbers.parseBoolean(jump.getAttributeValue("enabled"), true);
        int power = Numbers.parseInteger(jump.getAttributeValue("power"), 3);
        double rechargeTime = Strings.timeStringToExactSeconds(jump.getAttributeValue("recharge-time", "2.5s"));
        boolean rechargeBeforeLand = Numbers.parseBoolean(jump.getAttributeValue("recharge-before-landing"), false);
        kits.add(new KitDoubleJump(new KitDoubleJump.DoubleJumpData(enabled, power, rechargeTime, rechargeBeforeLand)));
      }

      for (Element jump : element.getChildren("fly")) {
        boolean canFly = Numbers.parseBoolean(jump.getAttributeValue("can-fly"), true);
        boolean flying = Numbers.parseBoolean(jump.getAttributeValue("flying"), false);
        float flySpeed = Float.parseFloat(jump.getAttributeValue("fly-speed", "1")) / 10F;
        kits.add(new KitFly(canFly, flying, flySpeed));
      }

      Filter filter = Cardinal.getModule(FilterModule.class)
          .getFilter(match, element.getAttributeValue("filter", "always"));
      String parent = element.getAttributeValue("parents", "");
      List<String> parents = Lists.newArrayList();
      for (String parentPart : parent.split(",")) {
        parents.add(parentPart.trim());
      }
      boolean force = Numbers.parseBoolean(element.getAttributeValue("force"), false);
      boolean potionParticles = Numbers.parseBoolean(element.getAttributeValue("potion-particles"), false);
      boolean discardPotionBottles = Numbers.parseBoolean(element.getAttributeValue("discard-potion-bottles"), true);
      boolean resetPearls = Numbers.parseBoolean(element.getAttributeValue("reset-ender-pearls"), false);
      return new AbstractMap.SimpleEntry<>(name,
          new KitCluster(match, filter, force, potionParticles, discardPotionBottles, resetPearls, parents, kits));
    } else {
      return parseKit(match, element.getParentElement(), true);
    }
  }

  private static KitItem.Item getKitItem(Element element) {
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

  private static ItemStack getItem(Element element) {
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

    return itemStack; //TODO: Item mods
  }

  private static ItemStack applyMeta(ItemStack itemStack, Element element) {
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

  private static PotionEffect getPotion(Element potion) {
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

  private static AttributeModifier.Operation getOperation(String operation) {
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
