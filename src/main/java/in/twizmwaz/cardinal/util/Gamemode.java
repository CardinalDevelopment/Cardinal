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

package in.twizmwaz.cardinal.util;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public enum Gamemode {

  TEAM_DEATHMATCH("tdm", "Team Deathmatch"),
  CAPTURE_THE_FLAG("ctf", "Capture the Flag"),
  DESTROY_THE_MONUMENT("dtc", "Destroy the Monument"),
  KING_OF_THE_HILL("koth", "King of the Hill"),
  RAGE("rage", "RAGE"),
  ARCADE("arcade", "Arcade"),
  FREE_FOR_ALL("ffa", "Free for All"),
  CAPTURE_THE_WOOL("ctw", "Capture the Wool"),
  DESTROY_THE_CORE("dtc", "Destroy the Core"),
  ATTACK_DEFEND("ad", "Attack/Defend"),
  BLITZ("blitz", "Blitz"),
  SCOREBOX("scorebox", "Scorebox"),
  GHOST_SQUADRON("gs", "Ghost Squadron"),
  MIXED("mixed", "Mixed Gamemodes");

  private static final Map<String, Gamemode> BY_ID = Maps.newHashMap();

  private final String id;
  private final String name;

  static {
    for (Gamemode gamemode : values()) {
      BY_ID.put(gamemode.getId(), gamemode);
    }
  }

  public static Gamemode byId(String name) {
    return BY_ID.get(name);
  }

}
