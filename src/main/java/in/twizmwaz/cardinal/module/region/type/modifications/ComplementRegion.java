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

import com.google.common.collect.ImmutableSet;
import in.twizmwaz.cardinal.module.region.AbstractRegion;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.parser.modifications.ComplementRegionParser;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ComplementRegion extends AbstractRegion {

  private final Region region;
  private final List<Region> complements;

  /**
   * Creates a region from a sub-region and its complements.
   *
   * @param region      The sub-region.
   * @param complements The complements.
   */
  public ComplementRegion(Region region, List<Region> complements) {
    super(region.getBounds());
    this.region = region;
    this.complements = complements;
  }

  public ComplementRegion(ComplementRegionParser parser) {
    this(parser.getRegion(), parser.getComplements());
  }

  @Override
  public boolean isRandomizable() {
    return false;
  }

  @Override
  public boolean isBounded() {
    return region.isBounded();
  }

  @Override
  public Collection<Block> getBlocks() {
    if (super.getBlocks() != null) {
      return super.getBlocks();
    }
    if (!isBounded()) {
      throw new UnsupportedOperationException("Cannot get blocks in unbounded region");
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
    if (!region.contains(evaluating)) {
      return false;
    }
    for (Region complement : complements) {
      if (complement.contains(evaluating)) {
        return false;
      }
    }
    return true;
  }

}
