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

package in.twizmwaz.cardinal.module.team;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.List;

@ModuleEntry
public class TeamModule extends AbstractModule {

  private List<Team> teams;

  @Override
  public boolean loadMatch(Match match) {
    //TODO
    return true;
  }

  @Override
  public void clearMatch(Match match) {
    //TODO: reimplement
    teams.clear();
  }

  /**
   * @param id The input ID.
   * @return The team that has the input ID.
   */
  public Team getTeamById(@NonNull String id) {
    for (Team team : teams) {
      if (team.getId().replaceAll(" ", "").equalsIgnoreCase(id.replaceAll(" ", ""))) {
        return team;
      }
    }
    for (Team team : teams) {
      if (team.getId().replaceAll(" ", "").toLowerCase().startsWith(
              id.replaceAll(" ", "").toLowerCase())) {
        return team;
      }
    }
    for (Team team : teams) {
      if (team.getId().replaceAll(" ", "-").equalsIgnoreCase(id.replaceAll(" ", "-"))) {
        return team;
      }
    }
    for (Team team : teams) {
      if (team.getId().replaceAll(" ", "-").toLowerCase().startsWith(
              id.replaceAll(" ", "-").toLowerCase())) {
        return team;
      }
    }
    return null;
  }

  /**
   * @param name The input name.
   * @return The team that ha the input name.
   */
  public Team getTeamByName(@NonNull String name) {
    for (Team team : teams) {
      if (team.getName().replaceAll(" ", "").toLowerCase().startsWith(
              name.replaceAll(" ", "").toLowerCase())) {
        return team;
      }
    }
    return null;
  }

  /**
   * @param player The input player.
   * @return The team which the player is on.
   */
  public Team getTeamByPlayer(@NonNull Player player) {
    for (Team team : teams) {
      if (team.contains(player)) {
        return team;
      }
    }
    return null;
  }

}
