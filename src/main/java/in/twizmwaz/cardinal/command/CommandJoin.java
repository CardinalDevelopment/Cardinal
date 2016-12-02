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

package in.twizmwaz.cardinal.command;

import ee.ellytr.command.Command;
import ee.ellytr.command.CommandContext;
import ee.ellytr.command.PlayerCommand;
import ee.ellytr.command.argument.Optional;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.match.MatchThread;
import in.twizmwaz.cardinal.module.team.SinglePlayerContainer;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.playercontainer.CompetitorContainer;
import in.twizmwaz.cardinal.playercontainer.Containers;
import in.twizmwaz.cardinal.playercontainer.PlayerContainerData;
import org.bukkit.entity.Player;

public class CommandJoin {

  /**
   * Join the game.
   *
   * @param cmd  The context of this command.
   * @param team The team to join, if specified.
   */
  @Command(aliases = "join", description = "Join the game")
  @PlayerCommand
  public static void join(CommandContext cmd, @Optional Team team) {
    Player player = (Player) cmd.getSender();
    MatchThread thread = Cardinal.getMatchThread(player);
    Match match = thread.getCurrentMatch();
    CompetitorContainer playing = team;
    if (!match.isFfa()) {
      if (playing == null) {
        playing = Team.getEmptiestTeam(Team.getTeams(Cardinal.getMatch(player)));
      }
    } else {
      playing = SinglePlayerContainer.of(player);
    }
    PlayerContainerData newData = new PlayerContainerData(thread, match, playing);
    PlayerContainerData oldData = PlayerContainerData.of(player);
    Containers.handleStateChangeEvent(player, oldData, newData);
  }

  /**
   * Leave the game.
   *
   * @param cmd  The context of this command.
   */
  @Command(aliases = "leave", description = "Leave the game")
  @PlayerCommand
  public static void leave(CommandContext cmd) {
    Player player = (Player) cmd.getSender();
    MatchThread thread = Cardinal.getMatchThread(player);
    PlayerContainerData newData = new PlayerContainerData(thread, null, null);
    PlayerContainerData oldData = PlayerContainerData.of(player);
    Containers.handleStateChangeEvent(player, oldData, newData);
  }

}