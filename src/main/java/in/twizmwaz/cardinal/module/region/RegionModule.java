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
import com.google.common.collect.Sets;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.region.exception.RegionAttributeException;
import in.twizmwaz.cardinal.module.region.exception.RegionPropertyException;
import in.twizmwaz.cardinal.module.region.exception.attribute.InvalidRegionAttributeException;
import in.twizmwaz.cardinal.module.region.exception.attribute.MissingRegionAttributeException;
import in.twizmwaz.cardinal.module.region.exception.property.InvalidRegionPropertyException;
import in.twizmwaz.cardinal.module.region.exception.property.MissingRegionPropertyException;
import in.twizmwaz.cardinal.module.region.parser.AboveRegionParser;
import in.twizmwaz.cardinal.module.region.parser.BelowRegionParser;
import in.twizmwaz.cardinal.module.region.parser.BlockRegionParser;
import in.twizmwaz.cardinal.module.region.parser.CircleRegionParser;
import in.twizmwaz.cardinal.module.region.parser.CuboidRegionParser;
import in.twizmwaz.cardinal.module.region.parser.CylinderRegionParser;
import in.twizmwaz.cardinal.module.region.parser.HalfRegionParser;
import in.twizmwaz.cardinal.module.region.parser.RectangleRegionParser;
import in.twizmwaz.cardinal.module.region.parser.SphereRegionParser;
import in.twizmwaz.cardinal.module.region.parser.modifications.ComplementRegionParser;
import in.twizmwaz.cardinal.module.region.parser.modifications.IntersectRegionParser;
import in.twizmwaz.cardinal.module.region.parser.modifications.MirroredRegionParser;
import in.twizmwaz.cardinal.module.region.parser.modifications.NegativeRegionParser;
import in.twizmwaz.cardinal.module.region.parser.modifications.TranslatedRegionParser;
import in.twizmwaz.cardinal.module.region.parser.modifications.UnionRegionParser;
import in.twizmwaz.cardinal.module.region.type.AboveRegion;
import in.twizmwaz.cardinal.module.region.type.BelowRegion;
import in.twizmwaz.cardinal.module.region.type.BlockRegion;
import in.twizmwaz.cardinal.module.region.type.CircleRegion;
import in.twizmwaz.cardinal.module.region.type.CuboidRegion;
import in.twizmwaz.cardinal.module.region.type.CylinderRegion;
import in.twizmwaz.cardinal.module.region.type.EmptyRegion;
import in.twizmwaz.cardinal.module.region.type.EverywhereRegion;
import in.twizmwaz.cardinal.module.region.type.HalfRegion;
import in.twizmwaz.cardinal.module.region.type.NowhereRegion;
import in.twizmwaz.cardinal.module.region.type.RectangleRegion;
import in.twizmwaz.cardinal.module.region.type.SphereRegion;
import in.twizmwaz.cardinal.module.region.type.modifications.ComplementRegion;
import in.twizmwaz.cardinal.module.region.type.modifications.IntersectRegion;
import in.twizmwaz.cardinal.module.region.type.modifications.MirroredRegion;
import in.twizmwaz.cardinal.module.region.type.modifications.NegativeRegion;
import in.twizmwaz.cardinal.module.region.type.modifications.TranslatedRegion;
import in.twizmwaz.cardinal.module.region.type.modifications.UnionRegion;
import in.twizmwaz.cardinal.util.ParseUtil;
import lombok.NonNull;
import org.jdom2.Element;
import org.jdom2.located.Located;

import java.util.Map;
import java.util.Set;

@ModuleEntry
public class RegionModule extends AbstractModule {

  private Map<Match, Map<String, Region>> regions = Maps.newHashMap();

