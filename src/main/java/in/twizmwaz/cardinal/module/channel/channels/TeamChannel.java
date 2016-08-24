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

package in.twizmwaz.cardinal.module.channel.channels;

import ee.ellytr.chat.component.builder.UnlocalizedComponentBuilder;
import in.twizmwaz.cardinal.component.team.TeamComponent;
import in.twizmwaz.cardinal.event.player.PlayerContainerChangeStateEvent;
import in.twizmwaz.cardinal.module.channel.AbstractChannel;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.util.Components;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Getter
@AllArgsConstructor
public class TeamChannel extends AbstractChannel implements Listener {

  private final Team team;

  /**
   * Adds or removes a player from this channel when they change teams.
   *
   * @param event The event.
   */
  @EventHandler
  public void onPlayerChangeTeam(PlayerContainerChangeStateEvent event) {
    Player player = event.getPlayer();
    if (event.getNewData().getPlaying() != null && event.getNewData().getPlaying().equals(team)) {
      addPlayer(player);
    } else {
      removePlayer(player);
    }
  }

  /**
   * Send a message in the team channel with a prefix.
   *
   * @param components The message, without a prefix.
   */
  public void sendPrefixedMessage(BaseComponent... components) {
    super.sendMessage(
        new UnlocalizedComponentBuilder(
            "[{0}] {1}",
            new BaseComponent[]{new TeamComponent(team), Components.compress(components)}
        ).color(team.getColor()).build()
    );
  }
}
