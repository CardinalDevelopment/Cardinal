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
import in.twizmwaz.cardinal.module.dependency.DependencyGraph;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang.Validate;

import java.util.List;
import java.util.Map;

@Getter(AccessLevel.PACKAGE)
public final class ModuleRegistry {

  private final BiMap<Class, Module> modules;
  private final List<Module> loadOrder;

  /**
   * Creates a new {@link ModuleRegistry}
   *
   * @param modules The modules in the registry to be created.
   */
  public ModuleRegistry(@NonNull Map<Class, Module> modules) {
    Validate.notNull(modules);
    this.modules = new ImmutableBiMap.Builder<Class, Module>().putAll(modules).build();
    DependencyGraph<Module> graph = new DependencyGraph<Module>();
    this.modules.values().forEach(module -> {
      graph.add(module);
      if (module.getDepends() != null) {
        for (Class dep : module.getDepends()) {
          graph.addDependency(module, modules.get(dep));
        }
      }
      if (module.getLoadBefore() != null) {
        for (Class dep : module.getLoadBefore()) {
          graph.addDependency(modules.get(dep), module);
        }
      }
    });
    loadOrder = graph.evaluateDependencies();
  }

  /**
   * @param clazz The module class to be found.
   * @param <T> The module class type.
   * @return The found module object, if any.
   */
  @SuppressWarnings("unchecked")
  public <T extends Module> T getModule(@NonNull Class<T> clazz) {
    Validate.notNull(clazz);
    for (Module module : modules.values()) {
      if (clazz.isInstance(module)) {
        return ((T) module);
      }
    }
    return null;
  }

}