  @Override
  public boolean loadMatch(@NonNull Match match) {
    Map<String, Region> matchRegions = Maps.newHashMap();
    regions.put(match, matchRegions);
    for (Element regionsElement : match.getMap().getDocument().getRootElement().getChildren("regions")) {
      for (Element regionElement : regionsElement.getChildren()) {
        try {
          getRegion(match, regionElement);
        } catch (RegionException e) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{getRegionError(e, "region", null)}, false));
        }
      }
    }
    return true;
  }

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
    String id = ParseUtil.getFirstNonNullAttributeValue(element, "id", "name");
    switch (element.getName()) {
      case "cuboid":
        return checkRegion(match, id, new CuboidRegion(new CuboidRegionParser(element)));
      case "cylinder":
        return checkRegion(match, id, new CylinderRegion(new CylinderRegionParser(element)));
      case "block":
        return checkRegion(match, id, new BlockRegion(new BlockRegionParser(element)));
      case "sphere":
        return checkRegion(match, id, new SphereRegion(new SphereRegionParser(element)));
      case "rectangle":
        return checkRegion(match, id, new RectangleRegion(new RectangleRegionParser(element)));
      case "circle":
        return checkRegion(match, id, new CircleRegion(new CircleRegionParser(element)));
      case "half":
        return checkRegion(match, id, new HalfRegion(new HalfRegionParser(element)));
      case "below":
        return checkRegion(match, id, new BelowRegion(new BelowRegionParser(element)));
      case "above":
        return checkRegion(match, id, new AboveRegion(new AboveRegionParser(element)));
      case "empty":
        return checkRegion(match, id, new EmptyRegion());
      case "nowhere":
        return checkRegion(match, id, new NowhereRegion());
      case "everywhere":
        return checkRegion(match, id, new EverywhereRegion());
      case "region": {
        Set<String> attributes = Sets.newHashSet(alternateAttributes);
        attributes.add("id");
        Region region = fromAttributes(match, element, attributes);
        if (region != null) {
          return checkRegion(match, id, region);
        }
        return getRegion(match, element.getChildren().get(0));
      }
      case "negative":
        return checkRegion(match, id, new NegativeRegion(new NegativeRegionParser(element)));
      case "union":
        return checkRegion(match, id, new UnionRegion(new UnionRegionParser(element)));
      case "complement":
        return checkRegion(match, id, new ComplementRegion(new ComplementRegionParser(element)));
      case "intersect":
        return checkRegion(match, id, new IntersectRegion(new IntersectRegionParser(element)));
      case "translate":
        return checkRegion(match, id, new TranslatedRegion(new TranslatedRegionParser(element)));
      case "mirror":
        return checkRegion(match, id, new MirroredRegion(new MirroredRegionParser(element)));
      default: {
        Set<String> attributes = Sets.newHashSet(alternateAttributes);
        attributes.add("id");
        Region region = fromAttributes(match, element, attributes);
        if (region != null) {
          return checkRegion(match, id, region);
        }
      }
    }
    return null;
  }

  private Region fromAttributes(@NonNull Match match, @NonNull Element element, @NonNull Set<String> attributes) {
    for (String attribute : attributes) {
      String regionValue = element.getAttributeValue(attribute);
      if (regionValue != null) {
        Region region = getRegionById(match, regionValue);
        if (region != null) {
          return region;
        }
      }
    }
    return null;
  }

  private Region checkRegion(Match match, String id, Region region) {
    if (id != null) {
      regions.get(match).put(id, region);
    }
    return region;
  }

  /**
   * Gets an appropriate error message for a failed region parsing.
   *
   * @param e      The exception thrown when parsing.
   * @param name   The name of the expected region.
   * @param parent The module that attempted to retrieve the region.
   * @return The error message.
   */
  public static String getRegionError(RegionException e, String name, String parent) {
    if (e instanceof RegionAttributeException) {
      RegionAttributeException exception = (RegionAttributeException) e;
      Located located = (Located) exception.getElement();
      if (exception instanceof MissingRegionAttributeException) {
        return "Missing attribute \"" + exception.getAttribute() + "\"" + (name != null ? " for " + name : "")
            + (parent != null ? " for " + parent : "") + " at " + located.getLine() + ", " + located.getColumn();
      } else if (exception instanceof InvalidRegionAttributeException) {
        return "Invalid attribute \"" + exception.getAttribute() + "\"" + (name != null ? " for " + name : "")
            + (parent != null ? " for " + parent : "") + " at " + located.getLine() + ", " + located.getColumn();
      }
    } else if (e instanceof RegionPropertyException) {
      RegionPropertyException exception = (RegionPropertyException) e;
      Located located = (Located) exception.getElement();
      if (exception instanceof MissingRegionPropertyException) {
        return "Missing property \"" + exception.getProperty() + "\"" + (name != null ? " for " + name : "")
            + (parent != null ? " for " + parent : "") + " at " + located.getLine() + ", " + located.getColumn();
      } else if (exception instanceof InvalidRegionPropertyException) {
        return "Invalid property \"" + exception.getProperty() + "\"" + (name != null ? " for " + name : "")
            + (parent != null ? " for " + parent : "") + " at " + located.getLine() + ", " + located.getColumn();
      }
    }
    return "Could not parse " + (name != null ? name : "region") + (parent != null ? " for " + parent : "")
        + (e.getMessage() != null ? ": " + e.getMessage() : "");
  }

}
