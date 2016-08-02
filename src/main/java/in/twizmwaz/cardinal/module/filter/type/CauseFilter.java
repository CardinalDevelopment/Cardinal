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

package in.twizmwaz.cardinal.module.filter.type;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.WorldEvent;

@AllArgsConstructor
public class CauseFilter extends ObjectTypeFilter<Event> {

  private final EventCause cause;

  @Override
  public Class<Event> getType() {
    return Event.class;
  }

  @Override
  public Boolean evaluate(Event event) {
    if (!(event instanceof EntityDamageEvent)) {
      switch (cause) {
        /* Actor Type */
        case WORLD:
          return event instanceof WorldEvent;
        case LIVING:
          return event instanceof EntityEvent && ((EntityEvent) event).getEntity() instanceof LivingEntity;
        case MOB:
          return event instanceof EntityEvent && ((EntityEvent) event).getEntity() instanceof Creature;
        case PLAYER:
          return event instanceof PlayerEvent;
        /* Block action */
        case PUNCH:
          return event instanceof BlockDamageEvent;
        case TRAMPLE:
          return event instanceof PlayerMoveEvent;
        case MINE:
          return event instanceof BlockBreakEvent;

        case EXPLOSION:
          return event instanceof EntityExplodeEvent;

        default:
          return null;
      }
    } else {
      /* Damage Type */
      EntityDamageEvent.DamageCause damageCause = ((EntityDamageEvent) event).getCause();
      switch (cause) {
        case MELEE:
          return damageCause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        case PROJECTILE:
          return damageCause.equals(EntityDamageEvent.DamageCause.PROJECTILE);
        case POTION:
          return damageCause.equals(EntityDamageEvent.DamageCause.MAGIC)
              || damageCause.equals(EntityDamageEvent.DamageCause.POISON)
              || damageCause.equals(EntityDamageEvent.DamageCause.WITHER)
              || damageCause.equals(EntityDamageEvent.DamageCause.DRAGON_BREATH);
        case EXPLOSION:
          return damageCause.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
              || damageCause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
        case COMBUSTION:
          return damageCause.equals(EntityDamageEvent.DamageCause.FIRE)
              || damageCause.equals(EntityDamageEvent.DamageCause.FIRE_TICK)
              || damageCause.equals(EntityDamageEvent.DamageCause.MELTING)
              || damageCause.equals(EntityDamageEvent.DamageCause.LAVA)
              || damageCause.equals(EntityDamageEvent.DamageCause.HOT_FLOOR);
        case FALL:
          return damageCause.equals(EntityDamageEvent.DamageCause.FALL);
        case GRAVITY:
          return damageCause.equals(EntityDamageEvent.DamageCause.FALL)
              || damageCause.equals(EntityDamageEvent.DamageCause.VOID);
        case VOID:
          return damageCause.equals(EntityDamageEvent.DamageCause.VOID);
        case SQUASH:
          return damageCause.equals(EntityDamageEvent.DamageCause.FALLING_BLOCK);
        case SUFFOCATION:
          return damageCause.equals(EntityDamageEvent.DamageCause.SUFFOCATION);
        case DROWNING:
          return damageCause.equals(EntityDamageEvent.DamageCause.DROWNING);
        case STARVATION:
          return damageCause.equals(EntityDamageEvent.DamageCause.STARVATION);
        case LIGHTNING:
          return damageCause.equals(EntityDamageEvent.DamageCause.LIGHTNING);
        case CACTUS:
          return damageCause.equals(EntityDamageEvent.DamageCause.CONTACT);
        case THORNS:
          return damageCause.equals(EntityDamageEvent.DamageCause.THORNS);

        default:
          return null;
      }
    }
  }

  public enum EventCause {
    /* Actor Type */
    WORLD(),
    LIVING(),
    MOB(),
    PLAYER(),
    /* Block action */
    PUNCH(),
    TRAMPLE(),
    MINE(),
    /* Damage Type */
    MELEE(),
    PROJECTILE(),
    POTION(),
    EXPLOSION(),
    COMBUSTION(),
    FALL(),
    GRAVITY(),
    VOID(),
    SQUASH(),
    SUFFOCATION(),
    DROWNING(),
    STARVATION(),
    LIGHTNING(),
    CACTUS(),
    THORNS();

    /**
     * Gets an event cause by a string
     * @param string The cause name
     * @return An event cause that matches that name, null if none were found.
     */
    public static EventCause getEventCause(String string) {
      switch (string.toLowerCase().replaceAll(" ", "")) {
        case "world":
          return WORLD;
        case "living":
          return LIVING;
        case "mob":
          return MOB;
        case "player":
          return PLAYER;
        case "punch":
          return PUNCH;
        case "trample":
          return TRAMPLE;
        case "mine":
          return MINE;
        case "melee":
          return MELEE;
        case "projectile":
          return PROJECTILE;
        case "potion":
          return POTION;
        case "tnt":
        case "explosion":
          return EXPLOSION;
        case "combustion":
          return COMBUSTION;
        case "fall":
          return FALL;
        case "gravity":
          return GRAVITY;
        case "void":
          return VOID;
        case "squash":
          return SQUASH;
        case "suffocation":
          return SUFFOCATION;
        case "drowning":
          return DROWNING;
        case "starvation":
          return STARVATION;
        case "lightning":
          return LIGHTNING;
        case "cactus":
          return CACTUS;
        case "thorns":
          return THORNS;
        default:
          return null;
      }
    }
  }

}
