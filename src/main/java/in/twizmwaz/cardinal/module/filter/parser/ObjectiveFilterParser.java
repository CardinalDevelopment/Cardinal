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

package in.twizmwaz.cardinal.module.filter.parser;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.filter.FilterException;
import in.twizmwaz.cardinal.module.filter.FilterParser;
import in.twizmwaz.cardinal.module.filter.exception.property.InvalidFilterPropertyException;
import in.twizmwaz.cardinal.module.filter.exception.property.MissingFilterPropertyException;
import in.twizmwaz.cardinal.module.objective.Objective;
import lombok.Getter;
import org.jdom2.Element;

@Getter
public class ObjectiveFilterParser implements FilterParser {

  private final Objective objective;

  /**
   * Parses an element for an objective filter.
   *
   * @param element The element.
   * @throws FilterException Thrown if the objective property is missing or invalid.
   */
  public ObjectiveFilterParser(Element element, Match match) throws FilterException {
    String objectiveProperty = element.getText();
    if (objectiveProperty == null) {
      throw new MissingFilterPropertyException("objective", element);
    }
    Objective objective = Objective.getObjectiveById(match, objectiveProperty);
    if (objective == null) {
      throw new InvalidFilterPropertyException("objective", element);
    }
    this.objective = objective;
  }

}
