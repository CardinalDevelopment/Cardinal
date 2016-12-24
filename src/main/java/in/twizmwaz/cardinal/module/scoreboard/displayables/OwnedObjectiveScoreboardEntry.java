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

package in.twizmwaz.cardinal.module.scoreboard.displayables;

import in.twizmwaz.cardinal.module.objective.OwnedObjective;
import in.twizmwaz.cardinal.module.scoreboard.ScoreboardDisplay;
import in.twizmwaz.cardinal.module.team.Team;
import net.md_5.bungee.api.ChatColor;

public class OwnedObjectiveScoreboardEntry extends ScoreboardEntry {

  OwnedObjective objective;
  Team attacker;

  /**
   * Creates a display entry for an OwnedObjective on a scoreboard.
   * @param objective The objective this entry represents.
   * @param display The ScoreboardDisplay this entry belongs to.
   * @param attacker The team scoreboard groups this entry is inside of.
   */
  public OwnedObjectiveScoreboardEntry(OwnedObjective objective, ScoreboardDisplay display, Team attacker) {
    super(display, "", display.getEntry(" " + ChatColor.WHITE + objective.getComponent().toPlainText(), null));
    this.objective = objective;
    this.attacker = attacker;
    if (objective instanceof EntryUpdater) {
      ((EntryUpdater) objective).getEntryHolder().addEntry(this);
    }
  }

  @Override
  public void setScore(int newScore) {
    setPrefix(" " + objective.getPrefix((Team) getDisplay().getViewer(), attacker) + " "
        /* + obj.getProximity(viewer, attacker)*/);
    // NOTE: If viewer is null (observers) and monument is touched, the proximity will be the number of pieces: num/max
    super.setScore(newScore);
  }

}
