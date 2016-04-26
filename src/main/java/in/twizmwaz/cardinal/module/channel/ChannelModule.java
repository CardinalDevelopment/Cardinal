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

package in.twizmwaz.cardinal.module.channel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.channel.channels.GlobalChannel;
import in.twizmwaz.cardinal.module.channel.channels.PlayerChannel;
import in.twizmwaz.cardinal.module.channel.channels.TeamChannel;
import in.twizmwaz.cardinal.module.event.ModuleLoadCompleteEvent;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.module.team.TeamModule;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Map;

@ModuleEntry
public class ChannelModule extends AbstractModule implements Listener {

  private GlobalChannel globalChannel;
  private Map<Match, List<TeamChannel>> teamChannels = Maps.newHashMap();
  private Map<Player, PlayerChannel> playerChannels = Maps.newHashMap();

  /**
   * Constructor for the channel module.
   */
  public ChannelModule() {
    this.depends = new Class[]{TeamModule.class};

    Cardinal.registerEvents(this);
  }

  /**
   * Registers a global channel whenever modules are loaded.
   *
   * @param event The event.
   */
  @EventHandler
  public void onModuleLoadComplete(ModuleLoadCompleteEvent event) {
    GlobalChannel channel = new GlobalChannel();
    Cardinal.registerEvents(channel);
    globalChannel = channel;
  }

  /**
   * Adds a new player channel whenever a player joins the server.
   *
   * @param event The event.
   */
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    playerChannels.put(player, new PlayerChannel(player));
  }

  /**
   * Removes the player channel of a player that quits the server.
   *
   * @param event The event.
   */
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    playerChannels.remove(event.getPlayer());
  }

  @Override
  public boolean loadMatch(@NonNull Match match) {
    List<TeamChannel> teamChannels = Lists.newArrayList();
    for (Team team : Team.getTeams()) {
      TeamChannel channel = new TeamChannel(team);
      Cardinal.registerEvents(channel);
      teamChannels.add(channel);
    }
    return true;
  }

  @Override
  public void clearMatch(@NonNull Match match) {
    teamChannels.get(match).forEach(HandlerList::unregisterAll);
    teamChannels.remove(match);
  }

  public GlobalChannel getGlobalChannel() {
    return globalChannel;
  }

  public PlayerChannel getPlayerChannel(@NonNull Player player) {
    return playerChannels.get(player);
  }

  /**
   * Gets the channel of a team for a certain match.
   *
   * @param match The match.
   * @param team  The team.
   * @return The team's channel.
   */
  public TeamChannel getTeamChannel(@NonNull Match match, @NonNull Team team) {
    for (TeamChannel channel : teamChannels.get(match)) {
      if (channel.getTeam().equals(team)) {
        return channel;
      }
    }
    return null;
  }

}
