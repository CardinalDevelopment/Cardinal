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

package in.twizmwaz.cardinal.module.region.type.modifications;

import in.twizmwaz.cardinal.module.region.AbstractRegion;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.parser.modifications.MirroredRegionParser;
import in.twizmwaz.cardinal.util.Geometry;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.stream.Collectors;

public class MirroredRegion extends AbstractRegion {

  private final Region region;
  private final Vector origin;
  private final Vector normal;

  /**
   * Creates a region, mirrored over a normal from an origin.
   *
   * @param region The original region.
   * @param origin The origin.
   * @param normal The normal.
   */
  public MirroredRegion(Region region, Vector origin, Vector normal) {
    super(region.getBounds().mirror(origin, normal.normalize()));
    this.region = region;
    this.origin = origin;
    this.normal = normal.normalize();
  }

  public MirroredRegion(MirroredRegionParser parser) {
    this(parser.getRegion(), parser.getOrigin(), parser.getNormal());
  }

  @Override
  public boolean contains(Vector vector) {
    return region.contains(Geometry.getMirrored(vector, origin, normal));
  }

  @Override
  public boolean isRandomizable() {
    return region.isRandomizable();
  }

  @Override
  public boolean isBounded() {
    return region.isBounded();
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
        block -> contains(block.getLocation().toVector().add(0.5, 0.5, 0.5))).collect(Collectors.toSet());
    setBlocks(blocks);
    return super.getBlocks();
  }

  @Override
  public Vector getRandomPoint() {
    if (!isRandomizable()) {
      throw new UnsupportedOperationException("Cannot get random point in non-randomizable region");
    }
    return Geometry.getMirrored(region.getRandomPoint(), origin, normal);
  }

}
