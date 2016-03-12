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
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MaterialPattern {

  public static final int ANY_DATA_VALUE = -1;

  @Getter
  private final List<Map.Entry<Material, Integer>> materials;

  public MaterialPattern(Map.Entry<Material, Integer>... materials) {
    this.materials = Lists.newArrayList();
    Collections.addAll(this.materials, materials);
  }

  public void add(Material type, int dataValue) {
    materials.add(new AbstractMap.SimpleEntry<>(type, dataValue));
  }

  public void add(MaterialPattern pattern) {
    pattern.getMaterials().forEach(material -> add(material.getKey(), material.getValue()));
  }

  public boolean contains(Material type, int dataValue) {
    if (materials.isEmpty()) {
      return true;
    }
    for (Map.Entry<Material, Integer> material : materials) {
      if (material.getKey().equals(type) && (material.getValue() == ANY_DATA_VALUE || material.getValue() == dataValue)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param in The input string that is used to get the single material pattern.
   * @return The material pair based on the input string.
   */
  @NonNull
  public static MaterialPattern getSingleMaterialPattern(@NonNull String in) {
    MaterialPattern pattern = new MaterialPattern();
    if (in.contains(":")) {
      String[] parts = in.split(":");
      pattern.add(Material.matchMaterial(parts[0]), Numbers.parseInteger(parts[1]));
    } else {
      pattern.add(Material.matchMaterial(in), ANY_DATA_VALUE);
    }
    return pattern;
  }

  /**
   * @param in The input string that is used to get the material pattern.
   * @return The list of material pairs based on the input string.
   */
  @NonNull
  public static MaterialPattern getMaterialPattern(@NonNull String in) {
    MaterialPattern pattern = new MaterialPattern();
    if (in.contains(";")) {
      for (String singlePattern : in.split(";")) {
        pattern.add(getSingleMaterialPattern(singlePattern));
      }
    } else {
      pattern.add(getSingleMaterialPattern(in));
    }
    return pattern;
  }

}
