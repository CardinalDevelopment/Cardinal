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

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.id.IdModule;
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
import in.twizmwaz.cardinal.module.region.parser.PointRegionParser;
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
import in.twizmwaz.cardinal.util.Proto;
import lombok.NonNull;
import org.jdom2.Element;
import org.jdom2.located.Located;

import java.util.List;
import java.util.Map;

@ModuleEntry(depends = {IdModule.class})
public class RegionModule extends AbstractModule {

  //Static regions. Can be shared across matches
  public static final Region EVERYWHERE = new EverywhereRegion(null);
  public static final Region NOWHERE = new NowhereRegion(null);

  @Override
  public boolean loadMatch(@NonNull Match match) {
    IdModule.get().add(match, "everywhere", new EverywhereRegion(match));
    IdModule.get().add(match, "nowhere", new NowhereRegion(match));

    for (Element regionsElement : match.getMap().getDocument().getRootElement().getChildren("regions")) {
      for (Element regionElement : regionsElement.getChildren()) {
        if (regionElement.getName().equals("apply")) {
          continue;
        }
        try {
          Map.Entry<String, String> id =
              ParseUtil.getFirstNonNullAttributeValue(regionElement, Lists.newArrayList("id", "name"));
          IdModule.get().add(match, id != null ? id.getValue() : null, getRegion(match, regionElement));
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
    return IdModule.get().get(match, id, Region.class);
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
    List<String> attributes = Lists.newArrayList(alternateAttributes);
    attributes.add("id");
    attributes.add("name");
    Map.Entry<String, String> value = ParseUtil.getFirstNonNullAttributeValue(element, attributes);
    String id = null;
    if (value != null) {
      String attr = value.getKey();
      Proto proto = match.getMap().getProto();
      if (attr.equals("id") && proto.isBefore("1.4.0")) {
        errors.add(new ModuleError(this, match.getMap(),
            new String[]{"Attribute \"id\" should be \"name\" prior to proto 1.4.0"}, false));
      } else if (attr.equals("name") && proto.isAfterOrAt("1.4.0")) {
        errors.add(new ModuleError(this, match.getMap(),
            new String[]{"Attribute \"name\" should be \"id\" in proto 1.4.0 or later"}, false));
      }
      id = value.getValue();
    }
    switch (element.getName()) {
      case "cuboid":
        return new CuboidRegion(match, new CuboidRegionParser(element));
      case "cylinder":
        return new CylinderRegion(match, new CylinderRegionParser(element));
      case "block":
        return new BlockRegion(match, new BlockRegionParser(element));
      case "sphere":
        return new SphereRegion(match, new SphereRegionParser(element));
      case "rectangle":
        return new RectangleRegion(match, new RectangleRegionParser(element));
      case "circle":
        return new CircleRegion(match, new CircleRegionParser(element));
      case "half":
        return new HalfRegion(match, new HalfRegionParser(element));
      case "below":
        return new BelowRegion(match, new BelowRegionParser(element));
      case "above":
        return new AboveRegion(match, new AboveRegionParser(element));
      case "empty":
        return new EmptyRegion(match);
      case "nowhere":
        return NOWHERE;
      case "everywhere":
        return EVERYWHERE;
      case "negative":
        return new NegativeRegion(match, new NegativeRegionParser(match, element));
      case "union":
        return new UnionRegion(match, new UnionRegionParser(match, element));
      case "complement":
        return new ComplementRegion(new ComplementRegionParser(match, element));
      case "intersect":
        return new IntersectRegion(match, new IntersectRegionParser(match, element));
      case "translate":
        return new TranslatedRegion(new TranslatedRegionParser(match, element));
      case "mirror":
        return new MirroredRegion(new MirroredRegionParser(match, element));
      case "point":
        return PointRegionParser.generateRegion(match, element);
      default:
        if (id != null) {
          Region region = getRegionById(match, id);
          if (region != null) {
            return region;
          }
        }
        List<Region> regions = Lists.newArrayList();
        for (Element child : element.getChildren()) {
          Region childRegion = getRegion(match, child);
          if (childRegion != null) {
            regions.add(childRegion);
          }
        }
        if (regions.isEmpty()) {
          return null;
        }
        return new UnionRegion(match, regions);
    }
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
