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

import lombok.Data;
import lombok.NonNull;

/**
 * Class to represent XML protocols, based on semantic versioning.
 */
@Data
public class Proto {

  private final int major;
  private final int minor;
  private final int patch;

  /**
   * @param in The semantic version string to be parsed.
   * @return The parsed Proto.
   */
  public static Proto parseProto(@NonNull String in) {
    String[] components = in.split("\\.");
    if (components.length != 3) {
      throw new NumberFormatException("A proto must be a semantic version.");
    }
    int major = Integer.parseInt(components[0]);
    int minor = Integer.parseInt(components[1]);
    int patch = Integer.parseInt(components[2]);
    return new Proto(major, minor, patch);
  }

  /**
   * @param proto The given proto.
   * @return If this proto is greater than the given proto.
   */
  public boolean isAfter(@NonNull Proto proto) {
    return this.major > proto.getMajor()
        || (this.major == proto.getMajor() && (this.minor > proto.getMinor()
        || (this.minor == proto.getMinor() && this.patch > proto.getPatch())));
  }

  public boolean isAfter(String proto) {
    return isAfter(parseProto(proto));
  }

  public boolean isAfterOrAt(@NonNull Proto proto) {
    return this.equals(proto) || this.isAfter(proto);
  }

  public boolean isAfterOrAt(String proto) {
    return isAfterOrAt(parseProto(proto));
  }

  /**
   * @param proto The given proto.
   * @return If this proto is less than the given proto.
   */
  public boolean isBefore(@NonNull Proto proto) {
    return this.major < proto.getMajor()
        || (this.major == proto.getMajor() && (this.minor < proto.getMinor()
        || (this.minor == proto.getMinor() && this.patch < proto.getPatch())));
  }

  public boolean isBefore(String proto) {
    return isBefore(parseProto(proto));
  }

  public boolean isBeforeOrAt(@NonNull Proto proto) {
    return this.equals(proto) || this.isBefore(proto);
  }

  public boolean isBeforeOrAt(String proto) {
    return isBeforeOrAt(parseProto(proto));
  }

  /**
   * Returns if a Proto is within a given range.
   * Note: The range is [min, max), a proto that equals the max will return false.
   *
   * @param min The minimum bound, inclusive.
   * @param max The maximum bound, exclusive.
   * @return If the proto is within the given range.
   */
  public boolean isInRange(Proto min, Proto max) {
    return isAfterOrAt(min) && isBefore(max);
  }

}
