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

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.module.group.groups.CompetitorGroup;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.objective.OwnedObjective;
import in.twizmwaz.cardinal.module.objective.wool.Wool;
import in.twizmwaz.cardinal.module.scoreboard.displayables.EmptyScoreboardEntry;
import in.twizmwaz.cardinal.module.scoreboard.displayables.OwnedObjectiveScoreboardEntry;
import in.twizmwaz.cardinal.module.scoreboard.displayables.ScoreScoreboardEntry;
import in.twizmwaz.cardinal.module.scoreboard.displayables.ScoreboardEntry;
import in.twizmwaz.cardinal.module.scoreboard.displayables.ScoreboardGroup;
import in.twizmwaz.cardinal.module.scoreboard.displayables.SortedScoreboardGroup;
import in.twizmwaz.cardinal.module.scoreboard.displayables.TeamName;
import in.twizmwaz.cardinal.module.scoreboard.displayables.WoolScoreboardEntry;
import in.twizmwaz.cardinal.module.scores.ScoreModule;
import in.twizmwaz.cardinal.module.team.Team;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ScoreboardDisplay extends ScoreboardGroup {

  Match match;
  @Getter
  CompetitorGroup viewer;
  private final Scoreboard scoreboard;
  @Getter
  private final Objective objective;

  private int maxTeam = 0;
  Set<String> used = new HashSet<>();

  SortedScoreboardGroup blitz;
  SortedScoreboardGroup score = new SortedScoreboardGroup();
  Map<Team, ScoreboardGroup> teams = new HashMap<>();
  ScoreboardGroup sharedFlags;
  ScoreboardGroup hills;

  EmptyScoreboardEntry defaultEntry;

  ScoreboardDisplay(Match match, CompetitorGroup viewer) {
    this.match = match;
    this.viewer = viewer;
    scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    //TODO: proper scoreboard name
    objective = scoreboard.registerNewObjective(ChatColor.AQUA + "Objectives", "dummy");
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    ScoreModule scoreModule = Cardinal.getModule(ScoreModule.class);
    if (scoreModule.hasScoring(match)) {
      scoreModule.getScores(match).forEach(score -> this.score.getEntries().add(new ScoreScoreboardEntry(this, score)));
      score.getEntries().add(new EmptyScoreboardEntry(this));
    }
    for (Team team : Team.getTeams(match)) {
      ScoreboardGroup group = new ScoreboardGroup();
      if (Team.getTeamShownObjectives(match, team).size() > 0) {
        group.getEntries().add(new TeamName(this, team));
        Team.getTeamShownObjectives(match, team).forEach(
            obj -> group.getEntries().add(getEntryForObjective(obj, team)));
        group.getEntries().add(new EmptyScoreboardEntry(this));
      }
      teams.put(team, group);
    }
    defaultEntry = new EmptyScoreboardEntry(this);
    updatePlayerContainers();
  }

  private ScoreboardEntry getEntryForObjective(in.twizmwaz.cardinal.module.objective.Objective objective,
                                               Team attacker) {
    if (objective instanceof Wool) {
      return new WoolScoreboardEntry((Wool) objective, this);
    } else if (objective instanceof OwnedObjective) {
      return new OwnedObjectiveScoreboardEntry((OwnedObjective) objective, this, attacker);
    } else {
      return new ScoreboardEntry(this, ChatColor.RED + "Unknown objective",
          getEntry(ChatColor.RED + "", ChatColor.RED + ""));
    }
  }

  /**
   * Applies the scoreboard to a player.
   * @param player The player.
   */
  public void setScoreboard(Player player) {
    player.setScoreboard(scoreboard);
  }

  /**
   * Updates the scoreboard, reorders teams.
   */
  public void updatePlayerContainers() {
    int index = getSize() - 1;
    if (index == -1) {
      defaultEntry.setScore(0);
    } else {
      defaultEntry.setScore(-1);
    }
    ScoreModule scoreModule = Cardinal.getModule(ScoreModule.class);
    if (scoreModule.hasScoring(match)) {
      score.setScore(index);
      index -= score.getSize();
    }
    // TODO: Show Blitz module
    if (viewer != null && viewer instanceof Team && teams.containsKey(viewer)) {
      ScoreboardGroup group = teams.get(viewer);
      group.setScore(index);
      index -= group.getSize();
    }
    // TODO: get teams in Winning order if viewer is null
    for (Team team : Team.getTeams(match)) {
      if (viewer != null && team.equals(viewer)) {
        continue;
      }
      if (teams.containsKey(team)) {
        ScoreboardGroup group = teams.get(team);
        group.setScore(index);
        index -= group.getSize();
      }
    }
    //TODO: Show Shared Flags
    //TODO: Show Hills
  }

  /**
   * Gets the scoreboard size.
   * @return The scoreboard size.
   */
  public int getSize() {
    int size = 0;
    // TODO: hills
    // TODO: shared flags
    // Team objectives
    for (Team team : Team.getTeams(match)) {
      if (Team.getTeamShownObjectives(match, team).size() > 0) {
        if (size != 0) {
          size++;
        }
        size += Team.getTeamShownObjectives(match, team).size() + 1;
      }
    }
    ScoreModule scoreModule = Cardinal.getModule(ScoreModule.class);
    if (scoreModule.hasScoring(match)) {
      if (size != 0) {
        size++;
      }
      size += scoreModule.getScores(match).size();
    }
    // TODO: blitz
    return size;
  }

  @Override
  public void update() {
    updatePlayerContainers();
  }

  /**
   * Creates a new scoreboard team, used for prefixes and suffixes.
   * @return A new team.
   */
  public org.bukkit.scoreboard.Team getNewTeam() {
    return scoreboard.registerNewTeam("scoreboard-" + maxTeam++);
  }

  /**
   * Creates an unique entry, using the base, and adding repeat as many times as needed until it's unique.
   *
   * <p>Should be always used before passing an entry to a ScoreboardEntry.
   * @param base The base string.
   * @param repeat The string to repeat, if null, random chat colors will be used.
   * @return An unique entry for this scoreboard display.
   */
  public String getEntry(String base, String repeat) {
    String result = base;
    while (used.contains(result)) {
      result += repeat == null ? getRandom() : repeat;
    }
    used.add(result);
    return result;
  }

  private static ChatColor getRandom() {
    return ChatColor.values()[new Random().nextInt(ChatColor.values().length)];
  }

}
