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

package in.twizmwaz.cardinal.component.team;

import in.twizmwaz.cardinal.component.BaseComponentBuilder;
import in.twizmwaz.cardinal.module.team.Team;

public class TeamComponentBuilder extends BaseComponentBuilder<TeamComponentBuilder, TeamComponent> {

  private Team team;
  private boolean hover;

  /**
   * Creates a builder of {@link TeamComponent} based on specified values.
   *
   * @param team The team for this component.
   */
  public TeamComponentBuilder(Team team) {
    this.team = team;
    hover = true;
  }

  public TeamComponentBuilder hover(boolean hover) {
    this.hover = hover;
    return this;
  }

  public TeamComponentBuilder getThis() {
    return this;
  }

  /**
   * Builds a {@link TeamComponent} from the specified values.
   *
   * @return The built component.
   */
  public TeamComponent build() {
    TeamComponent component = new TeamComponent(team);
    component.setHover(hover);
    return super.build(component);
  }

}
