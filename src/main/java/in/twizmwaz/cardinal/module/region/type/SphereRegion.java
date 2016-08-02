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

import com.google.common.collect.ImmutableSet;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.region.AbstractRegion;
import in.twizmwaz.cardinal.module.region.RegionBounds;
import in.twizmwaz.cardinal.module.region.parser.SphereRegionParser;
import org.bukkit.block.Block;
import org.bukkit.util.Cuboid;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.stream.Collectors;

public class SphereRegion extends AbstractRegion {

  private final Vector origin;
  private final double radius;

  /**
   * Creates a sphere region with a given origin and radius.
   *
   * @param origin The origin.
   * @param radius The radius.
   */
  public SphereRegion(Match match, Vector origin, double radius) {
    super(new RegionBounds(match, Cuboid.between(
        new Vector(origin.getX() - radius, origin.getY() - radius, origin.getZ() - radius),
        new Vector(origin.getX() + radius, origin.getY() + radius, origin.getZ() + radius))));
    this.origin = origin;
    this.radius = radius;
  }

  public SphereRegion(Match match, SphereRegionParser parser) {
    this(match, parser.getOrigin(), parser.getRadius());
  }

  @Override
  public boolean isRandomizable() {
    return false;
  }

  @Override
  public boolean isBounded() {
    return getBounds().isBounded();
  }

  @Override
  public Collection<Block> getBlocks() {
    if (!isBounded()) {
      throw new UnsupportedOperationException("Cannot get blocks in unbounded region");
    }
    if (super.getBlocks() != null) {
      return super.getBlocks();
    }
    Collection<Block> blocks = getBounds().getBlocks().stream().filter(
        block -> contains(block.getLocation().toVector().plus(0.5, 0.5, 0.5))).collect(Collectors.toSet());
    setBlocks(ImmutableSet.copyOf(blocks));
    return super.getBlocks();
  }

  @Override
  public Vector getRandomPoint() {
    throw new UnsupportedOperationException("Cannot get random point in non-randomizable region");
  }

  @Override
  public boolean contains(Vector evaluating) {
    return evaluating.isInSphere(origin, radius);
  }

}
