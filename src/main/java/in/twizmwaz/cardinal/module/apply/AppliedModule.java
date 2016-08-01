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

import com.google.common.collect.Lists;
import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.chat.component.builder.UnlocalizedComponentBuilder;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractListenerModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.filter.Filter;
import in.twizmwaz.cardinal.module.filter.FilterModule;
import in.twizmwaz.cardinal.module.filter.FilterState;
import in.twizmwaz.cardinal.module.filter.type.StaticFilter;
import in.twizmwaz.cardinal.module.filter.type.modifiers.DenyFilter;
import in.twizmwaz.cardinal.module.kit.Kit;
import in.twizmwaz.cardinal.module.kit.KitModule;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionModule;
import in.twizmwaz.cardinal.module.region.type.AboveRegion;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.ParseUtil;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.jdom2.Element;
import org.jdom2.located.Located;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ModuleEntry(depends = {RegionModule.class, FilterModule.class, KitModule.class})
public class AppliedModule extends AbstractListenerModule {

  private Map<Match, List<AppliedRegion>> applied = new HashMap<>();

  @Override
  public boolean loadMatch(@NonNull Match match) {
    this.applied.put(match, Lists.newArrayList());

    RegionModule regionModule = Cardinal.getModule(RegionModule.class);
    FilterModule filterModule = Cardinal.getModule(FilterModule.class);
    KitModule kitModule = Cardinal.getModule(KitModule.class);

    for (Element appliedElement : match.getMap().getDocument().getRootElement().getChildren("applied")) {
      for (Element applyElement : appliedElement.getChildren("apply")) {
        Region region = RegionModule.EVERYWHERE;
        if (applyElement.getAttribute("region") != null) {
          region = regionModule.getRegionById(match, applyElement.getAttributeValue("region"));
          if (region == null) {
            Located located = (Located) applyElement;
            errors.add(new ModuleError(this, match.getMap(), new String[]{"Invalid region specified for applied region",
                "Element at " + located.getLine() + ", " + located.getColumn()}, false));
            continue;
          }
        }

        String message = ParseUtil.getFirstAttribute("message", applyElement);
        boolean earlyWarning = Boolean.parseBoolean(
            ParseUtil.fallback(ParseUtil.getFirstAttribute("early-warning", applyElement), "false"));

        for (ApplyType type : ApplyType.values()) {
          if (applyElement.getAttribute(type.filterOnly ? type.filterAttr : type.otherAttr) == null) {
            continue;
          }
          Filter filter = FilterModule.ALLOW;
          if (applyElement.getAttribute(type.filterAttr) != null) {
            filter = filterModule.getFilter(match, applyElement.getAttributeValue(type.filterAttr));
            if (filter == null) {
              Located attrLocated = (Located) applyElement.getAttribute(type.filterAttr);
              errors.add(new ModuleError(this, match.getMap(),
                  new String[]{"Invalid filter specified for applied region attribute: " + type.filterAttr,
                      "Element at " + attrLocated.getLine() + ", " + attrLocated.getColumn()}, false));
              continue;
            }
          }

          if (type.filterOnly) {
            applied.get(match).add(new AppliedRegion(type, region, filter, message, earlyWarning));
          } else if (type.equals(ApplyType.VELOCITY)) {
            Vector vel = Numbers.getVector(applyElement.getAttributeValue(type.otherAttr));
            if (vel == null) {
              Located attrLocated = (Located) applyElement.getAttribute(type.otherAttr);
              errors.add(new ModuleError(this, match.getMap(),
                  new String[]{"Invalid vector specified for applied region attribute: " + type.otherAttr,
                      "Element at " + attrLocated.getLine() + ", " + attrLocated.getColumn()}, false));
              continue;
            }
            applied.get(match).add(new AppliedRegion(region, filter, vel));
          } else {
            Kit kit = kitModule.getKit(match, applyElement.getAttributeValue(type.otherAttr));
            if (kit == null) {
              Located attrLocated = (Located) applyElement.getAttribute(type.otherAttr);
              errors.add(new ModuleError(this, match.getMap(),
                  new String[]{"Invalid kit specified for applied region attribute: " + type.otherAttr,
                      "Element at " + attrLocated.getLine() + ", " + attrLocated.getColumn()}, false));
              continue;
            }
            applied.get(match).add(new AppliedRegion(type, region, filter, kit));
          }
        }
      }

      // Default to no mobs, new regions can be added before this one to allow spawning.
      add(match, new AppliedRegion(ApplyType.MOBS, RegionModule.EVERYWHERE,
          new DenyFilter(FilterModule.CREATURE), (String) null));

      // Max build height, Set to prioritize, so it will override other regions,
      for (Element maxBuildHeight : appliedElement.getChildren("maxbuildheight")) {
        int max = Numbers.parseInteger(maxBuildHeight.getText(), 256);
        add(match,
            new AppliedRegion(
                ApplyType.BLOCK_PLACE,
                new AboveRegion(match, new Vector(Integer.MIN_VALUE, max, Integer.MIN_VALUE)),
                new StaticFilter(FilterState.DENY),
                new LocalizedComponentBuilder(
                    ChatConstant.getConstant("region.max.build"),
                    new UnlocalizedComponentBuilder(max + "").color(ChatColor.AQUA).build()
                ).color(ChatColor.RED).build()),
            true);
      }
    }
    return true;
  }

