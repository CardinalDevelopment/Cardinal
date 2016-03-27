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

import ee.ellytr.chat.component.NameComponent;
import ee.ellytr.chat.component.NameComponentBuilder;
import in.twizmwaz.cardinal.module.team.Team;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
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
    if (who instanceof OfflinePlayer && ((OfflinePlayer) who).isOnline()) {
      return builder.color(Team.getTeamColor((Player) who));
    }
    return builder;
  }

}
