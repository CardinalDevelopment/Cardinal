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

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a scoreboard group, this can be any group of lines that should be displayed in the scoreboard.
 */
@Data
public class ScoreboardGroup implements Displayable {

  @Getter
  private int score;
  @Setter
  @Getter
  private List<Displayable> entries = Lists.newArrayList();

  /**
   * This will move the whole list of entries this group has, and put them in a new index, in the same order they had.
   * @param index the top most index.
   */
  @Override
  public void setScore(int index) {
    for (Displayable entry : entries) {
      entry.setScore(index);
      index -= entry.getSize();
    }
  }

  /**
   * Gets the size of the scoreboard group.
   * @return the group size.
   */
  @Override
  public int getSize() {
    int size = 0;
    for (Displayable displayable : entries) {
      size += displayable.getSize();
    }
    return size;
  }

  /**
   * Forces the entries to set their score again. Should be used after changing the entries for them to apply changes.
   */
  @Override
  public void update() {
    setScore(score);
  }

}
