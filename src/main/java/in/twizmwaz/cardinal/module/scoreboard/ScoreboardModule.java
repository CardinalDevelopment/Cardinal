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

import in.twizmwaz.cardinal.event.player.PlayerContainerChangeStateEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractListenerModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.objective.core.CoreModule;
import in.twizmwaz.cardinal.module.objective.destroyable.DestroyableModule;
import in.twizmwaz.cardinal.module.objective.wool.WoolModule;
import in.twizmwaz.cardinal.module.scores.ScoreModule;
import in.twizmwaz.cardinal.module.team.TeamModule;
import in.twizmwaz.cardinal.playercontainer.PlayerContainerData;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

@ModuleEntry(
    depends = {TeamModule.class, WoolModule.class, CoreModule.class, DestroyableModule.class, ScoreModule.class})
public class ScoreboardModule extends AbstractListenerModule implements Listener {

  Map<Match, MatchScoreboardManager> scoreboards = new HashMap<>();

  @Override
  public boolean loadMatch(@NonNull Match match) {
    scoreboards.put(match, new MatchScoreboardManager(match));
    return true;
  }

  @Override
  public void clearMatch(@NonNull Match match) {
    scoreboards.remove(match);
  }

  /**
   * Tells the MatchScoreboardManager for the new match to update the player.
   * @param event The PlayerContainerChangeStateEvent.
   */
  @EventHandler
  public void onPlayerChangeContainer(PlayerContainerChangeStateEvent event) {
    PlayerContainerData newData = event.getNewData();
    if (newData.getMatchThread() != null) {
      scoreboards.get(newData.getMatchThread().getCurrentMatch()).updatePlayer(event.getPlayer(), newData);
    }
  }

}
