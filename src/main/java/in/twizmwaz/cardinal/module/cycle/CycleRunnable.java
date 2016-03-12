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

import com.google.common.io.Files;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.module.repository.LoadedMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public final class CycleRunnable implements Runnable {

  @NonNull
  private final CycleModule parent;
  @NonNull
  private final UUID uuid;
  @Setter
  private LoadedMap map;
  private World world;
  private File matchFile;

  @Override
  public void run() {
    Validate.notNull(map);
    Cardinal.getPluginLogger().info("Cycling to map " + map.getName());
    File dest = new File(Cardinal.getInstance().getDataFolder(), "matches/" + uuid.toString());
    dest.mkdir();
    try {
      copyDirectory(map.getDirectory(), dest);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    World world = new WorldCreator(dest.getPath()).generator(new NullChunkGenerator())
        .createWorld();
    world.setPVP(true);
    this.world = world;
    this.matchFile = dest;
  }

  private void copyDirectory(File source, File dest) throws IOException {
    if (source.isDirectory()) {
      if (!dest.exists()) {
        dest.mkdir();
      }
      String[] children = source.list();
      for (String child : children) {
        copyDirectory(new File(source, child), new File(dest, child));
      }
    } else {
      Files.copy(source, dest);
    }
  }

}