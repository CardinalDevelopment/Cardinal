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
import in.twizmwaz.cardinal.event.player.PlayerChangeTeamEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.core.Core;
import in.twizmwaz.cardinal.module.objective.core.CoreModule;
import in.twizmwaz.cardinal.module.objective.destroyable.Destroyable;
import in.twizmwaz.cardinal.module.objective.destroyable.DestroyableModule;
import in.twizmwaz.cardinal.module.objective.wool.Wool;
import in.twizmwaz.cardinal.module.objective.wool.WoolModule;
import in.twizmwaz.cardinal.module.team.Team;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class CardinalScoreboard implements Listener {

  private final Team team;
  private final Scoreboard scoreboard;

  /**
   * A new scoreboard based on a team.
   *
   * @param team The team that is tracked by this scoreboard.
   */
  protected CardinalScoreboard(Team team) {
    this.team = team;
    scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    org.bukkit.scoreboard.Objective objective = scoreboard.registerNewObjective("objectives", "dummy");
    objective.setDisplayName(Cardinal.getModule(ScoreboardModule.class).getDisplayTitle());
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    Team.getTeams().forEach(this::updateTeam);
    Objective.getObjectives().forEach(this::updateObjective);
  }

  /**
   * Sets the player's scoreboard when they change teams.
   *
   * @param event The event.
   */
  @EventHandler
  public void onPlayerChangeTeam(PlayerChangeTeamEvent event) {
    if (event.getNewTeam().equals(team)) {
      event.getPlayer().setScoreboard(scoreboard);
    }
  }

  private void updateTeam(@NonNull Team team) {
    getObjective().getScore(team.getColor() + team.getName()).setScore(getTeamSlot(team));
  }

  private void updateObjective(@NonNull Objective objective) {
    getObjective().getScore(objective.getId()).setScore(getObjectiveSlot(objective));
  }

  @NonNull
  private org.bukkit.scoreboard.Objective getObjective() {
    return scoreboard.getObjective("objectives");
  }

  private int getTeamSlot(Team team) {
    int slot = 0;
    for (Team eachTeam : Team.getTeams()) {
      Match match = Cardinal.getInstance().getMatchThread().getCurrentMatch();
      for (Core ignored : Cardinal.getModule(CoreModule.class).getCores(match)) {
        slot++;
      }
      for (Destroyable ignored : Cardinal.getModule(DestroyableModule.class).getDestroyables(match)) {
        slot++;
      }
      for (Wool ignored : Cardinal.getModule(WoolModule.class).getWools(match)) {
        slot++;
      }

      if (team.equals(eachTeam)) {
        return slot;
      }
      slot++;
    }
    return slot;
  }

  private int getObjectiveSlot(Objective objective) {
    int slot = 0;
    for (Team ignored : Team.getTeams()) {
      Match match = Cardinal.getInstance().getMatchThread().getCurrentMatch();
      for (Core core : Cardinal.getModule(CoreModule.class).getCores(match)) {
        if (objective.equals(core)) {
          return slot;
        }
        slot++;
      }

      for (Destroyable destroyable : Cardinal.getModule(DestroyableModule.class).getDestroyables(match)) {
        if (objective.equals(destroyable)) {
          return slot;
        }
        slot++;
      }

      for (Wool wool : Cardinal.getModule(WoolModule.class).getWools(match)) {
        if (objective.equals(wool)) {
          return slot;
        }
        slot++;
      }

      slot++;
    }
    return slot;
  }

}
