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

package in.twizmwaz.cardinal.module.objective.wool;

import com.google.common.collect.Lists;
import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.NameComponent;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.chat.component.builder.UnlocalizedComponentBuilder;
import in.twizmwaz.cardinal.component.TeamComponent;
import in.twizmwaz.cardinal.event.objective.ObjectiveCompleteEvent;
import in.twizmwaz.cardinal.event.objective.ObjectiveTouchEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.ProximityMetric;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.util.Channels;
import in.twizmwaz.cardinal.util.Colors;
import in.twizmwaz.cardinal.util.Components;
import in.twizmwaz.cardinal.util.Strings;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

@Getter
public class Wool extends Objective implements Listener {

  private final Team team;
  private final DyeColor color;
  private final Region monument;
  private final boolean craftable;
  private final Vector location;
  private final ProximityMetric woolProximityMetric;
  private final boolean woolProximityHorizontal;
  private final ProximityMetric monumentProximityMetric;
  private final boolean monumentProximityHorizontal;

  private final List<Player> touchedPlayers = Lists.newArrayList();

  private boolean touched;
  private boolean complete;

  /**
   * @param match                       The match the wool belongs to.
   * @param id                          This wool's ID.
   * @param required                    Determines if this wool is required to win the match.
   * @param team                        The team that needs to capture this wool.
   * @param color                       The dye color of this wool.
   * @param monument                    The location for where the wool is placed when it is captured.
   * @param craftable                   Determines if this wool may be crafted with white wool and a dye.
   * @param show                        Determines if this wool shows on the scoreboard.
   * @param location                    The location of the wool room, used in proximity calculation.
   * @param woolProximityMetric         The proximity metric that determines how to calculate proximity
   *                                    before picking up the wool.
   * @param woolProximityHorizontal     Determines if only horizontal distance is considered when
   *                                    calculating proximity before picking up the wool.
   * @param monumentProximityMetric     The proximity metric that determines how to calculate proximity
   *                                    after picking up the wool.
   * @param monumentProximityHorizontal Determines if only horizontal distance is considered when
   *                                    calculating proximity after picking up the wool.
   */
  public Wool(Match match, String id, boolean required, Team team, DyeColor color, Region monument, boolean craftable,
              boolean show, Vector location, ProximityMetric woolProximityMetric, boolean woolProximityHorizontal,
              ProximityMetric monumentProximityMetric, boolean monumentProximityHorizontal) {
    super(match, id, required, show);
    this.team = team;
    this.color = color;
    this.monument = monument;
    this.craftable = craftable;
    this.location = location;
    this.woolProximityMetric = woolProximityMetric;
    this.woolProximityHorizontal = woolProximityHorizontal;
    this.monumentProximityMetric = monumentProximityMetric;
    this.monumentProximityHorizontal = monumentProximityHorizontal;

    complete = false;
  }

