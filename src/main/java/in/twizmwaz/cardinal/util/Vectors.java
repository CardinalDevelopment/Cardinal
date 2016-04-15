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
import org.bukkit.util.Vector;

import java.util.List;

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

    String[] rawCoords = str.split(",");
    for (String coordinate : rawCoords) {
      if (!Numbers.isDecimal(coordinate.trim())) {
        return null;
      }
      coordinates.add(Numbers.parseDouble(coordinate.trim()));
    }

    return coordinates;
  }

  public static Vector min() {
    return new Vector(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
  }

  public static Vector max() {
    return new Vector(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
  }

  public static Vector getMirroredVector(Vector vector, Vector origin, Vector normal) {
    vector = vector.minus(origin);
    vector = vector.minus(normal.times(vector.dot(normal)).times(2)).add(origin);
    vector = new Vector(round(vector.getX()), round(vector.getY()), round(vector.getZ()));
    return vector;
  }

  public static double round(double d) {
    return (double) Math.round(d * 10) / 10D;
  }

}
