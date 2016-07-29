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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.contributor.Contributor;
import in.twizmwaz.cardinal.util.Proto;
import lombok.Getter;
import lombok.NonNull;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ModuleEntry
public class RepositoryModule extends AbstractModule {

  @Getter
  private final Map<String, LoadedMap> loadedMaps = Maps.newHashMap();

  /**
   * Creates a new module instance.
   */
  public RepositoryModule() {
    //TODO: support alternate repos
    String mapRepository = Cardinal.getInstance().getConfig().getString("mapRepository");
    File mapRepositoryRoot = new File(Paths.get(mapRepository).isAbsolute() ? mapRepository : Cardinal.getInstance().getDataFolder().getAbsolutePath() + mapRepository);
    loadRepository(mapRepositoryRoot);
  }

  /**
   * @param file Recursively loads maps from a directory.
   */
  public void loadRepository(File file) {
    if (!file.exists()) {
      file.mkdir();
    }
    List<File> candidates = Lists.newArrayList();
    candidates.add(file);
    scanForDirectories(file).forEach(candidates::add);
    List<File> maps = Lists.newArrayList();
    maps.addAll(candidates.stream().filter(this::checkDirectory).collect(Collectors.toList()));
    Map<String, LoadedMap> loaded = Maps.newHashMap();
    maps.forEach(map -> {
      LoadedMap loadedMap = loadMap(map);
      if (loadedMap != null) {
        loaded.put(loadedMap.getName(), loadedMap);
      } else {
        Cardinal.getPluginLogger().warning("Failed to load map from " + map);
      }
    });
    loadedMaps.putAll(loaded);
    Cardinal.getPluginLogger().info("Loaded " + maps.size()
        + " maps from " + file.getAbsolutePath());
  }

  private List<File> scanForDirectories(@NonNull File file) {
    List<File> results = Lists.newArrayList();
    File[] list = file.listFiles();
    if (list != null) {
      for (File toCheck : list) {
        if (!toCheck.isDirectory()) {
          continue;
        }
        results.add(toCheck);
        results.addAll(scanForDirectories(toCheck));
      }
    }
    return results;
  }

  private boolean checkDirectory(@NonNull File file) {
    List<String> requirements = Arrays.asList("map.xml", "region", "level.dat");
    if (file.listFiles() != null) {
      if (Arrays.asList(file.list()).containsAll(requirements)) {
        return true;
      }
    }
    return false;
  }

  private LoadedMap loadMap(@NonNull File file) {
    Cardinal.getPluginLogger().info("Loading map from " + file.getAbsolutePath());
    SAXBuilder builder = new SAXBuilder();
    builder.setJDOMFactory(new LocatedJDOMFactory());
    try {
      Document doc = builder.build(new FileInputStream(file.getAbsolutePath() + "/map.xml"));
      Element root = doc.getRootElement();
      Proto proto = Proto.parseProto(root.getAttributeValue("proto"));
      String name = root.getChildText("name");
      String gamemode = root.getChildText("gamemode");
      LoadedMap.Edition edition = LoadedMap.Edition.forName(root.getChildText("edition"));
      String objective = root.getChildText("objective");
      Map<Contributor, String> authors = Maps.newHashMap();
      for (Element authorsElement : root.getChildren("authors")) {
        for (Element author : authorsElement.getChildren()) {
          // putContributor(author, authors);
        }
      }
      Map<Contributor, String> contributors = Maps.newHashMap();
      for (Element contributorsElement : root.getChildren("contributors")) {
        for (Element contributor : contributorsElement.getChildren()) {
          // putContributor(contributor, contributors);
        }
      }

      return
          new LoadedMap(file, doc, proto, name, gamemode,
              edition, objective, authors, contributors, 0);
    } catch (NullPointerException | JDOMException | IOException ex) {
      if (Cardinal.getInstance().getConfig().getBoolean("displayMapLoadErrors")) {
        ex.printStackTrace();
      }
      return null;
    }
  }
}
