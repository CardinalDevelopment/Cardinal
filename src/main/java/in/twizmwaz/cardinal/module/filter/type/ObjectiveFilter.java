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

package in.twizmwaz.cardinal.module.filter.type;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.filter.FilterException;
import in.twizmwaz.cardinal.module.filter.FilterState;
import in.twizmwaz.cardinal.module.filter.LoadLateFilter;
import in.twizmwaz.cardinal.module.filter.parser.ObjectiveFilterParser;
import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.core.Core;
import in.twizmwaz.cardinal.module.objective.destroyable.Destroyable;
import in.twizmwaz.cardinal.module.objective.wool.Wool;
import lombok.RequiredArgsConstructor;
import org.jdom2.Element;

@RequiredArgsConstructor
public class ObjectiveFilter extends AgnosticFilter implements LoadLateFilter {

  private final Element element;
  private Objective objective = null;

  @Override
  public void load(Match match) throws FilterException {
    ObjectiveFilterParser parser = new ObjectiveFilterParser(element, match);
    this.objective = parser.getObjective();
  }

  @Override
  public FilterState evaluate() {
    if (objective == null) {
      return FilterState.ABSTAIN;
    }
    if (objective instanceof Core) {
      return FilterState.fromBoolean(((Core) objective).isComplete());
    } else if (objective instanceof Destroyable) {
      return FilterState.fromBoolean(((Destroyable) objective).isCompleted());
    } else if (objective instanceof Wool) {
      return FilterState.fromBoolean(((Wool) objective).isComplete());
    }
    return FilterState.ABSTAIN;
  }
}
