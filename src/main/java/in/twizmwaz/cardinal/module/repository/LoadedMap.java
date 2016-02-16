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

package in.twizmwaz.cardinal.module.repository;

import in.twizmwaz.cardinal.module.contributor.Contributor;
import in.twizmwaz.cardinal.util.Proto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.jdom2.Document;

import java.util.Map;
import javax.annotation.Nullable;

@Data
public class LoadedMap {

  private final Document document;
  private final Proto proto;

  private final String gamemode;
  private final Edition edition;
  private final String objective;
  private final Map<Contributor, String> authors;
  private final Map<Contributor, String> contributors;
  private final int maxPlayers;

  @AllArgsConstructor
  @Getter
  public enum Edition {
    STANDARD("standard"),
    RANKED("ranked"),
    TOURNAMENT("tournament");

    private final String name;

    /**
     * @param name Name of edition.
     * @return The edition enum object.
     */
    @Nullable
    public static Edition forName(String name) {
      for (Edition edition : values()) {
        if (name.equalsIgnoreCase(edition.getName())) {
          return edition;
        }
      }
      return null;
    }
  }

}
