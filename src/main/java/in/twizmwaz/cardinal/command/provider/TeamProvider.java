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

package in.twizmwaz.cardinal.command.provider;

import com.google.common.collect.Lists;
import ee.ellytr.command.argument.ArgumentProvider;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.module.team.Team;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TeamProvider implements ArgumentProvider<Team> {

  @Override
  public Team getMatch(String input, CommandSender sender) {
    //TODO: Get match from player requesting command match
    return Team.getTeamById(Cardinal.getInstance().getMatchThreads().get(0).getCurrentMatch(), input);
  }

  @Override
  public List<String> getSuggestions(String input, CommandSender sender) {
    List<String> suggestions = Lists.newArrayList();
    //TODO: Get match from player requesting suggestions
    for (Team team : Team.getTeams(Cardinal.getInstance().getMatchThreads().get(0).getCurrentMatch())) {
      String id = team.getId();
      if (id.toLowerCase().startsWith(input.toLowerCase())) {
        suggestions.add(id);
      }
    }
    return suggestions;
  }

}
