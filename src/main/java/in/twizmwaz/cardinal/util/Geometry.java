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

package in.twizmwaz.cardinal.util;

import in.twizmwaz.cardinal.module.region.Region;
import lombok.NonNull;
import org.bukkit.util.Cuboid;
import org.bukkit.util.ImmutableVector;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.stream.Collectors;

public class Geometry {

  public static final Vector MINIMUM_VECTOR = ImmutableVector.of(Double.NEGATIVE_INFINITY);
  public static final Vector MAXIMUM_VECTOR = ImmutableVector.of(Double.POSITIVE_INFINITY);

  /**
   * Mirrors a vector across a normal from an origin.
   *
   * @param vector The original vector.
   * @param origin The origin.
   * @param normal The normal.
   * @return The mirrored vector.
   */
  public static Vector getMirrored(Vector vector, Vector origin, Vector normal) {
    vector = vector.minus(origin);
    vector = vector.minus(normal.times(vector.dot(normal)).times(2)).add(origin);
    vector = new Vector(round(vector.getX()), round(vector.getY()), round(vector.getZ()));
    return vector;
  }

  /**
   * Mirrors a cuboid across a normal from an origin.
   *
   * @param cuboid The original cuboid.
   * @param origin The origin.
   * @param normal The normal.
   * @return The mirrored cuboid.
   */
  public static Cuboid getMirrored(Cuboid cuboid, Vector origin, Vector normal) {
    return Cuboid.between(
        getMirrored(cuboid.minimum(), origin, normal),
        getMirrored(cuboid.maximum(), origin, normal));
  }

  public static double round(double d) {
    return (double) Math.round(d * 10) / 10d;
  }

  /**
   * Gets the minimum vector from a list of regions.
   *
   * @param regions The list of regions.
   * @return The minimum vector.
   */
  public static Vector getMinimumBound(@NonNull Collection<Region> regions) {
    Collection<Vector> minimums = regions.stream().map(region ->
        region.getBounds().getCuboid().minimum()).collect(Collectors.toList());
    return getMinimum((Vector[]) minimums.toArray());
  }

  /**
   * Gets the minimum vector from a list of vectors.
   *
   * @param vectors The list of vectors.
   * @return The minimum vector.
   */
  public static Vector getMinimum(@NonNull Vector... vectors) {
    return Cuboid.enclosing(vectors).minimum();
  }

  /**
   * Gets the maximum vector from a list of regions.
   *
   * @param regions The list of regions.
   * @return The maximum vector.
   */
  public static Vector getMaximumBound(@NonNull Collection<Region> regions) {
    Collection<Vector> maximums = regions.stream().map(region ->
        region.getBounds().getCuboid().maximum()).collect(Collectors.toList());
    return getMaximum((Vector[]) maximums.toArray());
  }

  /**
   * Gets the maximum vector from a list of vectors.
   *
   * @param vectors The list of vectors.
   * @return The maximum vector.
   */
  public static Vector getMaximum(@NonNull Vector... vectors) {
    return Cuboid.enclosing(vectors).maximum();
  }

  public static Vector floor(Vector vector) {
    return ImmutableVector.of(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
  }

  /**
   * Rounds all values of a cuboid down.
   *
   * @param cuboid The original cuboid.
   * @return The floored cuboid.
   */
  public static Cuboid floor(Cuboid cuboid) {
    Vector min = floor(cuboid.minimum());
    Vector max = floor(cuboid.maximum());
    return Cuboid.between(min, max);
  }

  public static Vector alignToBlock(Vector vector) {
    return ImmutableVector.copyOf(floor(vector)).add(0.5d, 0.5d, 0.5d);
  }

  /**
   * Aligns all values of a cuboid to blocks.
   *
   * @param cuboid The original cuboid.
   * @return The aligned cuboid.
   */
  public static Cuboid alignToBlock(Cuboid cuboid) {
    Vector min = alignToBlock(cuboid.minimum());
    Vector max = alignToBlock(cuboid.maximum());
    return Cuboid.between(min, max);
  }

  public static Cuboid getCuboidEnclosing(Collection<Region> regions) {
    return Cuboid.between(getMinimumBound(regions), getMaximumBound(regions));
  }

}
