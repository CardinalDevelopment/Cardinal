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

import lombok.NonNull;

import java.text.DecimalFormat;

public class Strings {

  public static String getTechnicalName(String in) {
    return in.toUpperCase().replaceAll(" ", "_");
  }

  public static String getSimpleName(String in) {
    return in.toLowerCase().replaceAll("_", " ");
  }

  /**
   * Sets the string to lower case and removes spaces. Helpful when comparing user input strings.
   * @param in The string.
   * @return Lower case space-less string.
   */
  public static String getSimplifiedName(String in) {
    return in.toLowerCase().replaceAll("_", "").replaceAll(" ","");
  }

  /**
   * Gets the first word of a string.
   *
   * @param str The string.
   * @return The first word.
   */
  public static String getFirstWord(@NonNull String str) {
    if (!str.contains(" ")) {
      return str;
    }
    return str.split(" ")[0];
  }

  /**
   * Trims a string from a start position to an end position.
   *
   * @param str   The string.
   * @param start The start position.
   * @param end   The end position.
   * @return The trimmed string.
   */
  public static String trim(@NonNull String str, int start, int end) {
    return str.length() > start ? (str.length() > end ? str.substring(start, end) : str.substring(start)) : "";
  }

  public static int timeStringToSeconds(String input) {
    return (int) timeStringToExactSeconds(input);
  }

  /**
   * Converts a time-string, to a time, in seconds.
   *
   * @param input The string to parse.
   * @return The time, in seconds.
   */
  public static double timeStringToExactSeconds(String input) {
    if (input.equals("oo")) {
      return Double.POSITIVE_INFINITY;
    }
    if (input.equals("-oo")) {
      return Double.NEGATIVE_INFINITY;
    }
    double time = 0;
    String currentUnit = "";
    String current = "";
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (Character.isDigit(c) && !currentUnit.equals("")) {
        time += parseTime(Numbers.parseDouble(current), currentUnit);
        current = "";
        currentUnit = "";
      }
      if (Character.isDigit(c) || c == '.') {
        current += c + "";
      } else if (c != '-') {
        currentUnit += c + "";
      }
    }
    time += parseTime(Numbers.parseDouble(current), currentUnit);
    if (input.startsWith("-")) {
      time *= -1;
    }
    return time;
  }

  private static double parseTime(double value, String unit) {
    switch (unit) {
      case "y":
        return value * 365 * 60 * 60 * 24;
      case "mo":
        return value * 31 * 60 * 60 * 24;
      case "d":
        return value * 60 * 60 * 24;
      case "h":
        return value * 60 * 60;
      case "m":
        return value * 60;
      case "s":
      default:
        return value;
    }
  }

  /**
   * Formats a double, in seconds, to a time string.
   *
   * @param time The time, in seconds.
   * @return The formatted string.
   */
  public static String formatTime(double time) {
    boolean negative = false;
    if (time < 0) {
      negative = true;
      time *= -1;
    }
    int hours = (int) time / 3600;
    int minutes = (int) (time - (hours * 3600)) / 60;
    int seconds = (int) time - (hours * 3600) - (minutes * 60);
    String hoursString = hours + "";
    String minutesString = minutes + "";
    String secondsString = seconds + "";
    while (minutesString.length() < 2) {
      minutesString = "0" + minutesString;
    }
    while (secondsString.length() < 2) {
      secondsString = "0" + secondsString;
    }
    return (negative ? "-" : "") + (hours == 0 ? "" : hoursString + ":") + minutesString + ":" + secondsString;
  }

  /**
   * Formats a double, in seconds, to a time string. Includes milliseconds.
   *
   * @param time The double to format.
   * @return The formatted string.
   */
  public static String formatTimeWithMillis(double time) {
    boolean negative = false;
    if (time < 0) {
      negative = true;
      time *= -1;
    }
    int hours = (int) time / 3600;
    int minutes = (int) (time - (hours * 3600)) / 60;
    int seconds = (int) time - (hours * 3600) - (minutes * 60);
    double millis = time - (hours * 3600) - (minutes * 60) - seconds;
    String hoursString = hours + "";
    String minutesString = minutes + "";
    String secondsString = seconds + "";
    String millisString = new DecimalFormat(".000").format(millis);
    millisString = millisString.substring(1);
    while (minutesString.length() < 2) {
      minutesString = "0" + minutesString;
    }
    while (secondsString.length() < 2) {
      secondsString = "0" + secondsString;
    }
    return (negative ? "-" : "") + (hours == 0 ? "" : hoursString + ":") + minutesString + ":" + secondsString + "."
        + millisString;
  }

  /**
   * Removes last word from a string.
   *
   * @param string The string to remove last word.
   * @return The string without the last word.
   */
  public static String removeLastWord(String string) {
    String word = string;
    boolean reachedWord = false;
    for (int i = word.length() - 1; i >= 0; i--) {
      if (word.charAt(i) == ' ') {
        if (reachedWord) {
          break;
        } else {
          word = word.substring(0, i);
        }
      } else {
        if (!reachedWord) {
          reachedWord = true;
        }
        word = word.substring(0, i);
      }
    }
    return word;
  }

}