  /**
   * Adds a new applied region to the match without a priority.
   * @param match The match.
   * @param appliedRegion The applied region to add.
   */
  public void add(Match match, AppliedRegion appliedRegion) {
    add(match, appliedRegion, false);
  }

  /**
   * Adds a new applied region to the match with a priority.
   * @param match The match.
   * @param appliedRegion The applied region to add.
   * @param prioritize if the region should be tested before or after other regions.
   */
  public void add(Match match, AppliedRegion appliedRegion, boolean prioritize) {
    applied.get(match).add(prioritize ? 0 : applied.get(match).size(), appliedRegion);
  }

  /**
   * Gets a list of applied regions that match the type.
   * @param match The match to get regions from.
   * @param types Array of types you want to include.
   * @return A list of all applied regions in the match that are from any of the types.
   */
  public List<AppliedRegion> get(Match match, ApplyType... types) {
    List<ApplyType> typeList = Arrays.asList(types);
    return applied.get(match).stream().filter(appliedRegion ->
        typeList.contains(appliedRegion.getType())).collect(Collectors.toList());
  }

  /**
   * Filters PlayerMoveEvent.
   *
   * <p>Applies to: enter, leave, kits and velocity.<p/>
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerMove(PlayerMoveEvent event) {
    Match match = Cardinal.getMatch(event.getPlayer());
    if (match == null || !match.hasPlayer(event.getPlayer())) {
      return;
    }
    Vector from = event.getFrom().toVector();
    Vector to = event.getTo().toVector();

    for (AppliedRegion reg : get(match, ApplyType.ENTER, ApplyType.LEAVE)) {
      boolean containsFrom = reg.contains(from);
      if (containsFrom ^ reg.contains(to) && reg.isType(ApplyType.ENTER) != containsFrom
          && apply(reg, null, event.getPlayer(), event, event, event.getPlayer())) {
        break;
      }
    }
    if (event.isCancelled()) {
      return;
    }
    for (AppliedRegion reg : get(match, ApplyType.KIT, ApplyType.KIT_LEND, ApplyType.VELOCITY)) {
      boolean containsFrom = reg.contains(from);
      if (containsFrom ^ reg.contains(to)) {
        if (containsFrom) {
          reg.remove(event.getPlayer());
        } else {
          reg.applyEffects(event.getPlayer(), event, event.getPlayer());
        }
      }
    }
  }

  /**
   * Filters BlockPlaceEvent.
   *
   * <p>Applies to: block, block place and block place against.<p/>
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockPlace(BlockPlaceEvent event) {
    Match match = Cardinal.getMatch(event.getPlayer());
    if (match == null || !match.hasPlayer(event.getPlayer())) {
      return;
    }
    for (AppliedRegion reg : get(match, ApplyType.BLOCK, ApplyType.BLOCK_PLACE, ApplyType.BLOCK_PLACE_AGAINST)) {
      Block evaluating = reg.isType(ApplyType.BLOCK_PLACE_AGAINST) ? event.getBlockAgainst() : event.getBlock();

      if (apply(reg, evaluating.getLocation(), event.getPlayer(), event, event, evaluating, event.getPlayer())) {
        break;
      }
    }
  }

  /**
   * Filters BlockBreakEvent.
   *
   * <p>Applies to: block, block break.<p/>
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    Match match = Cardinal.getMatch(event.getPlayer());
    if (match == null || !match.hasPlayer(event.getPlayer())) {
      return;
    }
    for (AppliedRegion reg : get(match, ApplyType.BLOCK, ApplyType.BLOCK_BREAK)) {
      Block evaluating = event.getBlock();

      if (apply(reg, evaluating.getLocation(), event.getPlayer(), event, event, evaluating, event.getPlayer())) {
        break;
      }
    }
  }

  /**
   * Filters EntityExplodeEvent. It will remove from the blocks to break list blocks that can't be destroyed.
   *
   * <p>Applies to: block, block break.<p/>
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockExplode(EntityExplodeEvent event) {
    Match match = Cardinal.getMatch(event.getWorld());
    if (match == null) {
      return;
    }

    Collection<AppliedRegion> regions = get(match, ApplyType.BLOCK, ApplyType.BLOCK_BREAK);

    Iterator<Block> blockIterator = event.blockList().iterator();
    while (blockIterator.hasNext()) {
      Block evaluating = blockIterator.next();

      for (AppliedRegion reg : regions) {
        if (apply(reg, evaluating.getLocation(), null, event, event, evaluating/* TODO: tnt tracker, pass player*/)) {
          if (event.isCancelled()) {
            event.setCancelled(false);
            blockIterator.remove();
          }
          break;
        }
      }
    }

  }

  /**
   * Filters PlayerBucketEmptyEvent (placing down a block of liquid).
   *
   * <p>Applies to: block, block place and block place against.<p/>
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    Match match = Cardinal.getMatch(event.getPlayer());
    if (match == null || !match.hasPlayer(event.getPlayer())) {
      return;
    }
    for (AppliedRegion reg : get(match, ApplyType.BLOCK, ApplyType.BLOCK_PLACE, ApplyType.BLOCK_PLACE_AGAINST)) {
      Block evaluating = reg.isType(ApplyType.BLOCK_PLACE_AGAINST)
          ? event.getBlockClicked() : event.getBlockClicked().getRelative(event.getBlockFace());

      if (apply(reg, evaluating.getLocation(), event.getPlayer(), event,
          event, getBucketResult(event.getBucket()), event.getPlayer())) {
        break;
      }
    }
  }

  /**
   * Filters PlayerBucketFillEvent (removing a block of liquid).
   *
   * <p>Applies to: block and block break.<p/>
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBucketFill(PlayerBucketFillEvent event) {
    Match match = Cardinal.getMatch(event.getPlayer());
    if (match == null || !match.hasPlayer(event.getPlayer())) {
      return;
    }
    for (AppliedRegion reg : get(match, ApplyType.BLOCK, ApplyType.BLOCK_BREAK)) {
      Block evaluating = event.getBlockClicked();

      if (apply(reg, evaluating.getLocation(), event.getPlayer(), event, event, evaluating, event.getPlayer())) {
        break;
      }
    }
  }

  /**
   * Filters BlockPistonExtendEvent.
   * <p>Will filter as block removing the old position, and placing a block in the new position.</p>
   *
   * <p>Applies to: block, block place and block break.<p/>
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockPistonExtend(BlockPistonExtendEvent event) {
    Match match = Cardinal.getMatch(event.getWorld());
    if (match == null) {
      return;
    }
    Collection<AppliedRegion> regions = get(match, ApplyType.BLOCK, ApplyType.BLOCK_PLACE, ApplyType.BLOCK_BREAK);

    Block pistonHead = event.getBlock().getRelative(event.getDirection());

    // Try place the piston head
    for (AppliedRegion reg : regions) {
      if (!reg.getType().equals(ApplyType.BLOCK_BREAK) && reg.contains(pistonHead.getLocation())) {
        FilterState result = reg.evaluate(event, Material.PISTON_EXTENSION);
        if (!result.toBoolean()) {
          event.setCancelled(true);
          return;
        } else if (result.hasResult()) {
          break;
        }
      }
    }

    for (Block block : event.getBlocks()) {
      if (!tryPistonMove(regions, block, event)) {
        event.setCancelled(true);
        return;
      }
    }
  }

  /**
   * Filters BlockPistonRetractEvent.
   * <p>Will filter as block removing the old position, and placing a block in the new position.</p>
   *
   * <p>Applies to: block, block place and block break.<p/>
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockPistonRetract(BlockPistonRetractEvent event) {
    Match match = Cardinal.getMatch(event.getWorld());
    if (match == null || !event.isSticky()) {
      return;
    }
    Collection<AppliedRegion> regions = get(match, ApplyType.BLOCK, ApplyType.BLOCK_PLACE, ApplyType.BLOCK_BREAK);

    for (Block block : event.getBlocks()) {
      if (!tryPistonMove(regions, block, event)) {
        event.setCancelled(true);
        return;
      }
    }
  }

  /**
   * Filters EntityChangeBlockEvent, like endermen placing down blocks or falling sand entity converting to block.
   * <p>If new block is air, it will filter as block removing else it will filter as block placing </p>
   *
   * <p>Applies to: block, block place and block break<p/>
   */
  @EventHandler(ignoreCancelled = true)
  public void onEntityChangeBlock(EntityChangeBlockEvent event) {
    Match match = Cardinal.getMatch(event.getWorld());
    if (match == null) {
      return;
    }
    if (event.getTo().equals(Material.AIR)) {
      for (AppliedRegion reg : get(match, ApplyType.BLOCK, ApplyType.BLOCK_BREAK)) {
        if (apply(reg, event.getBlock().getLocation(), null, event, event, event.getBlock())) {
          break;
        }
      }
    } else {
      for (AppliedRegion reg : get(match, ApplyType.BLOCK, ApplyType.BLOCK_PLACE)) {
        if (apply(reg, event.getBlock().getLocation(), null, event, event, event.getToData())) {
          break;
        }
      }
    }
  }

  /**
   * Filters BlockFormEvent, like endermen placing down blocks or falling sand entity converting to block.
   * <p>If new block is air, it will filter as block removing else it will filter as block placing </p>
   *
   * <p>Applies to: block, block place and block break<p/>
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockForm(BlockFormEvent event) {
    handleStateChange(event.getWorld(), event.getBlock(), event, event, event.getNewState());
  }

  /**
   * Filters BlockSpreadEvent, like mushrooms or fire spreading.
   *
   * <p>Applies to: block, block place<p/>
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockSpread(BlockSpreadEvent event) {
    handleStateChange(event.getWorld(), event.getBlock(), event, event, event.getNewState());
  }

  /**
   * Used by BlockSpreadEvent and BlockFormEvent to test conditions.
   */
  private void handleStateChange(World world, Block block, Cancellable event, Object... filter) {
    Match match = Cardinal.getMatch(world);
    if (match == null) {
      return;
    }
    for (AppliedRegion reg : get(match, ApplyType.BLOCK, ApplyType.BLOCK_PLACE)) {
      if (apply(reg, block.getLocation(), null, event, filter)) {
        break;
      }
    }
  }

  /**
   * Filters BlockPhysicsEvent, like redstone updating, or sand start falling.
   *
   * <p>Applies to: block physics<p/>
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockPhysics(BlockPhysicsEvent event) {
    Match match = Cardinal.getMatch(event.getWorld());
    if (match == null) {
      return;
    }
    for (AppliedRegion reg : get(match, ApplyType.BLOCK_PHYSICS)) {
      if (apply(reg, event.getBlock().getLocation(), null, event, event, event.getBlock())) {
        break;
      }
    }
  }

  /**
   * Filters PlayerInteractEvent (right clicking blocks or triggering pressure plates).
   *
   * <p>Applies to: use<p/>
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockUse(PlayerInteractEvent event) {
    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
      Match match = Cardinal.getMatch(event.getPlayer());
      if (match == null || !match.hasPlayer(event.getPlayer())) {
        return;
      }
      for (AppliedRegion reg : get(match, ApplyType.USE)) {
        Block evaluating = event.getClickedBlock();

        if (apply(reg, evaluating.getLocation(), event.getPlayer(), event, event, evaluating, event.getPlayer())) {
          if (event.isCancelled()) {
            event.setUseItemInHand(Event.Result.ALLOW);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setCancelled(false);
          }
          break;
        }
      }
    }
  }

  /**
   * Filters CreatureSpawnEvent, Used to deny mob spawning.
   *
   * <p>Applies to: mobs<p/>
   */
  @EventHandler
  public void onMobSpawn(CreatureSpawnEvent event) {
    Match match = Cardinal.getMatch(event.getWorld());
    if (match == null) {
      return;
    }
    for (AppliedRegion reg : get(match, ApplyType.MOBS)) {
      if (apply(reg, event.getLocation(), null, event, event, event.getSpawnReason(), event.getEntity())) {
        break;
      }
    }
  }

  /**
   * Filters BlockDamageEvent, Used for early warnings.
   *
   * <p>Applies to: block and block break<p/>
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockDamage(BlockDamageEvent event) {
    Match match = Cardinal.getMatch(event.getPlayer());
    if (match == null || !match.hasPlayer(event.getPlayer())) {
      return;
    }
    for (AppliedRegion reg : get(match, ApplyType.BLOCK, ApplyType.BLOCK_BREAK)) {
      Block evaluating = event.getBlock();

      if (apply(reg, evaluating.getLocation(), event.getPlayer(), event, event, evaluating, event.getPlayer())) {
        if (event.isCancelled()) {
          event.setCancelled(false);
        }
        break;
      }
    }
  }

  private boolean apply(AppliedRegion region, Vector position, Player player, Cancellable event, Object... objects) {
    if (position == null || region.contains(position)) {
      FilterState result = region.evaluate(objects);
      if (result.hasResult() && !result.toBoolean()) {
        event.setCancelled(true);
        region.sendMessage(player);
      }
      return result.hasResult();
    }
    return false;
  }

  private boolean tryPistonMove(Collection<AppliedRegion> regions, Block block, BlockPistonEvent event) {
    boolean allowPlace = false;
    boolean allowRemove = false;
    for (AppliedRegion reg : regions) {
      // Try remove the block
      if (!allowRemove && !reg.getType().equals(ApplyType.BLOCK_PLACE) && reg.contains(block.getLocation())) {
        FilterState result = reg.evaluate(event, block);
        if (!result.toBoolean()) {
          return false;
        } else if (result.hasResult()) {
          allowRemove = true;
        }
      }
      Location newLocation = block.getRelative(event.getDirection()).getLocation();
      // Try simulate a block place where it would end up
      if (!allowPlace && !reg.getType().equals(ApplyType.BLOCK_BREAK) && reg.contains(newLocation)) {
        FilterState result = reg.evaluate(event, block.getState().getData());
        if (!result.toBoolean()) {
          return false;
        } else if (result.hasResult()) {
          allowPlace = true;
        }
      }
      if (allowPlace && allowRemove) {
        return true;
      }
    }
    return true;
  }

  private Material getBucketResult(Material material) {
    return material.equals(Material.LAVA_BUCKET) ? Material.STATIONARY_LAVA :
        material.equals(Material.WATER_BUCKET) ? Material.STATIONARY_WATER : Material.AIR;
  }

}
