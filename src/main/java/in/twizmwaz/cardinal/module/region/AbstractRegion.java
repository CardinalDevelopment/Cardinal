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

import in.twizmwaz.cardinal.module.filter.FilterState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Random;

@Getter
@Setter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class AbstractRegion implements Region {

  private final RegionBounds bounds;

  private Collection<Block> blocks;

  private Random random = new Random();

  @Override
  public FilterState evaluate(Object... objects) {
    for (Object obj : objects) {
      FilterState response = FilterState.fromBoolean(this.evaluate(obj));
      if (!response.equals(FilterState.ABSTAIN)) {
        return response;
      }
    }
    return FilterState.ABSTAIN;
  }

  private Boolean evaluate(Object obj) {
    if (obj instanceof Vector) {
      return this.contains((Vector) obj);
    } else if (obj instanceof Block) {
      return evaluate(((Block) obj).getLocation());
    } else if (obj instanceof Entity) {
      return evaluate(((Entity) obj).getLocation());
    }
    return null;
  }

}
