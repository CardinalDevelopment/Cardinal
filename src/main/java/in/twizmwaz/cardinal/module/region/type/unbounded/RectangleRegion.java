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

package in.twizmwaz.cardinal.module.region.type.unbounded;

import in.twizmwaz.cardinal.module.region.type.UnboundedRegion;
import org.bukkit.util.Vector;

public class RectangleRegion extends UnboundedRegion {

  private Vector min;
  private Vector max;

  /**
   * @param id This region's ID.
   * @param xMin The minimum x position of this rectangle.
   * @param zMin The minimum z position of this rectangle.
   * @param xMax The maximum x position of this rectangle.
   * @param zMax The maximum z position of this rectangle.
   */
  public RectangleRegion(String id, double xMin, double zMin, double xMax, double zMax) {
    super(id);

    min = new Vector(xMin, Double.NEGATIVE_INFINITY, zMin);
    max = new Vector(xMax, Double.POSITIVE_INFINITY, zMax);
  }

  @Override
  public boolean contains(Vector vector) {
    return vector.isInAABB(min, max);
  }

}
