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
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ModuleEntry
public class RepositoryModule extends AbstractModule {

  @Getter
  private final Map<String, LoadedMap> loadedMaps = Maps.newHashMap();

  /**
   * Creates a new module instance.
   */
  public RepositoryModule() {
    //TODO: support alternate repos
    File repoRoot = new File(Cardinal.getInstance().getDataFolder().getAbsolutePath() + "/repo");
    loadRepository(repoRoot);
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
    scanForDirectories(file).forEach(found -> {
      candidates.add(found);
    });
    List<File> maps = Lists.newArrayList();
    maps.addAll(candidates.stream().filter(candidate ->
        checkDirectory(candidate)).collect(Collectors.toList()));
    Cardinal.getPluginLogger().info("Loaded " + maps.size()
        + " maps from " + file.getAbsolutePath());
  }

  private List<File> scanForDirectories(@Nonnull File file) {
    List<File> results = Lists.newArrayList();
    for (File toCheck : file.listFiles((FileFilter) DirectoryFileFilter.INSTANCE)) {
      results.add(toCheck);
      results.addAll(scanForDirectories(toCheck));
    }
    return results;
  }

  private boolean checkDirectory(@Nonnull File file) {
    List<String> requirements = Arrays.asList("map.xml", "region", "level.dat");
    if (file.listFiles() != null) {
      for (File map : file.listFiles()) {
        if (map.isFile()) {
          continue;
        }
        if (Arrays.asList(map.list()).containsAll(requirements)) {
          return true;
        }
      }
    }
    return false;
  }

  @Nullable
  private LoadedMap loadMap(File file) {
    SAXBuilder builder = new SAXBuilder();
    builder.setJDOMFactory(new LocatedJDOMFactory());
    try {
      Document doc = builder.build(new FileInputStream(file.getAbsolutePath() + "map.xml"));
      Element root = doc.getRootElement();
      Proto proto = Proto.parseProto(root.getChildText("proto"));
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
          new LoadedMap(file, doc, proto, gamemode, edition, objective, authors, contributors, 0);
    } catch (JDOMException | IOException ex) {
      ex.printStackTrace();
      return null;
    }
  }
}
