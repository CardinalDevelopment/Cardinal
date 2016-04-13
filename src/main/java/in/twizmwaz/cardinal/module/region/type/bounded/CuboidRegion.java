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

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.module.region.parser.bounded.CuboidParser;
import in.twizmwaz.cardinal.util.Numbers;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.List;

@AllArgsConstructor
public class CuboidRegion implements RandomizableRegion {

  private final Vector min;
  private final Vector max;

  public CuboidRegion(CuboidParser parser) {
    this(parser.getMin(), parser.getMax());
  }

  @Override
  public List<Block> getBlocks() {
    List<Block> blocks = Lists.newArrayList();
    for (int x = (int) min.getX(); x < max.getX(); x++) {
      for (int y = (int) min.getY(); y < max.getY(); y++) {
        for (int z = (int) min.getZ(); z < max.getZ(); z++) {
          blocks.add(new Location(null, x, y, z).getBlock()); //TODO: Get match world
        }
      }
    }
    return blocks;
  }

  @Override
  public BlockRegion getCenterBlock() {
    return new BlockRegion(min.getMidpoint(max));
  }

  @Override
  public boolean evaluate(Vector vector) {
    return vector.isInAABB(min, max);
  }

  @Override
  public Vector getRandomPoint() {
    return new Vector(Numbers.getRandom(min.getX(), max.getX()), Numbers.getRandom(min.getY(), max.getY()),
        Numbers.getRandom(min.getZ(), max.getZ()));
  }
}
