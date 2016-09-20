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

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.filter.Filter;
import in.twizmwaz.cardinal.module.id.IdModule;
import in.twizmwaz.cardinal.module.kit.Kit;
import in.twizmwaz.cardinal.module.kit.KitRemovable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class KitCluster implements KitRemovable {

  private final Match match;

  private final Filter filter;
  private final boolean force;
  private final boolean potionParticles;
  private final boolean discardPotionBottles;
  private final boolean resetEnderPearls;

  private final List<String> parentsRaw;
  private final List<Kit> children;

  private final List<Kit> parents = Lists.newArrayList();

  @Override
  public void apply(Player player, boolean force) {
    evaluateParents();
    if (filter.evaluate(player).toBoolean()) {
      for (Kit kit : parents) {
        kit.apply(player, force || this.force);
      }
      for (Kit kit : children) {
        kit.apply(player, force || this.force);
      }
      player.setPotionParticles(potionParticles);
      if (discardPotionBottles) {
        player.getInventory().remove(Material.GLASS_BOTTLE);
      }
      if (resetEnderPearls) {
        Cardinal.getMatch(player).getWorld().getEntities().stream().filter(entity -> entity instanceof EnderPearl)
            .forEach(entity -> {
              EnderPearl enderPearl = (EnderPearl) entity;
              if (player.equals(enderPearl.getShooter())) {
                enderPearl.setShooter(null);
              }
            });
      }
    }
  }

  @Override
  public void remove(Player player) {
    parents.stream().filter(kit -> kit instanceof KitRemovable).forEach(kit -> ((KitRemovable) kit).remove(player));
    children.stream().filter(kit -> kit instanceof KitRemovable).forEach(kit -> ((KitRemovable) kit).remove(player));
  }

  private void evaluateParents() {
    if (parentsRaw != null) {
      parentsRaw.forEach(parent -> {
        Kit kit = IdModule.get().get(match, parent, Kit.class);
        if (kit != null) {
          parents.add(kit);
        }
      });
      parentsRaw.clear();
    }
  }

}