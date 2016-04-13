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

package in.twizmwaz.cardinal.module.region.parser.unbounded;

import in.twizmwaz.cardinal.module.region.RegionException;
import in.twizmwaz.cardinal.module.region.RegionParser;
import in.twizmwaz.cardinal.module.region.exception.attribute.InvalidRegionAttributeException;
import in.twizmwaz.cardinal.module.region.exception.attribute.MissingRegionAttributeException;
import in.twizmwaz.cardinal.util.Numbers;
import lombok.Getter;
import org.jdom2.Element;

@Getter
public class BelowParser implements RegionParser {

  private final double xAxis;
  private final double yAxis;
  private final double zAxis;

  /**
   * Parses an element for a below region.
   *
   * @param element The element.
   */
  public BelowParser(Element element) throws RegionException {
    String xAxisValue = element.getAttributeValue("x");
    if (xAxisValue == null) {
      throw new MissingRegionAttributeException("x");
    }
    if (!Numbers.isDecimal(xAxisValue)) {
      throw new InvalidRegionAttributeException("x");
    }
    xAxis = Numbers.parseDouble(xAxisValue);

    String yAxisValue = element.getAttributeValue("y");
    if (yAxisValue == null) {
      throw new MissingRegionAttributeException("y");
    }
    if (!Numbers.isDecimal(yAxisValue)) {
      throw new InvalidRegionAttributeException("y");
    }
    yAxis = Numbers.parseDouble(yAxisValue);

    String zAxisValue = element.getAttributeValue("z");
    if (zAxisValue == null) {
      throw new MissingRegionAttributeException("z");
    }
    if (!Numbers.isDecimal(zAxisValue)) {
      throw new InvalidRegionAttributeException("z");
    }
    zAxis = Numbers.parseDouble(zAxisValue);
  }

}
