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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.Material;

import java.util.List;
import javax.annotation.Nonnull;

public class Materials {

  /**
   * @param in The input string that is used to get the material pattern.
   * @return The list of material pairs based on the input string.
   */
  @Nonnull
  public static List<ImmutablePair<Material, Integer>> getMaterialPattern(@Nonnull String in) {
    List<ImmutablePair<Material, Integer>> pattern = Lists.newArrayList();
    if (in.contains(";")) {
      for (String singlePattern : in.split(";")) {
        pattern.add(getSingleMaterialPattern(singlePattern));
      }
    } else {
      pattern.add(getSingleMaterialPattern(in));
    }
    return pattern;
  }

  /**
   * @param in The input string that is used to get the single material pattern.
   * @return The material pair based on the input string.
   */
  @Nonnull
  public static ImmutablePair<Material, Integer> getSingleMaterialPattern(@Nonnull String in) {
    if (in.contains(":")) {
      String[] parts = in.split(":");
      return new ImmutablePair<>(Material.matchMaterial(parts[0]), Numbers.parseInteger(parts[1]));
    } else {
      return new ImmutablePair<>(Material.matchMaterial(in), -1);
    }
  }

}