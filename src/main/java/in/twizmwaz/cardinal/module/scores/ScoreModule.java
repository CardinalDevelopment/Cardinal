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

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.module.team.TeamModule;
import in.twizmwaz.cardinal.playercontainer.PlayingPlayerContainer;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.ParseUtil;
import lombok.NonNull;
import org.jdom2.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleEntry(depends = {TeamModule.class})
public class ScoreModule extends AbstractModule {

  Map<Match, ScoreRule> rules = new HashMap<>();
  Map<Match, List<PlayerContainerScore>> scores = new HashMap<>();

  @Override
  public boolean loadMatch(@NonNull Match match) {
    for (Element score : match.getMap().getDocument().getRootElement().getChildren("score")) {
      int limit = Numbers.parseInteger(ParseUtil.getFirstAttribute("limit", score), -1);
      int kill = Numbers.parseInteger(ParseUtil.getFirstAttribute("kills", score), 0);
      int death = Numbers.parseInteger(ParseUtil.getFirstAttribute("deaths", score), 0);
      if (!rules.containsKey(match)) {
        rules.put(match, new ScoreRule(true, limit, kill, death));
      } else {
        errors.add(new ModuleError(this, match.getMap(), new String[]{"Multiple scores found for this map"}, false));
      }
    }
    if (!rules.containsKey(match)) {
      rules.put(match, new ScoreRule(false, -1, 0, 0));
    }
    scores.put(match, Lists.newArrayList());
    Team.getTeams(match).forEach(team -> addScoreModule(match, team));
    return true;
  }

  /**
   * Creates an score for a player container, used when a new player joins in ffa.
   * @param match The match.
   * @param container The container to create a score for.
   */
  public void addScoreModule(Match match, PlayingPlayerContainer container) {
    PlayerContainerScore score = getScore(match, container);
    if (score == null) {
      scores.get(match).add(new PlayerContainerScore(container, rules.get(match)));
    }
  }

  /**
   * Removes an score for a player container, used when a player leaves in ffa.
   * @param match The match.
   * @param container The container to remove the score from.
   */
  public void removeScoreModule(Match match, PlayingPlayerContainer container) {
    PlayerContainerScore score = getScore(match, container);
    if (score != null) {
      scores.get(match).remove(score);
    }
  }

  public boolean hasScoring(Match match) {
    return rules.get(match).isScoring();
  }

  /**
   * Returns a score for a PlayingPlayerContainer in a match.
   * @param match The match.
   * @param container The PlayingPlayerContainer
   * @return A PlayerContainerScore for that container.
   */
  public PlayerContainerScore getScore(Match match, PlayingPlayerContainer container) {
    for (PlayerContainerScore score : scores.get(match)) {
      if (score.getContainer().equals(container)) {
        return score;
      }
    }
    return null;
  }

  public List<PlayerContainerScore> getScores(Match match) {
    return scores.get(match);
  }

  //TODO: kills, deaths, and scoreboxes (events)

}
