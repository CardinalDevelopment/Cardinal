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

import com.google.common.collect.ImmutableSet;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.region.type.BlockRegion;
import in.twizmwaz.cardinal.util.Geometry;
import lombok.Data;
import org.bukkit.block.Block;
import org.bukkit.util.Cuboid;
import org.bukkit.util.Vector;

import java.util.Collection;

/**
 * RegionBounds is the smallest cuboid that contains a region,
 * it provides methods to check if the region is block bounded,
 * and if it is, you can get a list of it's blocks.
 */
@Data
public class RegionBounds {

  private final Match match;
  private final Cuboid cuboid;

  public static RegionBounds empty(Match match) {
    return new RegionBounds(match, Cuboid.empty());
  }

  public static RegionBounds unbounded(Match match) {
    return new RegionBounds(match, Cuboid.unbounded());
  }

  public RegionBounds translate(Vector offset) {
    return new RegionBounds(match, cuboid.translate(offset));
  }

  /**
   * Mirrors region bounds across a normal from an origin.
   *
   * @param origin The origin.
   * @param normal The normal.
   * @return The mirrored region bounds.
   */
  public RegionBounds mirror(Vector origin, Vector normal) {
    return new RegionBounds(match, Cuboid.between(
        Geometry.getMirrored(cuboid.minimum(), origin, normal),
        Geometry.getMirrored(cuboid.maximum(), origin, normal)));
  }

  /**
   * Substracts other regions.
   *
   * @param complement Regions to substract.
   * @return The complemented region bounds.
   */
  public RegionBounds complement(Collection<Region> complement) {
    Cuboid result = Cuboid.between(this.cuboid.minimum(), this.cuboid.maximum());
    for (Region substract : complement) {
      // Fixme: this will cause problems, because the substracted region doesn't have the same size as its bounds.
      result = Cuboid.complement(result, substract.getBounds().getCuboid());
    }
    return new RegionBounds(match, result);
  }

  /**
   * Checks if region bounds are bounded (non-infinite).
   *
   * @return If the bounds are bounded.
   */
  public boolean isBounded() {
    return cuboid.isFinite();
  }

  public Vector getCenter() {
    return cuboid.center();
  }

  public BlockRegion getCenterBlock() {
    return new BlockRegion(match, getCenter());
  }

  /**
   * Gets the blocks inside this region's bounds.
   *
   * @return The blocks.
   */
  public Collection<Block> getBlocks() {
    if (!cuboid.isBlockFinite()) {
      throw new UnsupportedOperationException("Cannot get blocks in unbounded region");
    }
    Vector min = Geometry.alignToBlock(this.cuboid.minimum());
    Vector max = Geometry.alignToBlock(this.cuboid.maximum());

    ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
    for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
      for (int y = min.getBlockY(); y < max.getBlockY(); y++) {
        for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
          builder.add(new Vector(x, y, z).toLocation(match.getWorld()).getBlock());
        }
      }
    }
    return builder.build();
  }

}
