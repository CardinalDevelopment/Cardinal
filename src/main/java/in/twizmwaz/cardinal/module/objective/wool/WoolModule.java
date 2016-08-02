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
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.component.TeamComponent;
import in.twizmwaz.cardinal.event.objective.ObjectiveCompleteEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.objective.ProximityMetric;
import in.twizmwaz.cardinal.module.objective.ProximityRule;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionException;
import in.twizmwaz.cardinal.module.region.RegionModule;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.module.team.TeamModule;
import in.twizmwaz.cardinal.playercontainer.PlayingPlayerContainer;
import in.twizmwaz.cardinal.util.Channels;
import in.twizmwaz.cardinal.util.Components;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.ParseUtil;
import in.twizmwaz.cardinal.util.Proto;
import in.twizmwaz.cardinal.util.Strings;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
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
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.located.Located;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleEntry(depends = {TeamModule.class, RegionModule.class})
public class WoolModule extends AbstractModule {

  private Map<Match, List<Wool>> wools = new HashMap<>();

  @Override
  public boolean loadMatch(Match match) {
    List<Wool> wools = Lists.newArrayList();
    Document document = match.getMap().getDocument();
    for (Element woolsElement : document.getRootElement().getChildren("wools")) {
      for (Element woolElement : woolsElement.getChildren("wool")) {
        Located located = (Located) woolElement;
        String colorValue = ParseUtil.getFirstAttribute("color", woolElement, woolsElement);
        String id = ParseUtil.getFirstAttribute("id", woolElement, woolsElement);
        if (id == null) {
          id = colorValue;
        }

        String requiredValue = ParseUtil.getFirstAttribute("required", woolElement, woolsElement);
        boolean required = requiredValue == null || Numbers.parseBoolean(requiredValue);

        String teamValue = ParseUtil.getFirstAttribute("team", woolElement, woolsElement);
        if (teamValue == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"No team specified for wool",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }
        Team team = Cardinal.getModule(TeamModule.class).getTeamById(match, teamValue);
        if (team == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"Invalid team specified for wool",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }

        if (colorValue == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"No color specified for wool",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }
        DyeColor color;
        try {
          color = DyeColor.valueOf(Strings.getTechnicalName(colorValue));
        } catch (IllegalArgumentException e) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"Invalid color specified for wool",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }

        RegionModule regionModule = Cardinal.getModule(RegionModule.class);
        Region monument;
        try {
          monument = regionModule.getRegion(match, woolElement, "monument");
        } catch (RegionException e) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{RegionModule.getRegionError(e, "monument", "wool"),
                  "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }
        if (monument == null && woolsElement.getAttribute("monument") != null) {
          monument = regionModule.getRegionById(match, woolsElement.getAttributeValue("monument"));
        }
        if (monument == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"No monument specified for wool",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }

        String craftableValue = ParseUtil.getFirstAttribute("craftable", woolElement, woolsElement);
        boolean craftable = craftableValue != null && Numbers.parseBoolean(craftableValue);

        String showValue = ParseUtil.getFirstAttribute("show", woolElement, woolsElement);
        boolean show = showValue == null || Numbers.parseBoolean(showValue);

        String locationValue = ParseUtil.getFirstAttribute("location", woolElement, woolsElement);
        Proto proto = match.getMap().getProto();
        if (locationValue != null && proto.isBefore("1.4.0")) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{"Attribute \"location\" is supported in proto 1.4.0 or later",
                  "Element at " + located.getLine() + ", " + located.getColumn()}, false));
        } else if (locationValue == null && proto.isAfterOrAt("1.4.0")) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{"Attribute \"location\" should be specified for wool in proto 1.4.0 or later",
                  "Element at " + located.getLine() + ", " + located.getColumn()}, false));
        }
        Vector location = null;
        if (locationValue != null) {
          String[] coordinates = locationValue.split(",");
          if (coordinates.length != 3) {
            errors.add(new ModuleError(this, match.getMap(), new String[]{"Invalid location format specified for wool",
                "Element at " + located.getLine() + ", " + located.getColumn()}, false));
            continue;
          }
          try {
            location = new Vector(Double.parseDouble(coordinates[0].trim()),
                Double.parseDouble(coordinates[1].trim()),
                Double.parseDouble(coordinates[2].trim()));
          } catch (NumberFormatException e) {
            errors.add(new ModuleError(this, match.getMap(), new String[]{"Invalid location specified for wool",
                "Element at " + located.getLine() + ", " + located.getColumn()}, false));
            continue;
          }
        }

        ProximityMetric woolProximityMetric = ProximityMetric.CLOSEST_KILL;
        String woolProximityMetricValue = ParseUtil.getFirstAttribute("woolproximity-metric",
            woolElement, woolsElement);
        if (woolProximityMetricValue != null) {
          try {
            woolProximityMetric = ProximityMetric.valueOf(Strings.getTechnicalName(woolProximityMetricValue));
          } catch (IllegalArgumentException e) {
            errors.add(new ModuleError(this, match.getMap(),
                new String[]{"Invalid wool proximity metric specified for wool",
                    "Element at " + located.getLine() + ", " + located.getColumn()}, false));
            continue;
          }
        }

        String woolProximityHorizontalValue =
            ParseUtil.getFirstAttribute("woolproximity-horizontal", woolElement, woolsElement);
        boolean woolProximityHorizontal = woolProximityHorizontalValue != null
            && Numbers.parseBoolean(woolProximityHorizontalValue);

        ProximityMetric monumentProximityMetric = ProximityMetric.CLOSEST_BLOCK;
        String monumentProximityMetricValue =
            ParseUtil.getFirstAttribute("monumentproximity-metric", woolElement, woolsElement);
        if (monumentProximityMetricValue != null) {
          try {
            monumentProximityMetric = ProximityMetric.valueOf(
                Strings.getTechnicalName(monumentProximityMetricValue));
          } catch (IllegalArgumentException e) {
            errors.add(new ModuleError(this, match.getMap(),
                new String[]{"Invalid monument proximity metric specified for wool",
                    "Element at " + located.getLine() + ", " + located.getColumn()}, false));
            continue;
          }
        }

        String monumentProximityHorizontalValue =
            ParseUtil.getFirstAttribute("monumentproximity-horizontal", woolElement, woolsElement);
        boolean monumentProximityHorizontal = monumentProximityHorizontalValue != null
            && Numbers.parseBoolean(monumentProximityHorizontalValue);

        Wool wool = new Wool(
            match, id, required, team, color, monument, craftable, show, location,
            new ProximityRule(woolProximityMetric, woolProximityHorizontal),
            new ProximityRule(monumentProximityMetric, monumentProximityHorizontal)
        );
        wools.add(wool);
      }
    }
    this.wools.put(match, wools);
    return true;
  }

  @Override
  public void clearMatch(Match match) {
    wools.get(match).forEach(HandlerList::unregisterAll);
    wools.remove(match);
  }

  public List<Wool> getWools(@NonNull Match match) {
    return wools.get(match);
  }


  /**
   * Checks if the wool has been picked up when a player clicks on an item in their inventory.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    for (Wool wool : wools.get(Cardinal.getMatch(event.getActor()))) {
      Player player = event.getActor();
      ItemStack item = event.getCurrentItem();
      Team team = wool.getTeam();
      Match match = Cardinal.getMatch(player);
      PlayingPlayerContainer container = match.getPlayingContainer(player);
      if (!wool.isComplete()
          && item.getType().equals(Material.WOOL)
          && item.getData().getData() == wool.getColor().getData()
          && team.equals(container)) {
        wool.setTouched(true);
        boolean showMessage = false;
        if (wool.isShow() && !wool.hasPlayerTouched(player)) {
          wool.addPlayerTouched(player);
          showMessage = true;

          Channels.getTeamChannel(match, team).sendPrefixedMessage(
              new LocalizedComponentBuilder(
                  ChatConstant.getConstant("objective.wool.touched"),
                  Components.getName(player).build(),
                  wool.getComponent()
              ).build()
          );
          //todo: send message to observers
        }
      }
    }
  }

  /**
   * Checks if the wool has been picked up when a player picks an item up from the ground.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    for (Wool wool : wools.get(Cardinal.getMatch(event.getPlayer()))) {
      Player player = event.getPlayer();
      ItemStack item = event.getItem().getItemStack();
      Team team = wool.getTeam();
      Match match = Cardinal.getMatch(player);
      PlayingPlayerContainer container = match.getPlayingContainer(player);
      if (!wool.isComplete()
          && item.getType().equals(Material.WOOL)
          && item.getData().getData() == wool.getColor().getData()
          && team.equals(container)) {
        wool.setTouched(true);
        boolean showMessage = false;
        if (wool.isShow() && !wool.hasPlayerTouched(player)) {
          wool.addPlayerTouched(player);
          showMessage = true;

          Channels.getTeamChannel(match, team).sendPrefixedMessage(
              new LocalizedComponentBuilder(
                  ChatConstant.getConstant("objective.wool.touched"),
                  Components.getName(player).build(),
                  wool.getComponent()
              ).build()
          );
          //todo: send message to observers
        }
      }
    }
  }

  /**
   * Checks if this wool has been captured when a block is placed.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockPlace(BlockPlaceEvent event) {
    for (Wool wool : wools.get(Cardinal.getMatch(event.getWorld()))) {
      Player player = event.getPlayer();
      Block block = event.getBlock();
      if (wool.getMonument().evaluate(block.getLocation().toVector()).toBoolean() && !wool.isComplete()) {
        if (block.getType().equals(Material.WOOL)) {
          if (((org.bukkit.material.Wool) block.getState().getMaterialData()).getColor().equals(wool.getColor())) {
            wool.setComplete(true);

            if (wool.isShow()) {
              //fixme: unchecked cast
              Match match = Cardinal.getMatch(event.getWorld());
              Team team = (Team) match.getPlayingContainer(player);
              Channels.getGlobalChannel(match.getMatchThread()).sendMessage(
                  new LocalizedComponentBuilder(ChatConstant.getConstant("objective.wool.completed"),
                      Components.getName(player).build(),
                      wool.getComponent(),
                      new TeamComponent(team)).color(ChatColor.GRAY).build());
            }

            Bukkit.getPluginManager().callEvent(new ObjectiveCompleteEvent(wool, player));
          } else {
            event.setCancelled(true);
            if (wool.isShow()) {
              Channels.getPlayerChannel(player).sendMessage(
                  new LocalizedComponentBuilder(
                      ChatConstant.getConstant("objective.wool.error.wrongBlock"),
                      wool.getComponent()
                  ).color(ChatColor.RED).build()
              );
            }
          }
        } else {
          event.setCancelled(true);
          if (wool.isShow()) {
            Channels.getPlayerChannel(player).sendMessage(
                new LocalizedComponentBuilder(
                    ChatConstant.getConstant("objective.wool.error.wrongBlock"),
                    wool.getComponent()
                ).color(ChatColor.RED).build()
            );
          }
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
    for (Wool wool : wools.get(Cardinal.getMatch(event.getWorld()))) {
      if (wool.getMonument().evaluate(event.getBlock().getLocation().toVector()).toBoolean()) {
        event.setCancelled(true);
        break;
      }
    }
  }

  /**
   * Prevents blocks from forming on the wool monument, such as snow.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockForm(BlockFormEvent event) {
    for (Wool wool : wools.get(Cardinal.getMatch(event.getWorld()))) {
      if (wool.getMonument().evaluate(event.getBlock().getLocation().toVector()).toBoolean()) {
        event.setCancelled(true);
        break;
      }
    }
  }

  /**
   * Prevents blocks from spreading onto the wool monument, such as mushrooms or fire.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockSpread(BlockSpreadEvent event) {
    for (Wool wool : wools.get(Cardinal.getMatch(event.getWorld()))) {
      if (wool.getMonument().evaluate(event.getBlock().getLocation().toVector()).toBoolean()) {
        event.setCancelled(true);
        break;
      }
    }
  }

  /**
   * Prevents entities from changing blocks, such as endermen or falling blocks.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onEntityChangeBlock(EntityChangeBlockEvent event) {
    for (Wool wool : wools.get(Cardinal.getMatch(event.getWorld()))) {
      if (wool.getMonument().evaluate(event.getBlock().getLocation().toVector()).toBoolean()) {
        event.setCancelled(true);
        break;
      }
    }
  }

  /**
   * Prevents blocks from being pushed into the wool monument by a piston.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockPistonExtend(BlockPistonExtendEvent event) {
    for (Wool wool : wools.get(Cardinal.getMatch(event.getWorld()))) {
      Region monument = wool.getMonument();
      Block piston = event.getBlock();
      BlockFace direction = event.getDirection();
      if (monument.evaluate(piston.getRelative(direction).getLocation().toVector()).toBoolean()) {
        //Cancels the event if the piston's arm extends into the monument
        event.setCancelled(true);
        break;
      } else {
        //Cancels the event if any of the pushed blocks extend into the monument
        for (Block block : event.getBlocks()) {
          if (monument.evaluate(block.getLocation().toVector()).toBoolean()
              || monument.evaluate(block.getRelative(direction).getLocation().toVector()).toBoolean()) {
            event.setCancelled(true);
            break;
          }
        }
      }
    }
  }

  /**
   * Prevents blocks from being pulled from the wool monument by a piston.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockPistonRetract(BlockPistonRetractEvent event) {
    for (Wool wool : wools.get(Cardinal.getMatch(event.getWorld()))) {
      //Cancels the event if any of the pulled blocks retract from the monument
      Region monument = wool.getMonument();
      BlockFace direction = event.getDirection();
      for (Block block : event.getBlocks()) {
        if (monument.evaluate(block.getLocation().toVector()).toBoolean()
            || monument.evaluate(block.getRelative(direction).getLocation().toVector()).toBoolean()) {
          event.setCancelled(true);
          break;
        }
      }
      if (event.isCancelled()) {
        break;
      }
    }
  }

  /**
   * Prevents the wool from being crafted if specified when registering the wool.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onCraftItem(CraftItemEvent event) {
    for (Wool wool : wools.get(Cardinal.getMatch(event.getActor()))) {
      if (event.getRecipe().getResult().equals(new ItemStack(Material.WOOL, 1, wool.getColor().getData()))
          && !wool.isCraftable()) {
        event.setCancelled(true);
        break;
      }
    }
  }

  /**
   * Removes the player from the list of players who have touched the wool during their previous life.
   *
   * @param event The event.
   */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    for (Wool wool : wools.get(Cardinal.getMatch(event.getEntity()))) {
      wool.removePlayerTouched(event.getEntity());
    }
  }

}
