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

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionException;
import in.twizmwaz.cardinal.module.region.RegionModule;
import in.twizmwaz.cardinal.module.region.RegionParser;
import in.twizmwaz.cardinal.module.region.exception.property.MissingRegionPropertyException;
import lombok.Getter;
import org.jdom2.Element;

import java.util.List;

@Getter
public class ComplementRegionParser implements RegionParser {

  private Region region;
  private final List<Region> complements = Lists.newArrayList();

  /**
   * Parses an element for a complement region.
   *
   * @param element The element.
   * @throws RegionException Thrown if no sub-regions are specified.
   */
  public ComplementRegionParser(Match match, Element element) throws RegionException {
    RegionModule module = Cardinal.getModule(RegionModule.class);
    if (element.getAttribute("region") != null) {
      region = module.getRegionById(match, element.getAttributeValue("region"));
    }
    for (Element subRegionElement : element.getChildren()) {
      Region region = module.getRegion(match, subRegionElement);
      if (region != null) {
        if (this.region == null) {
          this.region = region;
        } else {
          complements.add(region);
        }
      }
    }
    if (region == null) {
      throw new MissingRegionPropertyException("No sub-regions specified for complement region", element);
    }
    if (complements.isEmpty()) {
      throw new MissingRegionPropertyException("No complements specified for complement region", element);
    }
  }

}
