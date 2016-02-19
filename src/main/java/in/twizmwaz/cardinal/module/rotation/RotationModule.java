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
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.repository.LoadedMap;
import in.twizmwaz.cardinal.module.repository.RepositoryModule;
import lombok.Getter;

import java.util.List;

@ModuleEntry
public class RotationModule extends AbstractModule {

  @Getter
  private final List<LoadedMap> rotation = Lists.newArrayList();
  private int position;

  public RotationModule() {
    this.depends = new Class[] { RepositoryModule.class };
  }

  /**
   * Temporary method to create a temp rotation.
   */
  public void loadRotation(RepositoryModule repo) {
    repo.getLoadedMaps().entrySet().forEach(map ->
        rotation.add(map.getValue()));
  }

  public LoadedMap getNext() {
    return rotation.get(position);
  }

  /**
   * Increments the position in the rotation by one, and resets
   * it to the beginning if it has completed.
   *
   * @return The new position.
   */
  public int move() {
    position++;
    if (position > rotation.size() - 1) {
      position = 0;
    }
    return position;
  }
}
