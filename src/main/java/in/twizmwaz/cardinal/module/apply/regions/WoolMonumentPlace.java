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

package in.twizmwaz.cardinal.module.apply.regions;

import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import in.twizmwaz.cardinal.component.team.TeamComponentBuilder;
import in.twizmwaz.cardinal.module.apply.AppliedRegion;
import in.twizmwaz.cardinal.module.apply.ApplyType;
import in.twizmwaz.cardinal.module.filter.type.MaterialFilter;
import in.twizmwaz.cardinal.module.filter.type.TeamFilter;
import in.twizmwaz.cardinal.module.filter.type.modifiers.AllFilter;
import in.twizmwaz.cardinal.module.objective.wool.Wool;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.util.Channels;
import in.twizmwaz.cardinal.util.Components;
import in.twizmwaz.cardinal.util.MaterialPattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;


public class WoolMonumentPlace extends AppliedRegion {

  private final Team team;
  private final BaseComponent wrongTeam;
  private final boolean show;

  /**
   * Will create a wool monument applied region, that allows only the wool type to be placed by the correct team.
   * Custom messages for wrong team and wrong block will be sent to the player only if {@link Wool#isShow()};
   *
   * @param wool The wool to make the applied region for.
   */
  public WoolMonumentPlace(Wool wool) {
    super(ApplyType.BLOCK_PLACE, wool.getMonument(),
        new AllFilter(
            new MaterialFilter(
                MaterialPattern.getSingleMaterialPattern(Material.WOOL, (int) wool.getColor().getWoolData())),
            new TeamFilter(wool.getTeam())),
        new LocalizedComponentBuilder(
            ChatConstant.getConstant("objective.wool.error.block"),
            wool.getComponent()
        ).color(ChatColor.RED).build());
    team = wool.getTeam();
    wrongTeam = Components.getWarningComponent(new LocalizedComponentBuilder(
        ChatConstant.getConstant("objective.wool.error.team"),
        new TeamComponentBuilder(wool.getTeam()).build(),
        wool.getComponent()
    ).color(ChatColor.RED).build());
    show = wool.isShow();
  }

  @Override
  public void sendMessage(Player player) {
    if (!show) {
      return;
    }
    if (team.hasPlayer(player)) {
      super.sendMessage(player);
    } else {
      Channels.getPlayerChannel(player).sendMessage(wrongTeam);
    }
  }

}
