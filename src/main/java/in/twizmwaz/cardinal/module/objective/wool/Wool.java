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

package in.twizmwaz.cardinal.module.objective.wool;

import ee.ellytr.chat.component.builder.UnlocalizedComponentBuilder;
import ee.ellytr.chat.component.formattable.UnlocalizedComponent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.ProximityRule;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.scoreboard.displayables.EntryHolder;
import in.twizmwaz.cardinal.module.scoreboard.displayables.EntryUpdater;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.util.Characters;
import in.twizmwaz.cardinal.util.Colors;
import in.twizmwaz.cardinal.util.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.apache.commons.lang.WordUtils;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Wool extends Objective implements Listener, EntryUpdater {

  private final Team team;
  private final DyeColor color;
  private final Region monument;
  private final boolean craftable;
  private final Vector location;
  private final ProximityRule woolProximityRule;
  private final ProximityRule monumentProximityRule;

  private final EntryHolder entryHolder = new EntryHolder();

  private final List<Player> touchedPlayers = new ArrayList<>();

  private boolean touched;
  private boolean complete;

  /**
   * @param match                       The match the wool belongs to.
   * @param id                          This wool's ID.
   * @param required                    Determines if this wool is required to win the match.
   * @param team                        The team that needs to capture this wool.
   * @param color                       The dye color of this wool.
   * @param monument                    The location for where the wool is placed when it is captured.
   * @param craftable                   Determines if this wool may be crafted with white wool and a dye.
   * @param show                        Determines if this wool shows on the scoreboard.
   * @param location                    The location of the wool room, used in proximity calculation.
   * @param woolProximityRule           The proximity rule that determines how to calculate proximity
   *                                    before picking up the wool.
   * @param monumentProximityRule       The proximity rule that determines how to calculate proximity
   *                                    after picking up the wool.
   */
  public Wool(Match match, String id, boolean required, Team team, DyeColor color, Region monument, boolean craftable,
              boolean show, Vector location, ProximityRule woolProximityRule, ProximityRule monumentProximityRule) {
    super(match, id, required, show);
    this.team = team;
    this.color = color;
    this.monument = monument;
    this.craftable = craftable;
    this.location = location;
    this.woolProximityRule = woolProximityRule;
    this.monumentProximityRule = monumentProximityRule;
  }

  /**
   * Gets the wool prefix for a given viewer team.
   * @param viewer The viewer team, null for observers.
   * @return Color and wool state character. Always 3 characters.
   */
  public String getPrefix(Team viewer) {
    String result = Colors.convertDyeToChatColor(color) + "";
    if (isComplete()) {
      result += Characters.WOOL_COMPLETED;
    } else if (isTouched() && (viewer == null || viewer.equals(team))) {
      result += Characters.WOOL_TOUCHED;
    } else {
      result += Characters.WOOL_INCOMPLETE;
    }
    return result;
  }

  @Override
  public UnlocalizedComponent getComponent() {
    return new UnlocalizedComponentBuilder(
        WordUtils.capitalizeFully(Strings.getSimpleName(color.name())) + " Wool"
    ).color(Colors.convertDyeToChatColor(color)).build();
  }

  public boolean hasPlayerTouched(@NonNull Player player) {
    return touchedPlayers.contains(player);
  }

  public void addPlayerTouched(@NonNull Player player) {
    touchedPlayers.add(player);
  }

  public void removePlayerTouched(@NonNull Player player) {
    touchedPlayers.remove(player);
  }

  public void setTouched(boolean touched) {
    this.touched = touched;
    entryHolder.updateEntries();
  }

  public void setComplete(boolean complete) {
    this.complete = complete;
    entryHolder.updateEntries();
  }

}
