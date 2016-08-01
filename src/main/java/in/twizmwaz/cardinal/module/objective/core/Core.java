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

package in.twizmwaz.cardinal.module.objective.core;

import com.google.common.collect.Lists;
import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.formattable.LocalizedComponent;
import ee.ellytr.chat.component.formattable.UnlocalizedComponent;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.apply.AppliedModule;
import in.twizmwaz.cardinal.module.apply.AppliedRegion;
import in.twizmwaz.cardinal.module.apply.ApplyType;
import in.twizmwaz.cardinal.module.filter.FilterModule;
import in.twizmwaz.cardinal.module.filter.FilterState;
import in.twizmwaz.cardinal.module.filter.type.TeamFilter;
import in.twizmwaz.cardinal.module.filter.type.modifiers.TransformFilter;
import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.ProximityMetric;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.type.FiniteBlockRegion;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.util.MaterialPattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.AbstractMap;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Core extends Objective implements Listener {

  private static final MaterialPattern lavaPattern = new MaterialPattern(
          new AbstractMap.SimpleEntry<>(Material.LAVA, MaterialPattern.ANY_DATA_VALUE),
          new AbstractMap.SimpleEntry<>(Material.STATIONARY_LAVA, MaterialPattern.ANY_DATA_VALUE));

  private final String name;
  private final FiniteBlockRegion region;
  private final int leak;
  private final MaterialPattern material;
  private final Team owner;
  private final boolean modeChanges;
  private final ProximityMetric proximityMetric;
  private final boolean proximityHorizontal;

  private final List<Player> touchedPlayers = Lists.newArrayList();

  @Setter
  private boolean touched;
  private boolean complete;

  /**
   * @param match               The match the core is part of.
   * @param id                  The core's ID, for usage in code and XML.
   * @param name                The core's name, for usage by the user.
   * @param required            Determines if this objective is required to win the match.
   * @param region              The region that contains this core.
   * @param leak                The distance required for the lava to be from the core in order to be leaked.
   * @param material            The material that the core is made out of.
   * @param owner               The owner that owns this core.
   * @param modeChanges         Determines if this core follows mode changes.
   * @param show                Determines if this core shows on the scoreboard.
   * @param proximityMetric     The proximity metric for proximity tracking of this core.
   * @param proximityHorizontal Determines if only horizontal distance is considered when
   *                            calculating proximity.
   */
  public Core(Match match, String id, String name, boolean required, Region region, int leak,
              MaterialPattern material, Team owner, boolean modeChanges,
              boolean show, ProximityMetric proximityMetric, boolean proximityHorizontal) {
    super(match, id, required, show);
    this.name = name;
    this.leak = leak;
    this.material = material;
    this.owner = owner;
    this.modeChanges = modeChanges;
    this.proximityMetric = proximityMetric;
    this.proximityHorizontal = proximityHorizontal;

    AppliedModule appliedModule = Cardinal.getModule(AppliedModule.class);
    appliedModule.add(match,
        new AppliedRegion(ApplyType.BLOCK, FiniteBlockRegion.getFromMaterialPattern(match, region, lavaPattern),
            FilterModule.DENY, (String) null),
        true);
    this.region = FiniteBlockRegion.getFromMaterialPattern(match, region, material);
    appliedModule.add(match,
        new AppliedRegion(ApplyType.BLOCK_BREAK, this.region,
            new TransformFilter(new TeamFilter(owner), FilterState.DENY, FilterState.DENY, FilterState.ALLOW),
            new LocalizedComponent(ChatConstant.getConstant("objective.core.error.own")),
            true),
        true);
  }

  @Override
  public UnlocalizedComponent getComponent() {
    return new UnlocalizedComponent(name);
  }

}
