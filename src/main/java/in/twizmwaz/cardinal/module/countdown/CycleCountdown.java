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

package in.twizmwaz.cardinal.module.countdown;

import com.google.common.collect.Maps;
import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.chat.component.builder.UnlocalizedComponentBuilder;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.match.MatchState;
import in.twizmwaz.cardinal.match.MatchThread;
import in.twizmwaz.cardinal.module.cycle.CycleModule;
import in.twizmwaz.cardinal.module.repository.LoadedMap;
import in.twizmwaz.cardinal.util.Channels;
import in.twizmwaz.cardinal.util.Components;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.Map;

@Getter
public class CycleCountdown implements Cancellable, Runnable {

  @NonNull
  private final Match match;
  private final Map<Player, BossBar> bossBars = Maps.newHashMap();

  @Setter
  private MatchState originalState;
  private boolean cancelled = true;
  private int time;
  private int originalTime;

  public CycleCountdown(Match match) {
    this.match = match;
  }

  @Override
  public void run() {
    if (time == 0) {
      cancelled = true;

      CycleModule cycleModule = Cardinal.getModule(CycleModule.class);
      MatchThread matchThread = Cardinal.getMatchThread(match);
      LoadedMap map = cycleModule.getNextMap(matchThread);

      cycleModule.cycle(matchThread);
      Channels.getGlobalChannel(matchThread).sendMessage(new LocalizedComponentBuilder(
          ChatConstant.getConstant("cycle.cycled"),
          new UnlocalizedComponentBuilder(map.getName()).color(ChatColor.AQUA).build())
          .color(ChatColor.DARK_AQUA).build());
    } else if (!cancelled) {
      MatchThread matchThread = Cardinal.getMatchThread(match);

      Channels.getGlobalChannel(matchThread).sendMessage(new LocalizedComponentBuilder(
          ChatConstant.getConstant("cycle.cycling"),
          new UnlocalizedComponentBuilder(Cardinal.getModule(CycleModule.class)
              .getNextMap(matchThread).getName()).color(ChatColor.AQUA).build(),
          Components.getTimeComponentBuilder(time).color(ChatColor.DARK_RED).build()
      ).color(ChatColor.DARK_AQUA).build());
      time--;
      Bukkit.getScheduler().runTaskLaterAsynchronously(Cardinal.getInstance(), this, 20);
    }
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
    if (!cancelled) {
      Bukkit.getScheduler().runTaskAsynchronously(Cardinal.getInstance(), this);
    }
  }

  public void setTime(int time) {
    this.time = time;
    originalTime = time;
  }

}
