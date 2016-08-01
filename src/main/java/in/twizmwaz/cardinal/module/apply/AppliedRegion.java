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

package in.twizmwaz.cardinal.module.apply;

import ee.ellytr.chat.component.formattable.UnlocalizedComponent;
import in.twizmwaz.cardinal.module.filter.Filter;
import in.twizmwaz.cardinal.module.filter.FilterState;
import in.twizmwaz.cardinal.module.kit.Kit;
import in.twizmwaz.cardinal.module.kit.KitRemovable;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionBounds;
import in.twizmwaz.cardinal.util.Channels;
import in.twizmwaz.cardinal.util.Components;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AppliedRegion implements Region, Filter, KitRemovable {

  @Getter
  private final ApplyType type;
  private final Region region;
  private final Filter filter;

  private final Kit kit;
  private final Vector velocity;
  @Getter
  private final BaseComponent message;
  @Getter
  private final boolean earlyWarning;

  public AppliedRegion(ApplyType type, Region region, Filter filter, String message) {
    this(type, region, filter, message, false);
  }

  public AppliedRegion(ApplyType type, Region region, Filter filter, String message, boolean earlyWarning) {
    this(type, region, filter, message == null ? null : new UnlocalizedComponent(message), earlyWarning);
  }

  public AppliedRegion(ApplyType type, Region region, Filter filter, BaseComponent message) {
    this(type, region, filter, message, false);
  }

  public AppliedRegion(ApplyType type, Region region, Filter filter, BaseComponent message, boolean earlyWarning) {
    this(type, region, filter, null, null, message == null ? null : Components.getWarningComponent(message),
        earlyWarning);
  }

  public AppliedRegion(ApplyType type, Region region, Filter filter, Kit kit) {
    this(type, region, filter, kit, null, null, false);
  }

  public AppliedRegion(Region region, Filter filter, Vector velocity) {
    this(ApplyType.VELOCITY, region, filter, null, velocity, null, false);
  }

  public boolean isType(ApplyType type) {
    return this.type.equals(type);
  }

  /**
   * Sends the message for this region to the player as long as both message and player aren't null.
   * @param player The player to send the message to.
   */
  public void sendMessage(Player player) {
    if (message != null && player != null) {
      Channels.getPlayerChannel(player).sendMessage(message);
    }
  }

  /**
   * Applies the effects in this region (kits or velocity) to a player if the filter allows it.
   * @param player The player to apply effect on.
   * @param objects The object to pass to the filter.
   */
  public void applyEffects(Player player, Object... objects) {
    if (evaluate(objects).toBoolean()) {
      if (isType(ApplyType.VELOCITY)) {
        player.applyImpulse(velocity);
      } else if (kit != null) {
        kit.apply(player, false);
      }
    }
  }

  /* KitRemovable interface */
  @Override
  public void apply(Player player, boolean force) {
    kit.apply(player, force);
  }

  @Override
  public void remove(Player player) {
    if (isType(ApplyType.KIT_LEND) && kit instanceof KitRemovable) {
      ((KitRemovable) kit).remove(player);
    }
  }

  /* Filter interface */
  @Override
  public FilterState evaluate(Object... objects) {
    return filter.evaluate(objects);
  }

  /* Region interface */
  @Override
  public boolean contains(Vector vector) {
    return region.contains(vector);
  }

  @Override
  public boolean isRandomizable() {
    return region.isRandomizable();
  }

  @Override
  public boolean isBounded() {
    return region.isBounded();
  }

  @Override
  public RegionBounds getBounds() {
    return region.getBounds();
  }

  @Override
  public Collection<Block> getBlocks() {
    return region.getBlocks();
  }

  @Override
  public Vector getRandomPoint() {
    return region.getRandomPoint();
  }

}
