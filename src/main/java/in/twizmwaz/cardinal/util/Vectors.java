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

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.module.region.Region;
import lombok.NonNull;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class Vectors {

  /**
   * Gets a vector based on coordinates from a given string.
   *
   * @param str The string.
   * @return The vector.
   */
  public static Vector getVector(String str) {
    List<Double> coordinates = getCoordinates(str);
    if (coordinates == null || coordinates.size() != 3) {
      return null;
    }
    return new Vector(coordinates.get(0), coordinates.get(1), coordinates.get(2));
  }

  /**
   * Gets a list of coordinates based on a string.
   *
   * @param str The string.
   * @return The list of coordinates.
   */
  public static List<Double> getCoordinates(String str) {
    List<Double> coordinates = Lists.newArrayList();

    if (str.contains(",")) {
      String[] rawCoords = str.split(",");
      for (String coordinate : rawCoords) {
        if (!Numbers.isDecimal(coordinate.trim())) {
          return null;
        }
        coordinates.add(Numbers.parseDouble(coordinate.trim()));
      }
    } else {
      if (!Numbers.isDecimal(str.trim())) {
        return null;
      }
      coordinates.add(Numbers.parseDouble(str.trim()));
    }

    return coordinates;
  }

  public static Vector min() {
    return new Vector(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
  }

  public static Vector max() {
    return new Vector(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
  }

  /**
   * Mirrors a vector across a normal from an origin.
   *
   * @param vector The original vector.
   * @param origin The origin.
   * @param normal The normal.
   * @return The mirrored vector.
   */
  public static Vector getMirroredVector(Vector vector, Vector origin, Vector normal) {
    vector = vector.minus(origin);
    vector = vector.minus(normal.times(vector.dot(normal)).times(2)).add(origin);
    vector = new Vector(round(vector.getX()), round(vector.getY()), round(vector.getZ()));
    return vector;
  }

  public static double round(double d) {
    return (double) Math.round(d * 10) / 10D;
  }

  public static Vector getMinimumBound(@NonNull List<Region> regions) {
    return getMinimum(regions.stream().map(region -> region.getBounds().getMin()).collect(Collectors.toList()));
  }

  /**
   * Gets the minimum vector from a list of vectors.
   *
   * @param vectors The list of vectors.
   * @return The minimum vector.
   */
  public static Vector getMinimum(@NonNull List<Vector> vectors) {
    if (vectors.size() == 0) {
      throw new IllegalArgumentException("Cannot get maximum vector of no vectors");
    }
    double x = vectors.get(0).getX();
    double y = vectors.get(0).getY();
    double z = vectors.get(0).getZ();
    for (int i = 1; i < vectors.size(); i++) {
      if (vectors.get(i).getX() < x) {
        x = vectors.get(i).getX();
      }
      if (vectors.get(i).getY() < y) {
        y = vectors.get(i).getY();
      }
      if (vectors.get(i).getZ() < z) {
        z = vectors.get(i).getZ();
      }
    }
    return new Vector(x, y, z);
  }

  public static Vector getMaximumBound(@NonNull List<Region> regions) {
    return getMaximum(regions.stream().map(region -> region.getBounds().getMax()).collect(Collectors.toList()));
  }

  /**
   * Gets the maximum vector from a list of vectors.
   *
   * @param vectors The list of vectors.
   * @return The maximum vector.
   */
  public static Vector getMaximum(@NonNull List<Vector> vectors) {
    if (vectors.size() == 0) {
      throw new IllegalArgumentException("Cannot get maximum vector of no vectors");
    }
    double x = vectors.get(0).getX();
    double y = vectors.get(0).getY();
    double z = vectors.get(0).getZ();
    for (int i = 1; i < vectors.size(); i++) {
      if (vectors.get(i).getX() > x) {
        x = vectors.get(i).getX();
      }
      if (vectors.get(i).getY() > y) {
        y = vectors.get(i).getY();
      }
      if (vectors.get(i).getZ() > z) {
        z = vectors.get(i).getZ();
      }
    }
    return new Vector(x, y, z);
  }

  public static Vector alignToBlock(Vector vector) {
    return new Vector(vector.getBlockX() + 0.5d, vector.getBlockY() + 0.5d, vector.getBlockZ() + 0.5d);
  }

}
