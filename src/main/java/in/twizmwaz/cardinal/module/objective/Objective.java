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

package in.twizmwaz.cardinal.module.objective;

import ee.ellytr.chat.component.formattable.UnlocalizedComponent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.id.IdModule;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public abstract class Objective {

  @NonNull
  protected final Match match;
  private final String id;
  private final boolean required;
  protected final boolean show;

  /**
   * Gets the objective display name.
   * @return The display name.
   */
  public abstract UnlocalizedComponent getComponent();

  /**
   * Gets the objectives in the current match.
   *
   * @return The list of objectives.
   */
  public static List<Objective> getObjectives(@NonNull Match match) {
    return IdModule.get().getList(match, Objective.class);
  }

  /**
   * Gets the specific objective for this match. Returns null if there are multiple types of objectives.
   *
   * @return The class of the specific objective.
   */
  public static Class getSpecificObjective(@NonNull Match match) {
    Class specificObjective = null;
    for (Objective objective : getObjectives(match)) {
      Class objectiveClass = objective.getClass();
      if (specificObjective == null) {
        specificObjective = objectiveClass;
      } else if (specificObjective != objectiveClass) {
        specificObjective = null;
        break;
      }
    }
    return specificObjective;
  }

  /**
   * Gets the objective with the id on a specific match;
   *
   * @return The objective. Null if no objective is found with that id.
   */
  public static Objective getObjectiveById(@NonNull Match match, @NonNull String id) {
    return IdModule.get().get(match, id, Objective.class);
  }

}
