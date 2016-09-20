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

import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.chat.component.formattable.LocalizedComponent;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.component.team.TeamComponent;
import in.twizmwaz.cardinal.event.objective.ObjectiveCompleteEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractListenerModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.id.IdModule;
import in.twizmwaz.cardinal.module.objective.ProximityMetric;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionException;
import in.twizmwaz.cardinal.module.region.RegionModule;
import in.twizmwaz.cardinal.module.region.type.BlockRegion;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.module.team.TeamModule;
import in.twizmwaz.cardinal.util.Channels;
import in.twizmwaz.cardinal.util.Components;
import in.twizmwaz.cardinal.util.MaterialPattern;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.ParseUtil;
import in.twizmwaz.cardinal.util.Strings;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.located.Located;

import java.util.AbstractMap;
import java.util.List;

@ModuleEntry(depends = {IdModule.class, TeamModule.class, RegionModule.class})
public class CoreModule extends AbstractListenerModule {

  @Override
  public boolean loadMatch(Match match) {
    Document document = match.getMap().getDocument();
    for (Element coresElement : document.getRootElement().getChildren("cores")) {
      for (Element coreElement : coresElement.getChildren("core")) {
        Located located = (Located) coreElement;

        String id = ParseUtil.getFirstAttribute("id", coreElement, coresElement);

        String nameValue = ParseUtil.getFirstAttribute("name", coreElement, coresElement);
        String name = nameValue == null ? "Core" : nameValue;

        String requiredValue = ParseUtil.getFirstAttribute("required", coreElement, coresElement);
        boolean required = requiredValue == null || Numbers.parseBoolean(requiredValue);

        RegionModule regionModule = Cardinal.getModule(RegionModule.class);
        Region region;
        try {
          region = regionModule.getRegion(match, coreElement);
        } catch (RegionException e) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{RegionModule.getRegionError(e, "region", "core"),
                  "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }
        if (region == null && coresElement.getAttribute("region") != null) {
          region = regionModule.getRegionById(match, coresElement.getAttributeValue("region"));
        }
        if (region == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"No region specified for core",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }
        if (!region.isBounded()) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{"Region specified for core must be a bounded region",
                  "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }

        String leakValue = ParseUtil.getFirstAttribute("leak", coreElement, coresElement);
        int leak = 5;
        if (leakValue != null) {
          try {
            leak = Numbers.parseInteger(leakValue);
          } catch (NumberFormatException e) {
            errors.add(new ModuleError(this, match.getMap(),
                new String[]{"Invalid leak distance specified for core",
                    "Element at " + located.getLine() + ", " + located.getColumn()}, false));
            continue;
          }
        }

        MaterialPattern material = new MaterialPattern(new AbstractMap.SimpleEntry<>(Material.OBSIDIAN,
            MaterialPattern.ANY_DATA_VALUE));
        String materialValue = ParseUtil.getFirstAttribute("material", coreElement, coresElement);
        if (materialValue != null) {
          try {
            material = MaterialPattern.getSingleMaterialPattern(materialValue);
          } catch (NumberFormatException e) {
            errors.add(new ModuleError(this, match.getMap(),
                new String[]{"Invalid data value of material specified for core",
                    "Element at " + located.getLine() + ", " + located.getColumn()}, false));
            continue;
          }
        }
        String teamValue = ParseUtil.getFirstAttribute("team", coreElement, coresElement);
        if (teamValue == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"No team specified for core",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }
        Team team = Cardinal.getModule(TeamModule.class).getTeamById(match, teamValue);
        if (team == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"Invalid team specified for core",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }

        String modeChangesValue = ParseUtil.getFirstAttribute("mode-changes", coreElement, coresElement);
        boolean modeChanges = modeChangesValue == null || Numbers.parseBoolean(modeChangesValue);

        String showValue = ParseUtil.getFirstAttribute("show", coreElement, coresElement);
        boolean show = showValue == null || Numbers.parseBoolean(showValue);

        ProximityMetric proximityMetric = ProximityMetric.CLOSEST_PLAYER;
        String woolProximityMetricValue = ParseUtil.getFirstAttribute("proximity-metric", coreElement, coresElement);
        if (woolProximityMetricValue != null) {
          try {
            proximityMetric =
                ProximityMetric.valueOf(Strings.getTechnicalName(woolProximityMetricValue));
          } catch (IllegalArgumentException e) {
            errors.add(new ModuleError(this, match.getMap(),
                new String[]{"Invalid proximity metric specified for core",
                    "Element at " + located.getLine() + ", " + located.getColumn()}, false));
            continue;
          }
        }

        String proximityHorizontalValue = ParseUtil.getFirstAttribute("proximity-horizontal", coreElement,
            coresElement);
        boolean proximityHorizontal = proximityHorizontalValue != null
            && Numbers.parseBoolean(proximityHorizontalValue);

        Core core = new Core(match, id, name, required, region, leak, material, team, modeChanges, show,
            proximityMetric, proximityHorizontal);
        if (!IdModule.get().add(match, id, core)) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{"Core id is not valid or already in use",
                  "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          IdModule.get().add(match, null, core, true);
        }
      }
    }
    return true;
  }

  /**
   * @param vector The vector that this method bases the closest core off of.
   * @return The core closest to the given vector.
   */
  private Core getClosestCore(Match match, Vector vector) {
    Core closestCore = null;
    double closestDistance = Double.POSITIVE_INFINITY;
    for (Core core : IdModule.get().getList(match, Core.class)) {
      BlockRegion center = core.getRegion().getBounds().getCenterBlock();
      double distance = vector.distance(center.getVector());
      if (distance < closestDistance) {
        closestCore = core;
        closestDistance = distance;
      }
    }
    return closestCore;
  }


  private List<Core> getCores(@NonNull Match match) {
    return IdModule.get().getList(match, Core.class);
  }

  /**
   * Checks if the core has been touched when a player breaks a block.
   *
   * @param event The event.
   */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Match match = Cardinal.getMatch(player);
    List<Core> cores = getCores(match);
    if (match == null || !match.hasPlayer(player) || cores.size() == 0) {
      return;
    }
    Team team = (Team) match.getPlayingContainer(player);
    Block block = event.getBlock();
    cores.forEach(core -> {
      if (core.getRegion().contains(block.getLocation())) {
        core.setTouched(team);
        if (core.isShow() && !core.getTouchedPlayers().contains(player)) {
          core.getTouchedPlayers().add(player);
          Channels.getTeamChannel(match, team).sendPrefixedMessage(
              new LocalizedComponent(
                  ChatConstant.getConstant("objective.core.touched"),
                  new TeamComponent(core.getOwner()),
                  core.getComponent(),
                  Components.getName(player).build()
              )
          );
        }
      }
    });
  }

  /**
   * Checks if lava has reached the leak distance below this core.
   *
   * @param event The event.
   */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockFromTo(BlockFromToEvent event) {
    Match match = Cardinal.getMatch(event.getWorld());
    if (match == null) {
      return;
    }
    Block to = event.getToBlock();
    Material type = event.getBlock().getType();
    if (type.equals(Material.STATIONARY_LAVA) || type.equals(Material.LAVA)) {
      Core core = getClosestCore(match, to.getLocation().clone());
      if (core != null && !core.isComplete()) {
        int distance = getBottom(core) - to.getY();
        if (distance >= core.getLeak()) {
          core.setComplete(true);
          Channels.getGlobalChannel(Cardinal.getMatchThread(match)).sendMessage(new LocalizedComponentBuilder(
              ChatConstant.getConstant("objective.core.completed"),
              new TeamComponent(core.getOwner()),
              Components.setColor(core.getComponent(), ChatColor.RED)).color(ChatColor.RED).build());
          Bukkit.getPluginManager().callEvent(new ObjectiveCompleteEvent(core, null));
        }
      }
    }
  }

  /**
   * Removes the player from the list of players who have touched the core during their previous life.
   *
   * @param event The event.
   */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    Match match = Cardinal.getMatch(player);
    List<Core> cores = getCores(match);
    if (match == null || !match.hasPlayer(player) || cores.size() == 0) {
      return;
    }
    cores.forEach(core -> core.getTouchedPlayers().remove(player));
  }

  private int getBottom(Core core) {
    return core.getRegion().getBounds().getCuboid().minimum().getBlockY();
  }

}
