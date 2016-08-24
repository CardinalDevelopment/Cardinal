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

import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.command.Command;
import ee.ellytr.command.CommandContext;
import ee.ellytr.command.argument.MultiArgs;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.component.map.MapComponentBuilder;
import in.twizmwaz.cardinal.module.cycle.CycleModule;
import in.twizmwaz.cardinal.module.repository.LoadedMap;
import in.twizmwaz.cardinal.util.ChatUtil;
import net.md_5.bungee.api.ChatColor;

public class CommandNext {

  /**
   * Sets the next map.
   *
   * @param cmd The context of this command.
   */
  @Command(aliases = {"setnext", "sn"}, description = "Sets the next map.")
  public static void setNext(CommandContext cmd, @MultiArgs LoadedMap map) {
    Cardinal.getModule(CycleModule.class).getNextCycle(Cardinal.getMatchThread(cmd.getSender())).setMap(map);
  }

  /**
   * Sets the next map.
   *
   * @param cmd The context of this command.
   */
  @Command(aliases = {"next"}, description = "Gets the next map.")
  public static void getNext(CommandContext cmd) {
    LoadedMap map = Cardinal.getModule(CycleModule.class).getNextMap(Cardinal.getMatchThread(cmd.getSender()));
    ChatUtil.sendMessage(cmd.getSender(), new LocalizedComponentBuilder(ChatConstant.getConstant("command.next.map"),
        new MapComponentBuilder(map).color(ChatColor.GOLD).build()).color(ChatColor.DARK_PURPLE).build());
  }

}
