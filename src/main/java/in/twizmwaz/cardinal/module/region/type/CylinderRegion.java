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

package in.twizmwaz.cardinal.module.region.type;

import in.twizmwaz.cardinal.module.region.RegionBounds;
import in.twizmwaz.cardinal.module.region.parser.CylinderRegionParser;
import in.twizmwaz.cardinal.util.Numbers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.util.Vector;

@AllArgsConstructor
public class CylinderRegion implements RandomizableRegion {

  private final Vector base;
  private final double radius;
  private final double height;
  @Getter
  private final RegionBounds bounds;

  public CylinderRegion(Vector base, double radius, double height) {
    this(base, radius, height, new RegionBounds(
        new Vector(base.getX() - radius, base.getY(), base.getZ() - radius),
        new Vector(base.getX() + radius, base.getY() + height, base.getZ() + radius)));
  }

  public CylinderRegion(CylinderRegionParser parser) {
    this(parser.getBase(), parser.getRadius(), parser.getHeight());
  }

  @Override
  public Vector getRandomPoint() {
    double a = Numbers.getRandom(0, radius);
    double b = Numbers.getRandom(0, height);
    double c = Numbers.getRandom(0, 2 * Math.PI);

    return new Vector(base.getX() + a * Math.cos(c), base.getY() + b, base.getZ() + a * Math.sin(c));
  }

  @Override
  public boolean evaluate(Vector evaluating) {
    return Math.hypot(Math.abs(evaluating.getX() - base.getX()), Math.abs(evaluating.getZ() - base.getZ())) <= radius
        && base.getY() <= evaluating.getY() && evaluating.getY() <= base.getY() + height;
  }

}
