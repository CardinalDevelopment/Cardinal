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
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class PointProviderRegion extends AbstractRegion {

  private final Region region;
  private Vector direction;
  private final float yaw;
  private final float pitch;

  /**
   * @param region    The region to get points from.
   * @param direction A vector to set direction.
   * @param yaw       Yaw for direction.
   * @param pitch     Pitch for direction.
   */
  public PointProviderRegion(@NonNull Region region, Vector direction, float yaw, float pitch) {
    super(region.getBounds());
    this.region = region;
    this.yaw = yaw;
    this.pitch = pitch;
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
  public Vector getRandomPoint() {
    Location location = region.getRandomPoint().toLocation(getBounds().getMatch().getWorld(), yaw, pitch);
    if (direction != null) {
      location.setDirection(direction);
    }
    return location;
  }

  @Override
  public boolean contains(Vector evaluating) {
    throw new UnsupportedOperationException("Cannot determine absolute location of PointProvider");
  }
}
