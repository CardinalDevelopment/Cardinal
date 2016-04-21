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

package in.twizmwaz.cardinal.module.scoreboard.slot.objective;

import in.twizmwaz.cardinal.module.objective.wool.Wool;
import in.twizmwaz.cardinal.module.scoreboard.slot.ObjectiveScoreboardSlot;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.util.Characters;
import in.twizmwaz.cardinal.util.Colors;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

@Setter
public class WoolScoreboardSlot extends ObjectiveScoreboardSlot {

  private final Wool wool;
  private final Team viewing;
  private boolean proximity;

  /**
   * Creates a scoreboard slot for a wool.
   *
   * @param wool      The wool objective.
   * @param position  The position on the scoreboard.
   * @param viewing   The team that is viewing the slot.
   * @param proximity If proximity should be shown.
   */
  public WoolScoreboardSlot(@NonNull Wool wool, int position, @NonNull Team viewing, boolean proximity) {
    super(wool, position);
    this.wool = wool;
    this.viewing = viewing;
    this.proximity = proximity;
  }

  @Override
  public String getPrefix() {
    ChatColor color = Colors.convertDyeToChatColor(wool.getColor());
    if (wool.isComplete()) {
      return color + " " + Characters.WOOL_COMPLETED;
    } else if (wool.isTouched() && (wool.getTeam().equals(viewing) || Team.isObservers(viewing))) {
      return color + " " + Characters.WOOL_TOUCHED + (proximity ? " " + getFormattedProximity() : "");
    } else if (wool.getTeam().equals(viewing) || Team.isObservers(viewing) && proximity) {
      return color + " " + Characters.WOOL_INCOMPLETE + (proximity ? " " + getFormattedProximity() : "");
    } else {
      return color + " " + Characters.WOOL_INCOMPLETE;
    }
  }

  private String getFormattedProximity() {
    //TODO: Wool proximity
    return null;
  }

}
