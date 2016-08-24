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

package in.twizmwaz.cardinal.util;

import com.google.common.collect.Lists;
import ee.ellytr.chat.component.LanguageComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ChatUtil {

  /**
   * Sends a chat message to a CommandSender.
   * @param sender The CommandSender, player or console.
   * @param components The array of base components to send.
   */
  public static void sendMessage(CommandSender sender, BaseComponent... components) {
    List<BaseComponent> toSend = Lists.newArrayList();
    Locale locale = getLocale(sender);
    for (BaseComponent component : components) {
      if (component instanceof LanguageComponent) {
        toSend.addAll(Arrays.asList(((LanguageComponent) component).getComponents(locale)));
      } else {
        toSend.add(component);
      }
    }
    sender.sendMessage(toSend.toArray(new BaseComponent[toSend.size()]));
  }

  /**
   * Gets the locale of a CommandSender.
   * @param sender The CommandSender.
   * @return The player's locale if it's a player, default locale for console.
   */
  public static Locale getLocale(CommandSender sender) {
    if (sender instanceof Player) {
      return ee.ellytr.chat.util.ChatUtil.getLocale(((Player) sender).getLocale());
    } else {
      return Locale.getDefault();
    }
  }


}
