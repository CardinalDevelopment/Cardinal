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
import com.google.common.collect.Sets;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.filter.Filter;
import in.twizmwaz.cardinal.module.filter.FilterModule;
import in.twizmwaz.cardinal.module.id.IdModule;
import in.twizmwaz.cardinal.module.kit.listener.DoubleJumpListener;
import in.twizmwaz.cardinal.module.kit.listener.ShieldListener;
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
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.Strings;
import in.twizmwaz.cardinal.util.document.DocumentItems;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jdom2.Element;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ModuleEntry(depends = {IdModule.class, FilterModule.class})
public class KitModule extends AbstractModule {

  public KitModule() {
    Cardinal.registerEvents(new DoubleJumpListener());
    Cardinal.registerEvents(new ShieldListener());
  }

  @Override
  public boolean loadMatch(Match match) {
    for (Element kits : match.getMap().getDocument().getRootElement().getChildren("kits")) {
      for (Element element : kits.getChildren("kit")) {
        Map.Entry<String, Kit> entry = parseKit(match, element);
        IdModule.get().add(match, entry.getKey(), entry.getValue());
      }
    }
    return true;
  }

  public Kit getKit(@NonNull Match match, @NonNull String id) {
    return IdModule.get().get(match, id, Kit.class);
  }

  private Map.Entry<String, Kit> parseKit(Match match, Element element) {
    List<Kit> kits = Lists.newArrayList();
    String name = null;
    if (element.getAttributeValue("name") != null) {
      name = element.getAttributeValue("name");
    }
    if (element.getAttributeValue("id") != null) {
      name = element.getAttributeValue("id");
    }
    for (Map.Entry<String, Kit> kitPair : IdModule.get().getMap(match, Kit.class).entrySet()) {
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
    items.addAll(element.getChildren("item").stream().map(DocumentItems::getKitItem).collect(Collectors.toList()));
    if (!items.isEmpty()) {
      kits.add(new KitItem(items));
    }
    Set<KitArmor.Armor> armor = Sets.newHashSet();
    List<Element> armors = Lists.newArrayList();
    Lists.newArrayList(ArmorType.values()).forEach(type -> armors.addAll(element.getChildren(type.name())));
    for (Element piece : armors) {
      ItemStack itemStack = DocumentItems.getItem(piece);
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
    potions.addAll(element.getChildren("potion").stream().map(DocumentItems::getPotion).collect(Collectors.toList()));
    potions.addAll(element.getChildren("effect").stream().map(DocumentItems::getPotion).collect(Collectors.toList()));
    if (!potions.isEmpty()) {
      kits.add(new KitPotion(potions));
    }
    Set<AttributeModifier> attributes = Sets.newHashSet();
    attributes.addAll(element.getChildren("attribute").stream().map(attr -> new AttributeModifier(
        UUID.randomUUID(), attr.getText(),
        Double.parseDouble(attr.getAttributeValue("amount", "0.0")),
        DocumentItems.getOperation(attr.getAttributeValue("operation", "add")))).collect(Collectors.toList()));
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
    Filter filter =
        Cardinal.getModule(FilterModule.class).getFilter(match, element.getAttributeValue("filter", "always"));
    String parent = element.getAttributeValue("parents", (String) null);
    List<String> parents = Lists.newArrayList();
    if (parent != null) {
      for (String parentPart : parent.split(",")) {
        if (!parentPart.equals("")) {
          parents.add(parentPart.trim());
        }
      }
      if (parents.size() == 0) {
        parents = null;
      }
    }
    boolean force = Numbers.parseBoolean(element.getAttributeValue("force"), false);
    boolean potionParticles = Numbers.parseBoolean(element.getAttributeValue("potion-particles"), false);
    boolean discardPotionBottles = Numbers.parseBoolean(element.getAttributeValue("discard-potion-bottles"), true);
    boolean resetPearls = Numbers.parseBoolean(element.getAttributeValue("reset-ender-pearls"), false);
    return new AbstractMap.SimpleEntry<>(name,
        new KitCluster(match, filter, force, potionParticles, discardPotionBottles, resetPearls, parents, kits));
  }

}
