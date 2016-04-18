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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import in.twizmwaz.cardinal.module.region.AbstractRegion;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionBounds;
import in.twizmwaz.cardinal.module.region.parser.modifications.UnionRegionParser;
import in.twizmwaz.cardinal.util.ListUtil;
import in.twizmwaz.cardinal.util.Vectors;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

public class UnionRegion extends AbstractRegion {

  private final List<Region> regions;

  public UnionRegion(List<Region> regions) {
    super(new RegionBounds(Vectors.getMinimumBound(regions), Vectors.getMaximumBound(regions)));
    this.regions = regions;
  }

  public UnionRegion(UnionRegionParser parser) {
    this(parser.getRegions());
  }

  @Override
  public boolean isRandomizable() {
    for (Region region : regions) {
      if (!region.isRandomizable()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isBounded() {
    for (Region region : regions) {
      if (!region.isBounded()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public List<Block> getBlocks() {
    if (!isBounded()) {
      throw new UnsupportedOperationException("Cannot get blocks in unbounded region");
    }
    Set<Block> blocks = Sets.newHashSet();
    regions.forEach(region -> blocks.addAll(region.getBlocks()));
    return Lists.newArrayList(blocks);
  }

  @Override
  public Vector getRandomPoint() {
    if (!isRandomizable()) {
      throw new UnsupportedOperationException("Cannot get random point in non-randomizable region");
    }
    return ListUtil.getRandom(regions).getRandomPoint();
  }

  @Override
  public boolean evaluate(Vector evaluating) {
    for (Region region : regions) {
      if (region.evaluate(evaluating)) {
        return true;
      }
    }
    return false;
  }

}
