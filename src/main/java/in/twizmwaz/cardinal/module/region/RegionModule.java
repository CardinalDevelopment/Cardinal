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

package in.twizmwaz.cardinal.module.region;

import com.google.common.collect.Maps;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.region.exception.RegionAttributeException;
import in.twizmwaz.cardinal.module.region.parser.bounded.BlockParser;
import in.twizmwaz.cardinal.module.region.parser.bounded.CuboidParser;
import in.twizmwaz.cardinal.module.region.parser.unbounded.AboveParser;
import in.twizmwaz.cardinal.module.region.parser.unbounded.BelowParser;
import in.twizmwaz.cardinal.module.region.parser.unbounded.CircleParser;
import in.twizmwaz.cardinal.module.region.parser.unbounded.HalfParser;
import in.twizmwaz.cardinal.module.region.parser.unbounded.RectangleParser;
import in.twizmwaz.cardinal.module.region.type.bounded.BlockRegion;
import in.twizmwaz.cardinal.module.region.type.bounded.CuboidRegion;
import in.twizmwaz.cardinal.module.region.type.unbounded.AboveRegion;
import in.twizmwaz.cardinal.module.region.type.unbounded.BelowRegion;
import in.twizmwaz.cardinal.module.region.type.unbounded.CircleRegion;
import in.twizmwaz.cardinal.module.region.type.unbounded.EmptyRegion;
import in.twizmwaz.cardinal.module.region.type.unbounded.EverywhereRegion;
import in.twizmwaz.cardinal.module.region.type.unbounded.HalfRegion;
import in.twizmwaz.cardinal.module.region.type.unbounded.NowhereRegion;
import in.twizmwaz.cardinal.module.region.type.unbounded.RectangleRegion;
import lombok.NonNull;
import org.jdom2.Element;

import java.util.Map;

@ModuleEntry
public class RegionModule extends AbstractModule {

  private Map<Match, Map<String, Region>> regions = Maps.newHashMap();

  /**
   * @param id The ID of the region that is returned.
   * @return The region that has the given ID.
   */
  public Region getRegionById(@NonNull Match match, @NonNull String id) {
    return regions.get(match).get(id);
  }

  /**
   * Parses a region from an element.
   *
   * @param element             The element.
   * @param alternateAttributes Any alternative attributes to look for regions from.
   * @return The parsed region.
   * @throws RegionAttributeException Thrown if there are missing or invalid attributes for a region.
   */
  public Region getRegion(Match match, Element element, String... alternateAttributes) throws RegionException {
    switch (element.getName()) {
      case "cuboid":
        return new CuboidRegion(new CuboidParser(element));
      case "cylinder":
        //TODO
        return null;
      case "block":
        return new BlockRegion(new BlockParser(element));
      case "sphere":
        //TODO
        return null;
      case "rectangle":
        return new RectangleRegion(new RectangleParser(element));
      case "circle":
        return new CircleRegion(new CircleParser(element));
      case "half":
        return new HalfRegion(new HalfParser(element));
      case "below":
        return new BelowRegion(new BelowParser(element));
      case "above":
        return new AboveRegion(new AboveParser(element));
      case "empty":
        return new EmptyRegion();
      case "nowhere":
        return new NowhereRegion();
      case "everywhere":
        return new EverywhereRegion();
      default:
        for (String alternateAttribute : alternateAttributes) {
          String regionValue = element.getAttributeValue(alternateAttribute);
          if (regionValue != null) {
            Region region = getRegionById(match, regionValue);
            if (region != null) {
              return region;
            }
          }
        }

        String regionValue = element.getAttributeValue("id");
        if (regionValue != null) {
          Region region = getRegionById(match, regionValue);
          if (region != null) {
            return region;
          }
        }
    }
    return null;
  }

}
