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
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.chat.component.builder.UnlocalizedComponentBuilder;
import ee.ellytr.chat.component.formattable.LocalizedComponent;
import ee.ellytr.chat.component.formattable.UnlocalizedComponent;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.component.TeamComponent;
import in.twizmwaz.cardinal.event.objective.ObjectiveCompleteEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.proximity.AbstractProximal;
import in.twizmwaz.cardinal.module.proximity.Proximity;
import in.twizmwaz.cardinal.module.proximity.ProximityMetric;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.playercontainer.PlayingPlayerContainer;
import in.twizmwaz.cardinal.util.Channels;
import in.twizmwaz.cardinal.util.Components;
import in.twizmwaz.cardinal.util.MaterialPattern;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Core extends AbstractProximal implements Listener {

  private final String name;
  private final Region region;
  private final int leak;
  private final MaterialPattern material;
  private final PlayingPlayerContainer owner;
  private final boolean modeChanges;
  private final ProximityMetric proximityMetric;
  private final boolean proximityHorizontal;

  private final List<Player> touchedPlayers = Lists.newArrayList();
  private final List<Block> core;
  private final List<Block> lava;

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
              MaterialPattern material, PlayingPlayerContainer owner, boolean modeChanges,
              boolean show, ProximityMetric proximityMetric, boolean proximityHorizontal) {
    super(
        match, id, required, show,
        Lists.newArrayList(new Proximity(Proximity.Identifier.BEFORE_TOUCH, Double.POSITIVE_INFINITY))
    );
    this.name = name;
    this.region = region;
    this.leak = leak;
    this.material = material;
    this.owner = owner;
    this.modeChanges = modeChanges;
    this.proximityMetric = proximityMetric;
    this.proximityHorizontal = proximityHorizontal;

    core = Lists.newArrayList();
    lava = Lists.newArrayList();
    for (Block block : region.getBlocks()) {
      if (isPartOf(block)) {
        core.add(block);
      }
      Material type = block.getType();
      if (type.equals(Material.STATIONARY_LAVA) || type.equals(Material.LAVA)) {
        lava.add(block);
      }
    }
  }

  /**
   * Checks if the core has been touched when a player breaks a block.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Block block = event.getBlock();
    PlayingPlayerContainer container = Cardinal.getMatch(player).getPlayingContainer(player);
    if (!(container instanceof Team)) {
      //fixme: does it?
      throw new IllegalStateException("FFA does not support cores.");
    }
    Team team = (Team) container;
    if (getBlocks().contains(block) && (team == null || !team.equals(this.owner))) {
      touched = true;
      boolean showMessage = false;
      if (isShow() && !touchedPlayers.contains(player)) {
        touchedPlayers.add(player);
        showMessage = true;
        Channels.getTeamChannel(getMatch(), team).sendMessage(Components.appendTeamPrefix(team, new LocalizedComponent(
            ChatConstant.getConstant("objective.core.touched"),
            new TeamComponent((Team) this.owner),
            new UnlocalizedComponent(name),
            Components.getName(player).build())));
      }
    }
  }

  /**
   * Checks if lava has reached the leak distance below this core.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockFromTo(BlockFromToEvent event) {
    Block to = event.getToBlock();
    if (lava.contains(to)) {
      event.setCancelled(true);
      return;
    }
    Material type = event.getBlock().getType();
    if ((type.equals(Material.STATIONARY_LAVA) || type.equals(Material.LAVA))
        && to.getType().equals(Material.AIR) && Cardinal.getModule(CoreModule.class).getClosestCore(getMatch(),
        to.getLocation().toVector()).equals(this) && !complete) {
      Block bottomBlock = getBottomBlock();
      if (bottomBlock != null) {
        int distance = getBottomBlock().getY() - to.getY();
        if (distance >= leak) {
          complete = true;
          Channels.getGlobalChannel(Cardinal.getMatchThread(getMatch())).sendMessage(new LocalizedComponentBuilder(
              ChatConstant.getConstant("objective.core.completed"), new TeamComponent((Team) owner),
              new UnlocalizedComponentBuilder(name).color(ChatColor.RED).build()).color(ChatColor.RED).build());
          Bukkit.getPluginManager().callEvent(new ObjectiveCompleteEvent(this, null));
        }
      }
    }
  }

  /**
   * Removes the player from the list of players who have touched the core during their previous life.
   *
   * @param event The event.
   */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    if (match.hasPlayer(player)) {
      touchedPlayers.remove(player);
    }
  }

  /**
   * @param block The block that is checked as part of this core.
   * @return If the block has the properties to be considered part of the core.
   */
  private boolean isPartOf(@NonNull Block block) {
    return material.contains(block.getType(), (int) block.getState().getMaterialData().getData());
  }

  /**
   * @return The bottom block of the core inside the specified region.
   */
  private Block getBottomBlock() {
    Block bottomBlock = null;
    int bottomY = Integer.MAX_VALUE;
    for (Block block : core) {
      int yPos = block.getY();
      if (yPos < bottomY) {
        bottomBlock = block;
        bottomY = yPos;
      }
    }
    return bottomBlock;
  }

  public List<Block> getBlocks() {
    return core.stream().filter(this::isPartOf).collect(Collectors.toList());
  }

  @Override
  public UnlocalizedComponent getComponent() {
    return new UnlocalizedComponent(name);
  }

}
