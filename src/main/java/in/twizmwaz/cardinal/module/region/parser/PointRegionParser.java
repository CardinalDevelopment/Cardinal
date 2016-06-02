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

import com.google.common.base.Strings;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionException;
import in.twizmwaz.cardinal.module.region.RegionModule;
import in.twizmwaz.cardinal.module.region.RegionParser;
import in.twizmwaz.cardinal.module.region.exception.property.MissingRegionPropertyException;
import in.twizmwaz.cardinal.module.region.type.PointRegion;
import in.twizmwaz.cardinal.module.region.type.modifications.PointProviderRegion;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.ParseUtil;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jdom2.Element;

@Getter
public class PointRegionParser implements RegionParser {

  private final float yaw;
  private final float pitch;
  private Vector position = null;
  private Vector angle = null;
  private Region region = null;

  private PointRegionParser(@NonNull Match match, @NonNull Element element) throws RegionException {
    yaw = Numbers.parseFloat(ParseUtil.getFirstAttribute("yaw"));
    pitch = Numbers.parseFloat(element.getAttributeValue("pitch"));

    if (!Strings.isNullOrEmpty(element.getText().trim())) {
      position = Numbers.getVector(element.getText());
    }

    if (!Strings.isNullOrEmpty(element.getAttributeValue("angle").trim())) {
      angle = Numbers.getVector(element.getAttributeValue("angle"));
    }

    RegionModule module = Cardinal.getModule(RegionModule.class);

    if (!Strings.isNullOrEmpty(element.getAttributeValue("region"))) {

      Region region = module.getRegionById(match, element.getAttributeValue("region"));
      this.region = region;

    } else if (element.getChildren("region").size() > 0) {

      region = module.getRegion(match, element.getChild("region"));

    }
  }

  /**
   * Parses a region from the Element.
   *
   * <p>This method is different from other parsers intentionally. Point regions are different in that for some reason,
   * we have two different types with similar yet different purposes. This method is in place to differentiate between
   * the region types and to return the correct one without something ugly in the module class.</p>
   *
   * @param match   The match.
   * @param element The element to parse.
   * @return The region, if applicable.
   * @throws RegionException A {@link MissingRegionPropertyException} when there is no valid location.
   */
  public static Region generateRegion(@NonNull Match match, @NonNull Element element) throws RegionException {
    PointRegionParser parser = new PointRegionParser(match, element);
    if (parser.getPosition() != null) {
      Location location = parser.getPosition().toLocation(match.getWorld(), parser.getYaw(), parser.getPitch());
      if (parser.getAngle() != null) {
        location.setDirection(parser.getAngle());
      }
      return new PointRegion(match, location);
    } else if (parser.getRegion() != null) {
      return new PointProviderRegion(parser.getRegion(), parser.getAngle(), parser.getYaw(), parser.getPitch());
    } else {
      throw new MissingRegionPropertyException("position", element);
    }

  }

}
