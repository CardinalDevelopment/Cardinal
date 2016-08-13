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
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionBounds;
import in.twizmwaz.cardinal.util.Geometry;
import in.twizmwaz.cardinal.util.ListUtil;
import in.twizmwaz.cardinal.util.MaterialPattern;
import org.bukkit.block.Block;
import org.bukkit.util.Cuboid;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FiniteBlockRegion extends AbstractRegion {

  private FiniteBlockRegion(RegionBounds bounds, List<Block> blocks) {
    super(bounds);
    super.setBlocks(blocks);
  }

  @Override
  public Collection<Block> getBlocks() {
    return super.getBlocks();
  }

  @Override
  public Vector getRandomPoint() {
    return ListUtil.getRandom((List<Block>) super.getBlocks()).getLocation();
  }

  @Override
  public boolean isBounded() {
    return true;
  }

  public boolean contains(Vector evaluating) {
    evaluating = Geometry.floor(evaluating);
    for (Block block : super.getBlocks()) {
      if (block.getLocation().equals(evaluating)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isRandomizable() {
    return true;
  }

  /**
   * Creates a block finite region that matches a material.
   * @param match The match this region belongs to.
   * @param region The region to build the finite block region on. MUST be bounded.
   * @param pattern The material pattern to filter blocks.
   * @return A FiniteBlockRegion containing all regions that matched the pattern.
   */
  public FiniteBlockRegion getFromMaterialPattern(Match match, Region region, MaterialPattern pattern) {
    List<Block> blocks = region.getBlocks().stream().filter(block -> pattern.contains(block.getType(), block.getData()))
        .collect(Collectors.toList());
    return new FiniteBlockRegion(new RegionBounds(match, getBounds(blocks)), blocks);
  }


  private Cuboid getBounds(List<Block> blocks) {
    List<Vector> vectors = new ArrayList<>();
    vectors.addAll(blocks.stream().map(Block::getLocation).collect(Collectors.toList()));
    vectors.addAll(blocks.stream().map(block -> block.getLocation().plus(1, 1, 1)).collect(Collectors.toList()));
    return Cuboid.enclosing(vectors.toArray(new Vector[vectors.size()]));
  }

}
