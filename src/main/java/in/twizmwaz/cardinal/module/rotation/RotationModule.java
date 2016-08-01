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

package in.twizmwaz.cardinal.module.rotation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.event.matchthread.MatchThreadMakeEvent;
import in.twizmwaz.cardinal.match.MatchThread;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.event.ModuleLoadCompleteEvent;
import in.twizmwaz.cardinal.module.repository.LoadedMap;
import in.twizmwaz.cardinal.module.repository.RepositoryModule;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;

@ModuleEntry(depends = {RepositoryModule.class})
public class RotationModule extends AbstractModule implements Listener {

  @Getter
  private final Map<MatchThread, Rotation> rotations = Maps.newHashMap();

  public RotationModule() {
    Cardinal.registerEvents(this);
  }

  /**
   * Creates a temporary rotation.
   *
   * @param event The event called.
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onModuleLoadComplete(ModuleLoadCompleteEvent event) {
    Cardinal.getInstance().getMatchThreads()
        .forEach(matchThread -> rotations.put(matchThread, loadRotation(Cardinal.getModule(RepositoryModule.class))));
  }

  @EventHandler
  public void onMatchThreadMake(MatchThreadMakeEvent event) {
    rotations.put(event.getMatchThread(), loadRotation(Cardinal.getModule(RepositoryModule.class)));
  }

  /**
   * Temporary method to create a temp rotation.
   *
   * @param repo The repository to generate a rotation from.
   */
  public Rotation loadRotation(RepositoryModule repo) {
    //TODO: actual rotation parsing
    List<LoadedMap> list = Lists.newArrayList();
    repo.getLoadedMaps().forEach((name, map) -> list.add(map));
    Rotation rotation = new Rotation(list);
    return rotation;
  }
}
