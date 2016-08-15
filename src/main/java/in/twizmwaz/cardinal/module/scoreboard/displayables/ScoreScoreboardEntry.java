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

package in.twizmwaz.cardinal.module.scoreboard.displayables;

import in.twizmwaz.cardinal.module.scoreboard.ScoreboardDisplay;
import in.twizmwaz.cardinal.module.scores.PlayerContainerScore;
import net.md_5.bungee.api.ChatColor;

public class ScoreScoreboardEntry extends SortableScoreboardEntry {

  private final PlayerContainerScore score;

  /**
   * Represents a score for a player container on a scoreboard.
   * @param display The scoreboard display this entry belongs to.
   * @param score The PlayerContainerScore this entry represents.
   */
  public ScoreScoreboardEntry(ScoreboardDisplay display, PlayerContainerScore score) {
    super(display, "", display.getEntry(score.getContainer().getColor() + "", score.getContainer().getColor() + ""));
    this.score = score;
    score.getEntryHolder().addEntry(this);
  }

  @Override
  public void setScore(int newScore) {
    setDisplayName(score.getScore() + (score.getRule().getLimit() > 0
        ? ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + score.getRule().getLimit() : "")
        + " " + score.getContainer().getCompleteName());
    super.setScore(newScore);
  }

  @Override
  public int getSort() {
    return score.getScore();
  }

}
