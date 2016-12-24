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

package in.twizmwaz.cardinal.module.scoreboard;


import in.twizmwaz.cardinal.module.group.groups.CompetitorGroup;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.module.group.groups.GroupData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MatchScoreboardManager {

  private final Match match;
  private Map<CompetitorGroup, ScoreboardDisplay> displays = new HashMap<>();
  private ScoreboardDisplay defaultDisplay;

  /**
   * Creates a MatchScoreboardManager for a specific match.
   * @param match The match.
   */
  public MatchScoreboardManager(Match match) {
    this.match = match;
    defaultDisplay = new ScoreboardDisplay(match, null);
    Team.getTeams(match).forEach(this::addPlayerContainerDisplay);
  }

  /**
   * Adds a new ScoreboardDisplay for a CompetitorGroup, used in FFA when a new player joins.
   * @param container The player container.
   */
  public void addPlayerContainerDisplay(CompetitorGroup container) {
    displays.put(container, new ScoreboardDisplay(match, container));
  }

  /**
   * Removes a ScoreboardDisplay for a CompetitorGroup, used in FFA when a new player leaves.
   * @param container The player container.
   */
  public void removePlayerContainerDisplay(CompetitorGroup container) {
    displays.remove(container);
  }

  /**
   * Will update a player to apply a new container data, this will add or remove a player from a scoreboard.
   * @param player The player.
   * @param data The new player container data.
   */
  public void updatePlayer(Player player, GroupData data) {
    if (data.getPlaying() != null && displays.containsKey(data.getPlaying())) {
      displays.get(data.getPlaying()).setScoreboard(player);
    } else {
      defaultDisplay.setScoreboard(player);
    }
  }

}
