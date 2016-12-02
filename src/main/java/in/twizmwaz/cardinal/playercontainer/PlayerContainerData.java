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

package in.twizmwaz.cardinal.playercontainer;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.match.MatchThread;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Getter
public class PlayerContainerData {

  private final MatchThread matchThread;
  private final Match match;
  private final CompetitorContainer playing;

  /**
   * Creates new container data under a match thread, match, and player container.
   *
   * @param matchThread The match thread.
   * @param match The match.
   * @param playing The player container.
   */
  public PlayerContainerData(MatchThread matchThread, Match match, CompetitorContainer playing) {
    if (match != null && playing == null) {
      throw new IllegalArgumentException("Playing cannot be null when match is not null");
    }
    this.matchThread = matchThread;
    this.match = match;
    this.playing = playing;
  }

  /**
   * Creates data of a player container for a player.
   *
   * @param player The player.
   * @return The container data.
   */
  public static PlayerContainerData of(@NonNull Player player) {
    MatchThread thread = Cardinal.getMatchThread(player);
    if (thread.getCurrentMatch().hasPlayer(player)) {
      CompetitorContainer container = thread.getCurrentMatch().getPlayingContainer(player);
      return new PlayerContainerData(thread, thread.getCurrentMatch(), container);
    } else {
      return new PlayerContainerData(thread, null, null);
    }
  }

  public static PlayerContainerData empty() {
    return new PlayerContainerData(null, null, null);
  }

}
