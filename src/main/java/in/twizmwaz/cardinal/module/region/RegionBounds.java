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
import in.twizmwaz.cardinal.module.region.type.BlockRegion;
import in.twizmwaz.cardinal.util.Vectors;
import lombok.Data;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * RegionBounds is the smallest cuboid that contains a region,
 * it provides methods to check if the region is block bounded,
 * and if it is, you can get a list of it's blocks.
 */
@Data
public class RegionBounds {

  private final Vector min;
  private final Vector max;

  public static RegionBounds empty() {
    return new RegionBounds(Vectors.max(), Vectors.min());
  }

  public static RegionBounds unbounded() {
    return new RegionBounds(Vectors.min(), Vectors.max());
  }

  public RegionBounds translate(Vector offset) {
    return new RegionBounds(min.plus(offset), max.plus(offset));
  }

  /**
   * Mirrors region bounds across a normal from an origin.
   *
   * @param origin The origin.
   * @param normal The normal.
   * @return The mirrored region bounds.
   */
  public RegionBounds mirror(Vector origin, Vector normal) {
    return new RegionBounds(
        Vectors.getMirroredVector(min, origin, normal),
        Vectors.getMirroredVector(max, origin, normal));
  }

  /**
   * Checks if region bounds are bounded (non-infinite).
   *
   * @return If the bounds are bounded.
   */
  public boolean isBounded() {
    return !(Double.isInfinite(min.getX())
        || Double.isInfinite(max.getX())
        || Double.isInfinite(min.getY())
        || Double.isInfinite(max.getY())
        || Double.isInfinite(min.getZ())
        || Double.isInfinite(max.getZ()));
  }

  public Vector getCenter() {
    return min.midpoint(max);
  }

  public BlockRegion getCenterBlock() {
    return new BlockRegion(getCenter());
  }

  public Vector blockAlign(Vector vector) {
    return new Vector(vector.getBlockX() + 0.5d, vector.getBlockY() + 0.5d, vector.getBlockZ() + 0.5d);
  }

  /**
   * Gets the blocks inside this region's bounds.
   *
   * @return The blocks.
   */
  public List<Block> getBlocks() {
    if (!isBounded()) {
      throw new UnsupportedOperationException("Cannot get blocks in unbounded region");
    }
    Vector min = blockAlign(this.min);
    Vector max = blockAlign(this.max);

    List<Block> blocks = Lists.newArrayList();
    for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
      for (int y = min.getBlockY(); y < max.getBlockY(); y++) {
        for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
          blocks.add(new Vector(x, y, z).toLocation(null).getBlock()); //TODO: Get match world
        }
      }
    }
    return blocks;
  }

}
