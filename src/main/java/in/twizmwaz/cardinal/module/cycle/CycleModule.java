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

package in.twizmwaz.cardinal.module.cycle;

import com.google.common.collect.Maps;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.match.MatchThread;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.event.ModuleLoadCompleteEvent;
import in.twizmwaz.cardinal.module.repository.LoadedMap;
import in.twizmwaz.cardinal.module.repository.RepositoryModule;
import in.twizmwaz.cardinal.module.rotation.RotationModule;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@ModuleEntry
public final class CycleModule extends AbstractModule implements Listener {

  @Getter
  private final Map<MatchThread, CycleRunnable> nextCycle = Maps.newHashMap();

  public CycleModule() {
    this.depends = new Class[]{RotationModule.class};
    Bukkit.getPluginManager().registerEvents(this, Cardinal.getInstance());
  }

  /**
   * Creates a new cycle object for the initial server cycle.
   *
   * @param event The event listened for.
   */
  @EventHandler
  public void onModuleLoad(ModuleLoadCompleteEvent event) {
    CycleRunnable runnable = new CycleRunnable(this, UUID.randomUUID());
    Iterator<LoadedMap> it = Cardinal.getModule(RepositoryModule.class).getLoadedMaps().values().iterator();
    runnable.setMap(it.next());
    nextCycle.put(Cardinal.getInstance().getMatchThread(), runnable);
    cycle(Cardinal.getInstance().getMatchThread());
  }

  /**
   * Initiates the cycling process.
   *
   * @return If the cycle was successful.
   */
  public Match cycle(MatchThread thread) {
    World old = Cardinal.getInstance().getMatchThread().getCurrentMatch() != null
        ? Cardinal.getInstance().getMatchThread().getCurrentMatch().getWorld() : null;
    CycleRunnable cycle = nextCycle.get(thread);
    cycle.run();
    Match match = new Match(Cardinal.getInstance().getMatchThread(), cycle.getUuid(), cycle.getMap(), cycle.getWorld());
    thread.setCurrentMatch(match);
    CycleRunnable next = new CycleRunnable(this, UUID.randomUUID());
    next.setMap(Cardinal.getModule(RotationModule.class).getRotations().get(Cardinal.getInstance().getMatchThread())
        .getNext());
    nextCycle.put(thread, next);
    if (old != null) {
      Bukkit.unloadWorld(old, true);
    }
    return match;
  }
}
