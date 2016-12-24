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

package in.twizmwaz.cardinal.module.scores;

import in.twizmwaz.cardinal.module.scoreboard.displayables.EntryHolder;
import in.twizmwaz.cardinal.module.scoreboard.displayables.EntryUpdater;
import in.twizmwaz.cardinal.module.group.groups.CompetitorGroup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerContainerScore implements EntryUpdater {

  @Getter
  private final CompetitorGroup container;
  @Getter
  private final ScoreRule rule;
  private double score = 0;

  @Getter
  private final EntryHolder entryHolder = new EntryHolder();

  public int getScore() {
    return (int) score;
  }

  public double getScoreExact() {
    return score;
  }

  public void setScore(int score) {
    setScore((double) score);
  }

  /**
   * Sets the score for this PlayerContainerScore. It will update it's scoreboard entries.
   * @param score The score this should be set to.
   */
  public void setScore(double score) {
    this.score = score;
    //TODO: if goes up a number, trow event.
    entryHolder.updateEntries();
  }

}
