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

package in.twizmwaz.cardinal.module.interact;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.match.MatchThread;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.spawn.SpawnModule;
import in.twizmwaz.cardinal.util.ListUtil;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerAttackEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupExperienceEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

@ModuleEntry
public class InteractModule extends AbstractModule implements Listener {

  public InteractModule() {
    Cardinal.registerEvents(this);
  }

  private boolean canInteract(@NonNull Player player) {
    MatchThread thread = Cardinal.getMatchThread(player);
    return thread.getCurrentMatch().isRunning() && thread.getCurrentMatch().hasPlayer(player);
  }

  /**
   * Prevents a player that can't interact from taking void damage.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onPlayerMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    if (!canInteract(player) && event.getTo().getY() < -64) {
      Match match = Cardinal.getMatch(player);
      event.setTo(ListUtil.getRandom(Cardinal.getModule(SpawnModule.class).getDefaultSpawn(match).getRegions())
          .getRandomPoint().toLocation(match.getWorld()));
    }
  }

  /**
   * Prevents a player that can't interact from taking void damage.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    Player player = event.getPlayer();
    if (!canInteract(player) && event.getTo().getY() < -64) {
      Match match = Cardinal.getMatch(player);
      event.setTo(ListUtil.getRandom(Cardinal.getModule(SpawnModule.class).getDefaultSpawn(match).getRegions())
          .getRandomPoint().toLocation(match.getWorld()));
    }
  }

  /**
   * Prevents a player that can't interact from breaking blocks.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    if (!canInteract(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents a player that can't interact from placing blocks.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onBlockPlace(BlockPlaceEvent event) {
    if (!canInteract(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents a player that can't interact from being set on fire.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onEntityCombustByBlock(EntityCombustByBlockEvent event) {
    if (event.getEntity() instanceof Player && !canInteract((Player) event.getEntity())) {
      event.getEntity().setFireTicks(0);
    }
  }

  /**
   * Prevents a player that can't interact from dealing damage.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onEntityDamage(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player && !canInteract((Player) event.getEntity())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents a player that can't interact from taking void damage.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Player && !canInteract((Player) event.getDamager())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents a player that can't interact from breaking a hanging entity.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
    if (event.getEntity() instanceof Player && !canInteract((Player) event.getEntity())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents a player that can't interact from placing a hanging entity.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onHangingPlace(HangingPlaceEvent event) {
    if (!canInteract(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents a player that can't interact from attacking an entity.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onPlayerAttackEntity(PlayerAttackEntityEvent event) {
    if (!canInteract(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents a player that can't interact from dropping items.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    if (!canInteract(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents a player that can't interact from interacting with an entity.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
    if (!canInteract(event.getActor())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents a player that can't interact from picking up experience.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onPlayerPickupExperience(PlayerPickupExperienceEvent event) {
    if (!canInteract(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents a player that can't interact from picking up items.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    if (!canInteract(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents a player that can't interact from damaging vehicles.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onVehicleDamage(VehicleDamageEvent event) {
    if (event.getActor() instanceof Player && !canInteract((Player) event.getActor())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents a player that can't interact from entering vehicles.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onVehicleEnter(VehicleEnterEvent event) {
    if (event.getActor() instanceof Player && !canInteract((Player) event.getActor())) {
      event.setCancelled(true);
    }
  }

  /**
   * Prevents a player that can't interact from exiting vehicles.
   *
   * @param event The event.
   */
  @EventHandler(ignoreCancelled = true)
  public void onVehicleExit(VehicleExitEvent event) {
    if (event.getActor() instanceof Player && !canInteract((Player) event.getActor())) {
      event.setCancelled(true);
    }
  }

}
