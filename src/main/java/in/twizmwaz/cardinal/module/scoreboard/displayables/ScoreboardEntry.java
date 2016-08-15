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
import in.twizmwaz.cardinal.util.Strings;
import lombok.Getter;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

/**
 * Represents a single line in a scoreboard, prefix, entry, suffix, and a score.
 */
public class ScoreboardEntry implements Displayable {

  @Getter
  private final ScoreboardDisplay display;
  private final Objective objective;
  private final Team team;
  private Score score;

  @Getter
  private boolean shown = true;

  /**
   * Creates a ScoreboardEntry with a text to display.
   * Entry should be non-visible characters (colors) and must be unique.
   *
   * @param display The ScoreboardDisplay that owns this entry.
   * @param displayName The display name, will be split between prefix and suffix.
   * @param entry The entry.
   */
  public ScoreboardEntry(ScoreboardDisplay display, String displayName, String entry) {
    this(display, Strings.trim(displayName, 0, 16), entry, Strings.trim(displayName, 16, 32));
  }

  /**
   * Creates a ScoreboardEntry with a text to display.
   * Entry must be unique.
   *
   * @param display The ScoreboardDisplay that owns this entry.
   * @param prefix The prefix to use.
   * @param entry The entry to use.
   * @param suffix The suffix to use.
   */
  public ScoreboardEntry(ScoreboardDisplay display, String prefix, String entry, String suffix) {
    this.display = display;
    this.objective = display.getObjective();
    this.team = display.getNewTeam();
    team.addEntry(entry);
    this.score = objective.getScore(entry);
    setScore(0, true);
    setPrefix(prefix);
    setSuffix(suffix);
  }

  @Override
  public int getScore() {
    return score.getScore();
  }

  @Override
  public void setScore(int newScore) {
    setScore(newScore, false);
  }

  private void setScore(int newScore, boolean force) {
    if (newScore != getScore() || force) {
      if (newScore == 0) {
        score.setScore(-1);
      }
      score.setScore(newScore);
    }
  }

  @Override
  public void update() {
    setScore(getScore());
  }

  @Override
  public int getSize() {
    return shown ? 1 : 0;
  }

  /**
   * Gets the current prefix.
   * @return The current prefix.
   */
  public String getPrefix() {
    return team.getPrefix();
  }

  /**
   * This will set a new prefix, can't be longer than 16 chars, or words will be removed until it fits.
   * @param prefix The new prefix.
   */
  public void setPrefix(String prefix) {
    while (prefix.length() > 16) {
      prefix = Strings.removeLastWord(prefix);
    }
    if (!prefix.equals(getPrefix())) {
      team.setPrefix(prefix);
    }
  }

  /**
   * Gets the current entry.
   * @return The current entry.
   */
  public String getEntry() {
    return score.getEntry();
  }

  /**
   * This will replace the entry, take into account that this will make the scoreboard flicker.
   *
   * <p>IMPORTANT the new entry passed shouldn't be used anywhere else, or it will create problems.
   * @param newEntry The new entry.
   */
  public void setEntry(String newEntry) {
    if (!newEntry.equals(getEntry())) {
      Score old = score;
      objective.getScoreboard().resetScores(old.getEntry());
      team.removeEntry(old.getEntry());
      team.addEntry(newEntry);
      this.score = objective.getScore(newEntry);
      setScore(old.getScore(), true);
      if (!isShown()) {
        shown = true;
        hide();
      }
    }
  }

  /**
   * Gets the current suffix.
   * @return The current suffix.
   */
  public String getSuffix() {
    return team.getSuffix();
  }

  /**
   * This will set a new suffix, can't be longer than 16 chars, or words will be removed until it fits.
   * @param suffix The new suffix.
   */
  public void setSuffix(String suffix) {
    while (suffix.length() > 16) {
      suffix = Strings.removeLastWord(suffix);
    }
    if (!suffix.equals(getSuffix())) {
      team.setSuffix(suffix);
    }
  }

  /**
   * This will change the prefix and suffix, to get a display name. Entry should be non-visible characters (colors).
   * @param displayName The new prefix.
   */
  public void setDisplayName(String displayName) {
    setPrefix(Strings.trim(displayName, 0, 16));
    setSuffix(Strings.trim(displayName, 16, 32));
  }

  /**
   * Hides this scoreboard entry, it won't show on the scoreboard, but it will still exist.
   */
  public void hide() {
    if (isShown()) {
      objective.getScoreboard().resetScores(score.getEntry());
      shown = false;
    }
  }

  /**
   * Shows this scoreboard entry again, after being hid by running hide().
   */
  public void show() {
    if (!isShown()) {
      Score old = score;
      this.score = objective.getScore(old.getEntry());
      setScore(old.getScore(), true);
      shown = true;
    }
  }

}
