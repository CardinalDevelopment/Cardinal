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

import in.twizmwaz.cardinal.module.filter.FilterException;
import in.twizmwaz.cardinal.module.filter.FilterParser;
import in.twizmwaz.cardinal.module.filter.exception.property.InvalidFilterPropertyException;
import in.twizmwaz.cardinal.module.filter.exception.property.MissingFilterPropertyException;
import in.twizmwaz.cardinal.module.filter.type.CauseFilter;
import lombok.Getter;
import org.jdom2.Element;

@Getter
public class CauseFilterParser implements FilterParser {

  private final CauseFilter.EventCause cause;

  /**
   * Parses an element for a cause filter.
   *
   * @param element The element.
   * @throws FilterException Thrown if the cause property is missing or invalid.
   */
  public CauseFilterParser(Element element) throws FilterException {
    String causeProperty = element.getText();
    if (causeProperty == null) {
      throw new MissingFilterPropertyException("cause", element);
    }
    CauseFilter.EventCause cause = CauseFilter.EventCause.getEventCause(causeProperty);
    if (cause == null) {
      throw new InvalidFilterPropertyException("cause", element);
    }
    this.cause = cause;
  }

}
