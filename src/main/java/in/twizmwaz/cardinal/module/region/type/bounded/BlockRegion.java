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

package in.twizmwaz.cardinal.module.region.type.bounded;

import in.twizmwaz.cardinal.module.region.parser.bounded.BlockParser;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BlockRegion implements RandomizableRegion {

  private final Vector vector;

  public BlockRegion(BlockParser parser) {
    this(parser.getVector());
  }

  @Override
  public List<Block> getBlocks() {
    List<Block> blocks = new ArrayList<>();
    blocks.add(getBlock());
    return blocks;
  }

  @Override
  public boolean evaluate(Vector vector) {
    return vector.getBlockX() == getVector().getBlockX()
        && vector.getBlockY() == getVector().getBlockY()
        && vector.getBlockZ() == getVector().getBlockZ();
  }

  @Override
  public BlockRegion getCenterBlock() {
    return this;
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
  public Vector getRandomPoint() {
    return new Vector(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
  }
}
