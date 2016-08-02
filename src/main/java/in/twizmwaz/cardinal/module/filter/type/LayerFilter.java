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

package in.twizmwaz.cardinal.module.filter.type;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;

@AllArgsConstructor
public class LayerFilter extends ObjectTypeFilter<Block> {

  private final int layer;
  private final Coordinate coordinate;

  @Override
  public Class<Block> getType() {
    return Block.class;
  }

  @Override
  public Boolean evaluate(Block block) {
    return getCoord(block) == layer
        || block.getWorld().getBlockAt(
        layerOrBlock(Coordinate.X, block.getX()),
        layerOrBlock(Coordinate.Y, block.getY()),
        layerOrBlock(Coordinate.Z, block.getZ())).getType().equals(Material.AIR);
  }

  private int layerOrBlock(Coordinate coord, int block) {
    return coordinate.equals(coord) ? layer : block;
  }

  private int getCoord(Block block) {
    switch (coordinate) {
      case X:
        return block.getX();
      case Y:
        return block.getY();
      case Z:
        return block.getZ();
      default:
        return 0;
    }
  }

  public enum Coordinate {
    X,
    Y,
    Z;
  }

}
