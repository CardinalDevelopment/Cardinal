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

package in.twizmwaz.cardinal.match;

import in.twizmwaz.cardinal.module.repository.LoadedMap;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.World;

import java.util.UUID;

public final class Match {

  private static int matchCounter = -1;

  @Getter
  private final MatchThread thread;
  @Getter
  private final UUID uuid;
  @Getter
  private final LoadedMap map;
  @Getter
  private final World world;

  @Getter
  private final int matchNumber;

  /**
   * Creates a new Match.
   *
   * @param thread The {@link MatchThread} that this match will occur on.
   * @param uuid The unique id of this match.
   * @param map The {@link LoadedMap} this match will occur on.
   */
  public Match(@NonNull MatchThread thread, @NonNull UUID uuid, @NonNull LoadedMap map, @NonNull World world) {
    this.thread = thread;
    this.uuid = uuid;
    this.map = map;
    this.world = world;
    this.matchNumber = matchCounter++;
  }

}
