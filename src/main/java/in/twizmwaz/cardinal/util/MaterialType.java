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

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Optional;

@Getter
public class MaterialType {

  private final Material material;
  private final Optional<Byte> data;

  public MaterialType(@NonNull Material material) {
    this.material = material;
    data = Optional.empty();
  }

  public MaterialType(@NonNull Material material, byte data) {
    this.material = material;
    this.data = Optional.of(data);
  }

  public boolean isType(@NonNull MaterialData data) {
    return data.getItemType().equals(material) && (!this.data.isPresent() || this.data.get() == data.getData());
  }

  /**
   * Parses a string for a {@link MaterialType} by the format material:data.
   *
   * @param str The string to be parsed.
   * @return The parsed {@link MaterialType}
   */
  public static MaterialType parse(@NonNull String str) {
    if (str.contains(":")) {
      String[] materialData = str.split(":");
      return new MaterialType(Material.matchMaterial(materialData[0]), Byte.parseByte(materialData[1]));
    } else {
      return new MaterialType(Material.matchMaterial(str));
    }
  }

}
