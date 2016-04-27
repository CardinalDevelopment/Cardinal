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

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

public class Colors {

  /**
   * Converts a {@link DyeColor} into a returned {@link ChatColor}.
   *
   * @param color The dye color to convert.
   * @return The converted chat color.
   */
  public static ChatColor convertDyeToChatColor(DyeColor color) {
    switch (color) {
      case WHITE:
        return ChatColor.WHITE;
      case ORANGE:
        return ChatColor.GOLD;
      case MAGENTA:
        return ChatColor.LIGHT_PURPLE;
      case LIGHT_BLUE:
        return ChatColor.BLUE;
      case YELLOW:
        return ChatColor.YELLOW;
      case LIME:
        return ChatColor.GREEN;
      case PINK:
        return ChatColor.RED;
      case GRAY:
        return ChatColor.GRAY;
      case SILVER:
        return ChatColor.GRAY;
      case CYAN:
        return ChatColor.DARK_AQUA;
      case PURPLE:
        return ChatColor.DARK_PURPLE;
      case BLUE:
        return ChatColor.DARK_BLUE;
      case BROWN:
        return ChatColor.GOLD;
      case GREEN:
        return ChatColor.DARK_GREEN;
      case RED:
        return ChatColor.DARK_RED;
      case BLACK:
        return ChatColor.BLACK;
      default:
        return ChatColor.RESET;
    }
  }


  /**
   * Converts a Bungee {@link ChatColor} to a Bukkit {@link DyeColor}
   *
   * @param chatColor The chat color to convert.
   * @return The dye color to convert.
   */
  public static DyeColor convertChatColorToDyeColor(ChatColor chatColor) {
    switch (chatColor) {
      case WHITE:
        return DyeColor.WHITE;
      case AQUA:
        return DyeColor.LIGHT_BLUE;
      case GOLD:
        return DyeColor.ORANGE;
      case LIGHT_PURPLE:
        return DyeColor.MAGENTA;
      case YELLOW:
        return DyeColor.YELLOW;
      case GREEN:
        return DyeColor.LIME;
      case RED:
        return DyeColor.PINK;
      case GRAY:
        return DyeColor.SILVER;
      case DARK_GRAY:
        return DyeColor.GRAY;
      case DARK_AQUA:
        return DyeColor.CYAN;
      case DARK_PURPLE:
        return DyeColor.PURPLE;
      case DARK_BLUE:
        return DyeColor.BLUE;
      case BLUE:
        return DyeColor.BLUE;
      case DARK_GREEN:
        return DyeColor.GREEN;
      case DARK_RED:
        return DyeColor.RED;
      case BLACK:
        return DyeColor.BLACK;
      default:
        return DyeColor.WHITE;
    }
  }

  /**
   * Converts a RGB color string to a {@link Color}.
   *
   * @param color The input RGB string.
   * @return The corresponding color.
   */
  public static Color convertHexToRgb(String color) {
    return Color.fromRGB(Integer.valueOf(color.substring(0, 2), 16),
        Integer.valueOf(color.substring(2, 4), 16),
        Integer.valueOf(color.substring(4, 6), 16));
  }

}
