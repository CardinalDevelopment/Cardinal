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

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang.Validate;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ModuleRegistry {

  @Getter(AccessLevel.PACKAGE)
  private final BiMap<Class, Module> modules;

  public ModuleRegistry(@Nonnull Map<Class, Module> modules) {
    Validate.notNull(modules);
    this.modules = new ImmutableBiMap.Builder<Class, Module>().putAll(modules).build();
  }

  /**
   * @param clazz The module class to be found.
   * @param <T> The module class type.
   * @return The found module object, if any.
   */
  @Nullable
  @SuppressWarnings("unchecked")
  public <T extends Module> T getModule(@Nonnull Class<T> clazz) {
    Validate.notNull(clazz);
    for (Module module : modules.values()) {
      if (clazz.isInstance(module)) {
        return ((T) module);
      }
    }
    return null;
  }

}
