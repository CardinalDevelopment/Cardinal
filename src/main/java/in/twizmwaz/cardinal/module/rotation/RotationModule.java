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
import in.twizmwaz.cardinal.match.MatchThread;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.event.ModuleLoadCompleteEvent;
import in.twizmwaz.cardinal.module.repository.LoadedMap;
import in.twizmwaz.cardinal.module.repository.RepositoryModule;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;

//TODO: consider a rotation object for each MatchThread, rather than the maps
@ModuleEntry
public class RotationModule extends AbstractModule implements Listener {

  @Getter
  private final Map<MatchThread, List<LoadedMap>> rotations = Maps.newHashMap();
  private Map<MatchThread, Integer> positions = Maps.newHashMap();

  public RotationModule() {
    this.depends = new Class[] { RepositoryModule.class };
    Bukkit.getPluginManager().registerEvents(this, Cardinal.getInstance());
  }

  /**
   * Creates a temporary rotation.
   *
   * @param event The event called.
   */
  @EventHandler
  public void onModuleLoadComplete(ModuleLoadCompleteEvent event) {
    rotations.put(Cardinal.getInstance().getMatchThread(),
        loadRotation(Cardinal.getModule(RepositoryModule.class)));
    positions.put(Cardinal.getInstance().getMatchThread(), 0);
  }

  /**
   * Temporary method to create a temp rotation.
   *
   * @param repo The repository to generate a rotation from.
   */
  public List<LoadedMap> loadRotation(RepositoryModule repo) {
    //TODO: actual rotation parsing
    List<LoadedMap> rotation = Lists.newArrayList();
    //repo.getLoadedMaps().forEach((name, map) -> rotation.add(map));
    rotation.add(repo.getLoadedMaps().get(0));
    return rotation;
  }

  /**
   * @param thread The thread to get the rotation for.
   * @return The next map in the rotation.
   */
  public LoadedMap getNext(MatchThread thread) {
    List<LoadedMap> maps = rotations.get(thread);
    int index = positions.get(thread);
    Validate.notNull(maps.get(index));
    return maps.get(index);
  }

  /**
   * Increments the position in the rotation by one, and resets
   * it to the beginning if it has completed.
   *
   * @return The new position.
   */
  public int move(MatchThread thread) {
    positions.put(thread, positions.get(thread) + 1);
    if (positions.get(thread) > rotations.get(thread).size() - 1) {
      positions.put(thread, 0);
    }
    return positions.get(thread);
  }
}
