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

package in.twizmwaz.cardinal.module;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.Validate;
import org.jdom2.Document;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Object to control modules.
 */
@RequiredArgsConstructor
public class ModuleHandler {

  @Getter
  private final ModuleRegistry registry;

  public void clearMatch() {
    registry.getModules().entrySet().forEach(entry -> entry.getValue().clearMatch());
  }

  /**
   * @param document The document modules should load the map from
   * @return If the modules loaded successfully.
   */
  public boolean loadMatch(@Nonnull Document document) {
    Validate.notNull(document);
    // Already loaded modules
    List<String> completed = Lists.newArrayList();
    // As long as all modules are not loaded
    while (completed.size() < registry.getModules().size()) {
      // For each
      for (Module module : registry.getModules().values()) {
        // If all dependent modules are completed but this one isn't
        if (completed.contains(module.getName())
            && completed.containsAll(Arrays.asList(module.getDepends()))) {
          // Load and return false if it fails
          if (!module.loadMatch(document)) {
            return false;
          }
        }
      }
    }
    // If all goes well
    return true;
  }

}
