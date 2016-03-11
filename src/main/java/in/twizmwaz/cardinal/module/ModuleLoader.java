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
import com.google.common.collect.Sets;
import in.twizmwaz.cardinal.Cardinal;
import lombok.Getter;
import lombok.NonNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Object used to load modules from packages.
 */
public final class ModuleLoader {

  private static final String MODULE_DESCRIPTOR = Type.getDescriptor(ModuleEntry.class);

  @Getter
  private final Set<Class> moduleEntries = Sets.newHashSet();

  /**
   * Loads entries from a specified package.
   *
   * @param file Jar to load modules from. The jar must already be available on the classpath.
   */
  @SuppressWarnings("unchecked")
  public void findEntries(@NonNull File file) throws IOException {
    Cardinal.getPluginLogger().info("Loading modules from " + file.getAbsolutePath());
    Set<String> classStrings = Sets.newHashSet();
    Set<Class> found = Sets.newHashSet();
    // The Jar to load modules from
    ZipFile zipFile = new ZipFile(file);
    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    // Basically a for each ZipEntry
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      // If it isn't a class, skip it
      if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
        continue;
      }
      try (InputStream in = zipFile.getInputStream(entry)) {
        // Parse the class file
        ClassReader reader = new ClassReader(in);
        ClassNode node = new ClassNode();
        // Skip the parts we aren't concerned with
        reader.accept(node,
            ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        // For each annotation
        if (node.visibleAnnotations != null) {
          node.visibleAnnotations.forEach(annotation -> {
            // If it is our descriptor, save this one
            if (((AnnotationNode) annotation).desc.equalsIgnoreCase(MODULE_DESCRIPTOR)) {
              classStrings.add(node.name.replace('/', '.'));
            }
          });
        }
      }
    }
    // Now that we have located ModuleEntries, for each
    classStrings.forEach(classString -> {
      try {
        // Get the class from the name ASM found
        Class clazz = Class.forName(classString);
        // And save it for later
        found.add(clazz);
      } catch (ClassNotFoundException ex) {
        Cardinal.getPluginLogger().info("ASM found module '"
            + classString + "' but it could not be located, skipping.");
      }
    });

    Cardinal.getPluginLogger().info("Identified " + classStrings.size() + " modules");
    Cardinal.getPluginLogger().info("Found " + found.size() + " modules");
    moduleEntries.addAll(found);
  }

  /**
   * @param entries Module entries to be loaded.
   * @return The completed map of modules.
   */
  @NonNull
  @SuppressWarnings("unchecked")
  public Map<Class, Module> makeModules(@NonNull Set<Class> entries) {
    Map<Class, Module> results = Maps.newHashMap();
    entries.forEach(entry -> {
      try {
        // Invoke the entry point and add it to the return map
        Module invoked = (Module) entry.getConstructor().newInstance();
        results.put(invoked.getClass(), invoked);
      } catch (NoSuchMethodException | InvocationTargetException
          | IllegalAccessException | InstantiationException ex) {
        Cardinal.getPluginLogger().warning("Failed to load " + entry.getName() + ", skipping");
        ex.printStackTrace();
      }
    });
    Cardinal.getPluginLogger().info("Built " + results.size() + '/' + entries.size() + " modules");
    return results;
  }

}
