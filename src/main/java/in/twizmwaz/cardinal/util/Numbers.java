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

import java.util.Random;

public class Numbers {

  /**
   * @param in The input string.
   * @return A parsed boolean based on the input string.
   */
  public static boolean parseBoolean(String in) {
    return in != null && (in.equalsIgnoreCase("on") || in.equalsIgnoreCase("true"));
  }

  /**
   * @param in       The input string.
   * @param fallback Fallback value if the string is null.
   * @return A parsed boolean based on the input string.
   */
  public static boolean parseBoolean(String in, boolean fallback) {
    return parseBoolean(in) || fallback;
  }

  /**
   * @param in       The input string.
   * @param fallback The double fallback if parsing fails.
   * @return The parsed double based on the input string.
   */
  public static double parseDouble(String in, double fallback) {
    if (in == null) {
      return fallback;
    } else if (in.equalsIgnoreCase("oo")) {
      return Double.POSITIVE_INFINITY;
    } else if (in.equalsIgnoreCase("-oo")) {
      return Double.NEGATIVE_INFINITY;
    } else {
      return Double.parseDouble(in);
    }
  }

  /**
   * @param in The input string.
   * @return The parsed double based on the input string.
   */
  public static double parseDouble(String in) {
    return parseDouble(in, 0);
  }

  /**
   * @param in       The input string.
   * @param fallback Fallback value if the input is null.
   * @return The parsed integer based on the input string.
   */
  public static int parseInteger(String in, int fallback) {
    if (in == null) {
      return fallback;
    } else if (in.equalsIgnoreCase("oo")) {
      return Integer.MAX_VALUE;
    } else if (in.equalsIgnoreCase("-oo")) {
      return Integer.MIN_VALUE;
    } else {
      return Integer.parseInt(in);
    }
  }

  /**
   * @param in The input string.
   * @return The parsed integer based on the input string.
   */
  public static int parseInteger(String in) {
    return parseInteger(in, 0);
  }

  /**
   * @param in       The input string.
   * @param fallback Fallback value if the input is null.
   * @return The parsed integer based on the input string.
   */
  public static short parseShort(String in, short fallback) {
    if (in == null) {
      return fallback;
    } else if (in.equalsIgnoreCase("oo")) {
      return Short.MAX_VALUE;
    } else if (in.equalsIgnoreCase("-oo")) {
      return Short.MIN_VALUE;
    } else {
      return Short.parseShort(in);
    }
  }

  /**
   * @param in The input string.
   * @return The parsed integer based on the input string.
   */
  public static short parseShort(String in) {
    return parseShort(in, (short) 0);
  }

  public static double getRandom(double min, double max) {
    return new Random().nextInt((int) (max - min) + 1) + min;
  }

  public static boolean isDecimal(String str) {
    return str != null && str.matches("-?\\d+(\\.\\d+)?");
  }

  public static boolean isInteger(String str) {
    return str != null && str.matches("\\d+");
  }

  public static boolean isInfinity(String str) {
    return str != null && str.matches("[\\-\\+]?[oO][oO]");
  }

  public static boolean isNumber(String str) {
    return isDecimal(str) || isInfinity(str);
  }

}
