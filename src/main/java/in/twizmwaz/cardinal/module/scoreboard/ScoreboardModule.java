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
import com.google.common.collect.Maps;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.core.Core;
import in.twizmwaz.cardinal.module.objective.core.CoreModule;
import in.twizmwaz.cardinal.module.objective.destroyable.Destroyable;
import in.twizmwaz.cardinal.module.objective.destroyable.DestroyableModule;
import in.twizmwaz.cardinal.module.objective.wool.Wool;
import in.twizmwaz.cardinal.module.objective.wool.WoolModule;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.module.team.TeamModule;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.Map;

@ModuleEntry
public class ScoreboardModule extends AbstractModule {

  private Map<Match, List<CardinalScoreboard>> scoreboards = Maps.newHashMap();

  public ScoreboardModule() {
    depends = new Class[]{CoreModule.class, DestroyableModule.class, TeamModule.class, WoolModule.class};
  }

  @Override
  public boolean loadMatch(@NonNull Match match) {
    scoreboards.put(match, Lists.newArrayList());

    Team.getTeams().forEach(team -> {
      CardinalScoreboard scoreboard = new CardinalScoreboard(team);
      Cardinal.registerEvents(scoreboard);
      scoreboards.get(match).add(scoreboard);
    });

    CardinalScoreboard scoreboard = new CardinalScoreboard(null);
    Cardinal.registerEvents(scoreboard);
    scoreboards.get(match).add(scoreboard);

    return true;
  }

  @Override
  public void clearMatch(@NonNull Match match) {
    scoreboards.get(match).forEach(HandlerList::unregisterAll);
    scoreboards.remove(match);
  }

  /**
   * Gets the appropriate display title for the scoreboard.
   *
   * @return The display title.
   */
  @NonNull
  public String getDisplayTitle() {
    String displayTitle = null;
    boolean hasObjectives = false;
    for (Objective objective : Objective.getObjectives()) {
      if (objective.isShow()) {
        hasObjectives = true;
      }
    }
    Class objective = Objective.getSpecificObjective();
    if (hasObjectives) {
      if (objective != null) {
        if (objective.equals(Wool.class)) {
          displayTitle = "Wools";
        } else if (objective.equals(Core.class)) {
          displayTitle = "Cores";
        } else if (objective.equals(Destroyable.class)) {
          displayTitle = "Monuments";
        }
        //TODO: Implement check for hills
      } else {
        displayTitle = "Objectives";
      }
    }
    //TODO: Check for score and blitz modules
    if (displayTitle == null) {
      return ChatColor.RED + "Invalid";
    }
    return ChatColor.AQUA + displayTitle;
  }

}