  /**
   * Checks if the wool has been picked up when a player clicks on an item in their inventory.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();
    ItemStack item = event.getCurrentItem();
    if (!complete && item.getType().equals(Material.WOOL)
        && item.getData().getData() == color.getData() && team.equals(Team.getTeam(player))) {
      touched = true;
      boolean showMessage = false;
      if (isShow() && !touchedPlayers.contains(player)) {
        touchedPlayers.add(player);
        showMessage = true;

        Channels.getTeamChannel(team).sendMessage(Components.appendTeamPrefix(team,
            new LocalizedComponentBuilder(ChatConstant.getConstant("objective.wool.touched"),
                new NameComponent(player),
                new UnlocalizedComponentBuilder(getName())
                    .color(Colors.convertDyeToChatColor(color)).build()).build()));
        Channels.getTeamChannel(Team.getObservers()).sendMessage(Components.appendTeamPrefix(team,
            new LocalizedComponentBuilder(ChatConstant.getConstant("objective.wool.touchedFor"),
                Components.getNameComponentBuilder(player).flairs(false).build(),
                new UnlocalizedComponentBuilder(getName()).color(Colors.convertDyeToChatColor(color)).build(),
                new TeamComponent(team)).build()));
      }
      Bukkit.getPluginManager().callEvent(new ObjectiveTouchEvent(this, player, showMessage));
    }
  }

  /**
   * Checks if the wool has been picked up when a player picks an item up from the ground.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    Player player = event.getPlayer();
    ItemStack item = event.getItem().getItemStack();
    if (!complete && item.getType().equals(Material.WOOL)
        && item.getData().getData() == color.getData() && team.equals(Team.getTeam(player))) {
      touched = true;
      boolean showMessage = false;
      if (isShow() && !touchedPlayers.contains(player)) {
        touchedPlayers.add(player);
        showMessage = true;

        Channels.getTeamChannel(team).sendMessage(Components.appendTeamPrefix(team,
            new LocalizedComponentBuilder(ChatConstant.getConstant("objective.wool.touched"),
                Components.getNameComponentBuilder(player).flairs(false).build(),
                new UnlocalizedComponentBuilder(getName())
                    .color(Colors.convertDyeToChatColor(color)).build()).build()));
        Channels.getTeamChannel(Team.getObservers()).sendMessage(Components.appendTeamPrefix(team,
            new LocalizedComponentBuilder(ChatConstant.getConstant("objective.wool.touchedFor"),
                Components.getNameComponentBuilder(player).flairs(false).build(),
                new UnlocalizedComponentBuilder(getName()).color(Colors.convertDyeToChatColor(color)).build(),
                new TeamComponent(team)).build()));
      }
      Bukkit.getPluginManager().callEvent(new ObjectiveTouchEvent(this, player, showMessage));
    }
  }

  /**
   * Checks if this wool has been captured when a block is placed.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockPlace(BlockPlaceEvent event) {
    Block block = event.getBlock();
    Player player = event.getPlayer();
    if (monument.evaluate(block.getLocation().toVector()) && !complete) {
      if (block.getType().equals(Material.WOOL)) {
        if (((org.bukkit.material.Wool) block.getState().getMaterialData()).getColor().equals(color)) {
          complete = true;

          if (isShow()) {
            Channels.getGlobalChannel().sendMessage(
                new LocalizedComponentBuilder(ChatConstant.getConstant("objective.wool.completed"),
                    Components.getNameComponentBuilder(player).build(),
                    new UnlocalizedComponentBuilder(getName()).color(Colors.convertDyeToChatColor(color)).build(),
                    new TeamComponent(Team.getTeam(player))).color(ChatColor.GRAY).build());
          }

          Bukkit.getPluginManager().callEvent(new ObjectiveCompleteEvent(this, player));
        } else {
          event.setCancelled(true);
          if (isShow()) {
            Channels.getPlayerChannel(player).sendMessage(
                new LocalizedComponentBuilder(ChatConstant.getConstant("objective.wool.error.wrongBlock"),
                    new UnlocalizedComponentBuilder(getName()).color(Colors.convertDyeToChatColor(color)).build())
                    .color(ChatColor.RED).build());
          }
        }
      } else {
        event.setCancelled(true);
        if (isShow()) {
          Channels.getPlayerChannel(player).sendMessage(
              new LocalizedComponentBuilder(ChatConstant.getConstant("objective.wool.error.wrongBlock"),
                  new UnlocalizedComponentBuilder(getName()).color(Colors.convertDyeToChatColor(color)).build())
                  .color(ChatColor.RED).build());
        }
      }
    }
  }

  /**
   * Prevents players from breaking blocks that are inside the wool monument.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    if (monument.evaluate(event.getBlock().getLocation().toVector())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents blocks from forming on the wool monument, such as snow.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockForm(BlockFormEvent event) {
    if (monument.evaluate(event.getBlock().getLocation().toVector())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents blocks from spreading onto the wool monument, such as mushrooms or fire.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockSpread(BlockSpreadEvent event) {
    if (monument.evaluate(event.getBlock().getLocation().toVector())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents entities from changing blocks, such as endermen or falling blocks.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onEntityChangeBlock(EntityChangeBlockEvent event) {
    if (monument.evaluate(event.getBlock().getLocation().toVector())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents blocks from being pushed into the wool monument by a piston.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockPistonExtend(BlockPistonExtendEvent event) {
    Block block = event.getBlock();
    if (monument.evaluate(event.getBlock().getRelative(event.getDirection()).getLocation().toVector())) {
      //Cancels the event if the piston's arm extends into the monument
      event.setCancelled(true);
    } else {
      //Cancels the event if any of the pushed blocks extend into the monument
      event.getBlocks().stream().filter(extended -> monument.evaluate(extended.getLocation().toVector())
          || monument.evaluate(block.getRelative(event.getDirection()).getLocation().toVector()))
          .forEach(extended -> event.setCancelled(true));
    }
  }

  /**
   * Prevents blocks from being pulled from the wool monument by a piston.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockPistonRetract(BlockPistonRetractEvent event) {
    //Cancels the event if any of the pulled blocks retract from the monument
    event.getBlocks().stream().filter(block -> monument.evaluate(block.getLocation().toVector())
        || monument.evaluate(block.getRelative(event.getDirection()).getLocation().toVector()))
        .forEach(block -> event.setCancelled(true));
  }

  /**
   * Prevents the wool from being crafted if specified when registering the wool.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onCraftItem(CraftItemEvent event) {
    if (event.getRecipe().getResult().equals(new ItemStack(Material.WOOL, 1, color.getData())) && !this.craftable) {
      event.setCancelled(true);
    }
  }

  /**
   * Removes the player from the list of players who have touched the wool during their previous life.
   *
   * @param event The event.
   */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    touchedPlayers.remove(event.getEntity());
  }

  @Override
  public String getName() {
    return WordUtils.capitalizeFully(Strings.getSimpleName(color.name())) + " Wool";
  }

}
