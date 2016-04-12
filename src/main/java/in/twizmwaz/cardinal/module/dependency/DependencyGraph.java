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

package in.twizmwaz.cardinal.module.dependency;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public final class DependencyGraph<T> {

  private Map<T, DependencyNode<T>> nodes = Maps.newHashMap();

  /**
   * @param object The object to add to the graph.
   */
  public void add(T object) {
    nodes.putIfAbsent(object, new DependencyNode<T>(object));
  }

  /**
   * Adds a dependency. This method will add the objects to the graph if they are not already present.
   *
   * @param object     The object to hold the dependency.
   * @param dependency The dependency object.
   */
  public void addDependency(T object, T dependency) {
    add(object);
    add(dependency);
    nodes.putIfAbsent(dependency, new DependencyNode<>(dependency));
    if (hasDependency(dependency, object)) {
      throw new IllegalStateException("Cannot create dependency, creates a dependency loop");
    }
    nodes.get(object).addDependency(nodes.get(dependency));
  }

  /**
   * @return The ordered list of modules.
   */
  public List<T> evaluateDependencies() {
    List<DependencyNode<T>> evaluated = Lists.newArrayList();
    List<T> results = Lists.newArrayList();
    while (!evaluated.containsAll(nodes.values())) {
      for (DependencyNode<T> node : nodes.values()) {
        if (!evaluated.contains(node)) {
          for (DependencyNode<T> dep : node.getDependencies()) {
            if (!evaluated.contains(dep)) {
              results.add(dep.getValue());
              evaluated.add(dep);
            }
          }
          results.add(node.getValue());
          evaluated.add(node);
        }
      }
    }
    return results;
  }

  /**
   * @param object     The object to contain the dependency.
   * @param dependency The dependency object.
   * @return If the dependency exists.
   */
  public boolean hasDependency(T object, T dependency) {
    DependencyNode<T> objectNode = nodes.get(object);
    DependencyNode<T> dependencyNode = nodes.get(dependency);
    return generateDependencies(objectNode).contains(dependencyNode);
  }

  private List<DependencyNode<T>> generateDependencies(DependencyNode<T> node) {
    List<DependencyNode<T>> results = Lists.newArrayList();
    for (DependencyNode<T> dependency : node.getDependencies()) {
      results.add(dependency);
      results.addAll(generateDependencies(dependency));
    }
    return results;
  }

}