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

import com.google.common.collect.Maps;
import in.twizmwaz.cardinal.Cardinal;
import lombok.Getter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nonnull;

/**
 * Object used to load modules from packages.
 */
public final class ModuleLoader {

  private static final String MODULE_DESCRIPTOR = Type.getDescriptor(ModuleEntry.class);

  @Getter
  private final Map<String, Method> moduleEntries = Maps.newHashMap();

  /**
   * Loads entries from a specified package.
   * @param file Jar to load modules from. The jar must already be available on the classpath.
   */
  @SuppressWarnings("unchecked")
  public void findEntries(@Nonnull File file) throws IOException {
    Cardinal.getPluginLogger().info("Loading modules from " + file.getAbsolutePath());
    HashMap<String, String> methods = Maps.newHashMap();
    Map<String, Method> found = Maps.newHashMap();
    ZipFile zipFile = new ZipFile(file);
    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
        continue;
      }
      try (InputStream in = zipFile.getInputStream(entry)) {
        ClassReader reader = new ClassReader(in);
        ClassNode node = new ClassNode();
        reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        ((List<MethodNode>) node.methods).forEach(method -> {
          if (method.visibleAnnotations != null) {
            ((List<AnnotationNode>) method.visibleAnnotations).forEach(annotation -> {
              if (annotation.desc.equals(MODULE_DESCRIPTOR)) {
                methods.put(node.name.replace('/', '.'), method.name);
              }
            });
          }
        });
      }
      methods.forEach((classString, methodString) -> {
        try {
          Class clazz = Class.forName(classString);
          Method method = clazz.getDeclaredMethod(methodString);
          found.put(method.getAnnotation(ModuleEntry.class).value(), method);
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
          Cardinal.getInstance().getLogger().info("ASM found module '"
              + classString + "." + methodString
              + "' but it could not be located, skipping.");
        }
      });
    }
    Cardinal.getPluginLogger().info("Found " + found.size() + " modules");
    moduleEntries.putAll(found);
  }

  /**
   * @param entries Module entries to be loaded.
   * @return The completed map of modules.
   */
  @Nonnull
  public Map<String, Module> makeModules(@Nonnull Map<String, Method> entries) {
    Map<String, Module> results = Maps.newHashMap();
    for (Map.Entry<String, Method> entry : entries.entrySet()) {
      try {
        results.put(entry.getKey(), (Module) entry.getValue().invoke(null));
      } catch (InvocationTargetException | IllegalAccessException ex) {
        Cardinal.getPluginLogger().warning("Failed to load " + entry.getKey() + ", skipping");
        ex.printStackTrace();
      }
    }
    return results;
  }

}
