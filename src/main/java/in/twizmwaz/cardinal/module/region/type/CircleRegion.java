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

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.region.AbstractRegion;
import in.twizmwaz.cardinal.module.region.RegionBounds;
import in.twizmwaz.cardinal.module.region.parser.CircleRegionParser;
import org.bukkit.block.Block;
import org.bukkit.util.Cuboid;
import org.bukkit.util.Vector;

import java.util.Collection;

public class CircleRegion extends AbstractRegion {

  private final Vector center;
  private final double radius;

  /**
   * Creates a circle region with a given center and radius.
   *
   * @param center The center.
   * @param radius The radius.
   */
  public CircleRegion(Match match, Vector center, double radius) {
    super(new RegionBounds(match, Cuboid.between(
        new Vector(center.getX() - radius, Double.NEGATIVE_INFINITY, center.getZ() - radius),
        new Vector(center.getX() + radius, Double.POSITIVE_INFINITY, center.getZ() + radius))));
    this.center = center;
    this.radius = radius;
  }

  public CircleRegion(Match match, CircleRegionParser parser) {
    this(match, parser.getCenter(), parser.getRadius());
  }

  @Override
  public boolean contains(Vector vector) {
    return Math.hypot(Math.abs(vector.getX() - center.getX()), Math.abs(vector.getZ() - center.getZ())) <= radius;
  }

  @Override
  public boolean isRandomizable() {
    return false;
  }

  @Override
  public boolean isBounded() {
    return false;
  }

  @Override
  public Collection<Block> getBlocks() {
    throw new UnsupportedOperationException("Cannot get blocks in unbounded region");
  }

  @Override
  public Vector getRandomPoint() {
    throw new UnsupportedOperationException("Cannot get random point in non-randomizable region");
  }

}
