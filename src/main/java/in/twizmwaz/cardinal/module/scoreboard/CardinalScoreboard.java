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

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.event.player.PlayerChangeTeamEvent;
import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.core.Core;
import in.twizmwaz.cardinal.module.objective.wool.Wool;
import in.twizmwaz.cardinal.module.scoreboard.slot.BlankScoreboardSlot;
import in.twizmwaz.cardinal.module.scoreboard.slot.EntryScoreboardSlot;
import in.twizmwaz.cardinal.module.scoreboard.slot.ObjectiveScoreboardSlot;
import in.twizmwaz.cardinal.module.scoreboard.slot.TeamScoreboardSlot;
import in.twizmwaz.cardinal.module.scoreboard.slot.objective.CoreScoreboardSlot;
import in.twizmwaz.cardinal.module.scoreboard.slot.objective.WoolScoreboardSlot;
import in.twizmwaz.cardinal.module.team.Team;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public class CardinalScoreboard implements Listener {

  private final Team team;
  private final Scoreboard scoreboard;
  private final List<ScoreboardSlot> slots = Lists.newArrayList();

  /**
   * A new scoreboard based on a team.
   *
   * @param team The team that is tracked by this scoreboard.
   */
  protected CardinalScoreboard(Team team) {
    this.team = team;
    scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    Team.getTeams().forEach(scoreboardTeam -> {
      org.bukkit.scoreboard.Team registered = scoreboard.registerNewTeam(scoreboardTeam.getId());
      registered.setPrefix(scoreboardTeam.getColor() + "");
    });

    if (team == null) {
      return;
    }
    org.bukkit.scoreboard.Objective objective = scoreboard.registerNewObjective("objectives", "dummy");
    objective.setDisplayName(Cardinal.getModule(ScoreboardModule.class).getDisplayTitle());
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    //TODO: Check for time limit module
    boolean proximity = true;

    int position = 0;
    List<String> used = Lists.newArrayList();
    for (Team scoreboardTeam : Team.getTeams()) {
      List<Objective> objectives = Team.getTeamObjectives(scoreboardTeam);
      if (!Team.isObservers(scoreboardTeam) && objectives.size() > 0) {
        if (position != 0) {
          String base = BlankScoreboardSlot.getNextBlankBase(used);
          used.add(base);
          slots.add(new BlankScoreboardSlot(base, position));
          position++;
        }
        for (Objective scoreboardObj : objectives) {
          if (scoreboardObj instanceof Core) {
            slots.add(new CoreScoreboardSlot((Core) scoreboardObj, position, team, proximity));
            position++;
          } else if (scoreboardObj instanceof Wool) {
            slots.add(new WoolScoreboardSlot((Wool) scoreboardObj, position, team, proximity));
            position++;
          }
        }
        String base = TeamScoreboardSlot.getNextTeamBase(scoreboardTeam, used);
        used.add(base);
        slots.add(new TeamScoreboardSlot(scoreboardTeam, base, position));
        position++;
      }
      position++;
    }
    slots.forEach(this::updateSlot);
  }

  /**
   * Sets the player's scoreboard when they change teams.
   *
   * @param event The event.
   */
  @EventHandler
  public void onPlayerChangeTeam(PlayerChangeTeamEvent event) {
    Team oldTeam = event.getOldTeam();
    Player player = event.getPlayer();
    String entry = player.getName();
    if (oldTeam != null) {
      scoreboard.getTeam(oldTeam.getId()).removeEntry(entry);
    }
    Team newTeam = event.getNewTeam();
    scoreboard.getTeam(newTeam.getId()).addEntry(entry);
    if (newTeam.equals(team)) {
      player.setScoreboard(scoreboard);
    }
  }

  private void updateSlot(@NonNull ScoreboardSlot slot) {
    String base = slot.getBase();
    if (slot instanceof EntryScoreboardSlot) {
      String id = null;
      if (slot instanceof TeamScoreboardSlot) {
        id = ((TeamScoreboardSlot) slot).getTeam().getId() + "-t";
      } else if (slot instanceof ObjectiveScoreboardSlot) {
        id = ((ObjectiveScoreboardSlot) slot).getObjective().getId() + "-o";
      }
      org.bukkit.scoreboard.Team team = scoreboard.getTeam(id);
      if (team == null) {
        team = scoreboard.registerNewTeam(id);
      }
      if (!team.hasEntry(base)) {
        team.addEntry(base);
      }
      EntryScoreboardSlot entrySlot = (EntryScoreboardSlot) slot;
      team.setPrefix(entrySlot.getPrefix());
      team.setSuffix(entrySlot.getSuffix());
    }
    getObjective().getScore(base).setScore(slot.getPosition());
  }

  @NonNull
  private org.bukkit.scoreboard.Objective getObjective() {
    return scoreboard.getObjective("objectives");
  }

  private TeamScoreboardSlot getTeamSlot(@NonNull Team team) {
    for (ScoreboardSlot slot : slots) {
      if (slot instanceof TeamScoreboardSlot) {
        TeamScoreboardSlot teamSlot = (TeamScoreboardSlot) slot;
        if (teamSlot.getTeam().equals(team)) {
          return teamSlot;
        }
      }
    }
    return null;
  }

  private ObjectiveScoreboardSlot getObjectiveSlot(@NonNull Objective objective) {
    for (ScoreboardSlot slot : slots) {
      if (slot instanceof ObjectiveScoreboardSlot) {
        ObjectiveScoreboardSlot objSlot = (ObjectiveScoreboardSlot) slot;
        if (objSlot.getObjective().equals(objective)) {
          return objSlot;
        }
      }
    }
    return null;
  }

  public boolean isCompact() {
    //TODO: Check if more than 16 slots in original scoreboard
    return false;
  }

}
