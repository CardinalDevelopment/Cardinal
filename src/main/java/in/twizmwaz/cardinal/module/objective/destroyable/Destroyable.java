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

package in.twizmwaz.cardinal.module.objective.destroyable;

import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.NameComponent;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.chat.component.formattable.ListComponent;
import ee.ellytr.chat.component.formattable.LocalizedComponent;
import ee.ellytr.chat.component.formattable.UnlocalizedComponent;
import in.twizmwaz.cardinal.component.TeamComponent;
import in.twizmwaz.cardinal.event.objective.ObjectiveCompleteEvent;
import in.twizmwaz.cardinal.event.objective.ObjectiveTouchEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.ProximityMetric;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.playercontainer.PlayingPlayerContainer;
import in.twizmwaz.cardinal.util.Channels;
import in.twizmwaz.cardinal.util.Components;
import in.twizmwaz.cardinal.util.MaterialPattern;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class Destroyable extends Objective implements Listener {

  private final String name;
  private final Region region;
  private final MaterialPattern materials;
  private final Team owner;
  private final double completion;
  private final boolean modeChanges;
  private final boolean showProgress;
  private final boolean repairable;
  private final boolean sparks;
  private final ProximityMetric proximityMetric;
  private final boolean proximityHorizontal;

  private final List<Player> touchedPlayers = new ArrayList<>();
  private final Map<UUID, Integer> playerContributions = new HashMap<>();

  private int broken;
  private long total;

  private boolean completed;

  /**
   * @param match               The match the destroyable belongs to.
   * @param id                  This destroyable's ID.
   * @param name                This destroyable's name.
   * @param required            Determines if this objective is required to win the match.
   * @param region              The region that contains this destroyable.
   * @param materials           The materials that make up this destroyable.
   * @param owner               The owner of this destroyable.
   * @param completion          The percentage that this monument needs to be broken to be consider complete.
   * @param modeChanges         Determines if this destroyable follows mode changes.
   * @param showProgress        Determines if the progress of this destroyable is shown on the scoreboard.
   * @param repairable          Determines if this destroyable can be repaired.
   * @param sparks              Determines if sparks show when part of the destroyable is broken.
   * @param show                Determines if this destroyable is shown on the scoreboard.
   * @param proximityMetric     The proximity metric that determines how proximity is calculated.
   * @param proximityHorizontal Determines if only horizontal distance is considered when
   *                            calculating proximity.
   */
  public Destroyable(Match match, String id, String name, boolean required, Region region,
                     MaterialPattern materials, Team owner,
                     double completion, boolean modeChanges, boolean showProgress,
                     boolean repairable, boolean sparks, boolean show,
                     ProximityMetric proximityMetric, boolean proximityHorizontal) {
    super(match, id, required, show);
    this.name = name;
    this.region = region;
    this.materials = materials;
    this.owner = owner;
    this.completion = completion;
    this.modeChanges = modeChanges;
    this.showProgress = showProgress;
    this.repairable = repairable;
    this.sparks = sparks;
    this.proximityMetric = proximityMetric;
    this.proximityHorizontal = proximityHorizontal;

    total = region.getBlocks().stream().filter(this::isPartOf).count();
  }

  private boolean isPartOf(@NonNull Block block) {
    return materials.contains(block.getType(), (int) block.getState().getMaterialData().getData());
  }

  /**
   * Checks the destroyable's state when a player breaks a block.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    if (match.hasPlayer(player)) {
      Block block = event.getBlock();
      if (isPartOf(block)) {
        PlayingPlayerContainer team = match.getPlayingContainer(player);
        if (team != null && !team.equals(owner)) {
          if (!touchedPlayers.contains(player)) {
            touchedPlayers.add(player);

            if (show) {
              //fixme
              /*BaseComponent message = Components.getTeamComponent(team,
                  new LocalizedComponent(ChatConstant.getConstant("objective.destroyable.touched"),
                      new TeamComponent(owner),
                      new UnlocalizedComponent(name),
                      new NameComponent(player)
                  )
              );
              team.forEach(teamMember -> {
                if (teamMember.equals(player)) {
                  Channels.getPlayerChannel(player).sendMessage(
                      new LocalizedComponent(ChatConstant.getConstant("objective.destroyable.touched.self"))
                  );
                } else {
                  Channels.getPlayerChannel(teamMember).sendMessage(message);
                }
              });*/
            }
          }
          broken++;

          UUID uuid = player.getUniqueId();
          playerContributions.putIfAbsent(uuid, 0);
          playerContributions.put(uuid, playerContributions.get(uuid) + 1);

          if (!completed) {
            if ((double) broken / total >= completion) {
              completed = true;

              Channels.getGlobalChannel(match.getMatchThread()).sendMessage(
                  new LocalizedComponentBuilder(
                      ChatConstant.getConstant("objective.destroyable.completed"),
                      new TeamComponent(owner),
                      new UnlocalizedComponent(name),
                      getContributionList()
                  ).build()
              );

              Bukkit.getPluginManager().callEvent(new ObjectiveCompleteEvent(this, player));
            } else {
              Bukkit.getPluginManager().callEvent(new ObjectiveTouchEvent(this, player));
            }
          }
        } else {
          event.setCancelled(true);
          Channels.getPlayerChannel(player).sendMessage(Components.getWarningComponent(
              new LocalizedComponent(ChatConstant.getConstant("objective.destroyable.error.own"))
          ));
        }
      }
    }
  }

  private ListComponent getContributionList() {
    List<BaseComponent> contributions = new ArrayList<>();
    playerContributions.forEach((player, amount) -> {
      contributions.add(
          new UnlocalizedComponent("{0} (" + amount + "%)", new NameComponent(Bukkit.getOfflinePlayer(player)))
      );
    });
    return new ListComponent(contributions);
  }

  @Override
  public UnlocalizedComponent getComponent() {
    return new UnlocalizedComponent(name);
  }

}
