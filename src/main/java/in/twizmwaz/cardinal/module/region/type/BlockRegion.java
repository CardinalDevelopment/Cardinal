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

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.module.region.AbstractRegion;
import in.twizmwaz.cardinal.module.region.RegionBounds;
import in.twizmwaz.cardinal.module.region.parser.BlockRegionParser;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.List;

public class BlockRegion extends AbstractRegion {

  private final Vector vector;

  public BlockRegion(Vector vector) {
    super(new RegionBounds(vector, vector.plus(1, 1, 1)));
    this.vector = vector;
  }

  public BlockRegion(BlockRegionParser parser) {
    this(parser.getVector());
  }

  @Override
  public boolean evaluate(Vector vector) {
    return vector.getBlockX() == getVector().getBlockX()
        && vector.getBlockY() == getVector().getBlockY()
        && vector.getBlockZ() == getVector().getBlockZ();
  }

  public Vector getVector() {
    return vector.clone().add(0.5, 0.5, 0.5);
  }

  public Location getLocation() {
    return getVector().toLocation(null); //TODO: Get match world
  }

  public Block getBlock() {
    return getLocation().getBlock();
  }

  @Override
  public boolean isRandomizable() {
    return true;
  }

  @Override
  public boolean isBounded() {
    return true;
  }

  @Override
  public List<Block> getBlocks() {
    return Lists.newArrayList(getBlock());
  }

  @Override
  public Vector getRandomPoint() {
    return new Vector(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
  }

}
