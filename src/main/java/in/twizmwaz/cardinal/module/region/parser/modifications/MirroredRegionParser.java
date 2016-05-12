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

package in.twizmwaz.cardinal.module.region.parser.modifications;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionException;
import in.twizmwaz.cardinal.module.region.RegionModule;
import in.twizmwaz.cardinal.module.region.RegionParser;
import in.twizmwaz.cardinal.module.region.exception.attribute.InvalidRegionAttributeException;
import in.twizmwaz.cardinal.module.region.exception.attribute.MissingRegionAttributeException;
import in.twizmwaz.cardinal.module.region.exception.property.MissingRegionPropertyException;
import in.twizmwaz.cardinal.util.Vectors;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.util.Vector;
import org.jdom2.Element;

@Getter
public class MirroredRegionParser implements RegionParser {

  private Region region;
  private final Vector origin;
  private final Vector normal;

  /**
   * Parses an element for a mirrored region.
   *
   * @param element The element.
   * @throws RegionException Thrown if no sub-region is specified, if the origin attribute is invalid, or if the normal
   *                         attribute is missing or invalid.
   */
  public MirroredRegionParser(@NonNull Match match, Element element) throws RegionException {
    RegionModule module = Cardinal.getModule(RegionModule.class);
    for (Element subRegionElement : element.getChildren()) {
      region = module.getRegion(match, subRegionElement);
      if (region != null) {
        break;
      }
    }
    if (region == null && element.getAttribute("region") != null) {
      region = module.getRegionById(match, element.getAttributeValue("region"));
    }
    if (region == null) {
      throw new MissingRegionPropertyException("No sub-region specified for mirrored region", element);
    }

    String originValue = element.getAttributeValue("origin");
    if (originValue == null) {
      origin = new Vector(0, 0, 0);
    } else {
      origin = Vectors.getVector(originValue);
      if (origin == null) {
        throw new InvalidRegionAttributeException("origin", element);
      }
    }

    String normalValue = element.getAttributeValue("normal");
    if (normalValue == null) {
      throw new MissingRegionAttributeException("normal", element);
    }
    normal = Vectors.getVector(normalValue);
    if (normal == null) {
      throw new InvalidRegionAttributeException("normal", element);
    }
  }

}
