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

import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.NameComponent;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.chat.component.builder.NameComponentBuilder;
import ee.ellytr.chat.component.builder.UnlocalizedComponentBuilder;
import ee.ellytr.chat.component.formattable.LocalizedComponent;
import ee.ellytr.chat.component.formattable.UnlocalizedComponent;
import in.twizmwaz.cardinal.component.TeamComponent;
import in.twizmwaz.cardinal.module.team.Team;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

public class Components {

  /**
   * Gets a name component based on a retrieved builder.
   *
   * @param who The user to get the component for.
   * @return The name component of the user.
   */
  public static NameComponent getNameComponent(@NonNull ServerOperator who) {
    return getNameComponentBuilder(who).build();
  }

  /**
   * Gets a builder of a {@link NameComponent}.
   *
   * @param who The user to get the builder for.
   * @return The builder of the name component.
   */
  public static NameComponentBuilder getNameComponentBuilder(@NonNull ServerOperator who) {
    NameComponentBuilder builder = new NameComponentBuilder(who);
    if (who instanceof Player) {
      return builder.color(Team.getTeamColor((Player) who));
    }
    return builder;
  }

  /**
   * Appends a team prefix to the beginning of an array of base components.
   *
   * @param team       The team.
   * @param components The base components.
   * @return The new array of base components.
   */
  public static BaseComponent[] appendTeamPrefix(@NonNull Team team, BaseComponent... components) {
    BaseComponent[] newComponents = new BaseComponent[components.length + 1];
    newComponents[0] = new UnlocalizedComponentBuilder("[{0}] ",
        new TeamComponent(team)).color(team.getColor()).build();
    System.arraycopy(components, 0, newComponents, 1, components.length);
    return newComponents;
  }

  /**
   * Compresses an array of base components into one.
   *
   * @param components The array of components.
   * @return The compressed component.
   */
  public static BaseComponent compress(@NonNull BaseComponent[] components) {
    if (components.length == 0) {
      throw new IllegalArgumentException("Array of components cannot be empty");
    }
    BaseComponent component = components[0];
    for (int i = 1; i < components.length; i++) {
      component.addExtra(components[i]);
    }
    return component;
  }

  public static LocalizedComponent getTimeComponent(int seconds) {
    return getTimeComponentBuilder(seconds).build();
  }

  public static LocalizedComponentBuilder getTimeComponentBuilder(int seconds) {
    return new LocalizedComponentBuilder(ChatConstant.getConstant(seconds == 1 ? "time.second" : "time.seconds"),
        new UnlocalizedComponent(seconds + ""));
  }

}
