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

package in.twizmwaz.cardinal.module.region.parser;

import in.twizmwaz.cardinal.module.region.RegionException;
import in.twizmwaz.cardinal.module.region.RegionParser;
import in.twizmwaz.cardinal.module.region.exception.attribute.InvalidRegionAttributeException;
import in.twizmwaz.cardinal.module.region.exception.attribute.MissingRegionAttributeException;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.Vectors;
import lombok.Getter;
import org.bukkit.util.Vector;
import org.jdom2.Element;

import java.util.List;

@Getter
public class CircleRegionParser implements RegionParser {

  private final Vector center;
  private final double radius;

  /**
   * Parses an element for a circle region.
   *
   * @param element The element.
   * @throws RegionException Thrown if the center or radius attributes are missing or invalid.
   */
  public CircleRegionParser(Element element) throws RegionException {
    String centerValue = element.getAttributeValue("center");
    if (centerValue == null) {
      throw new MissingRegionAttributeException("center");
    }
    List<Double> coords = Vectors.getCoordinates(centerValue);
    if (coords == null || coords.size() != 2) {
      throw new InvalidRegionAttributeException("center");
    }
    center = new Vector(coords.get(0), 0, coords.get(1));

    String radiusValue = element.getAttributeValue("radius");
    if (radiusValue == null) {
      throw new MissingRegionAttributeException("radius");
    }
    if (!Numbers.isDecimal(radiusValue)) {
      throw new InvalidRegionAttributeException("radius");
    }
    radius = Numbers.parseDouble(radiusValue);
  }

}
