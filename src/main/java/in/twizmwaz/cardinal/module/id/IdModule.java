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

package in.twizmwaz.cardinal.module.id;

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ModuleEntry
public class IdModule extends AbstractModule {

  private static IdModule idModule;
  private final Map<Match, Map<String, Object>> ids = new HashMap<>();

  public IdModule() {
    IdModule.idModule = this;
  }

  @Override
  public boolean loadMatch(@NonNull Match match) {
    ids.put(match, new HashMap<>());
    return true;
  }

  @Override
  public void clearMatch(@NonNull Match match) {
    if (ids.containsKey(match)) {
      ids.remove(match);
    }
  }

  /**
   * Gets the id module, used for easy access from outside classes.
   * @return the IdModule.
   */
  public static IdModule get() {
    return idModule;
  }

  /**
   * Adds an object to a match, only if the id is not already present.
   * @param match The match to add the object to.
   * @param id The id to add the object with, can't be duplicated.
   * @param object The object to add.
   * @return if the object was successfully added.
   */
  public boolean add(Match match, String id, Object object) {
    return add(match, id, object, false);
  }

  /**
   * Adds an object to a match, only if the id is not already present or it will be added with a random id if force.
   * @param match The match to add the object to.
   * @param id The id to add the object with, can't be duplicated.
   * @param object The object to add.
   * @param force Add the object with a random id if the id is null or already in use.
   * @return if the object was successfully added with the provided id.
   */
  public boolean add(Match match, String id, Object object, boolean force) {
    if (id != null && !ids.get(match).containsKey(id)) {
      ids.get(match).put(id, object);
      return true;
    } else if (force) {
      add(match, UUID.randomUUID().toString(), object, true);
    }
    return false;
  }

  /**
   * Get an object with the given id.
   * @param match The match the object belongs to.
   * @param id The id of the object.
   * @param clazz The class of the object you want to retrieve.
   * @param <T> The object type.
   * @return The object if id is present and it's the correct type, null otherwise.
   */
  public <T> T get(Match match, String id, Class<T> clazz) {
    return get(match, id, clazz, false);
  }

  public <T> T get(Match match, String id, Class<T> clazz, boolean caseSensitive) {
    if (ids.get(match).containsKey(id)) {
      Object obj = ids.get(match).get(id);
      if (clazz.isInstance(obj)) {
        return (T) obj;
      }
    }
    if (!caseSensitive) {
      return null;
    }
    Map<String, T> map = getMap(match, clazz);

    for (String idVal : map.keySet()) {
      if (idVal.replaceAll(" ", "").equalsIgnoreCase(id.replaceAll(" ", ""))) {
        return map.get(idVal);
      }
    }
    for (String idVal : map.keySet()) {
      if (idVal.replaceAll(" ", "").toLowerCase().startsWith(id.replaceAll(" ", "").toLowerCase())) {
        return map.get(idVal);
      }
    }
    for (String idVal : map.keySet()) {
      if (idVal.replaceAll(" ", "-").equalsIgnoreCase(id.replaceAll(" ", "-"))) {
        return map.get(idVal);
      }
    }
    for (String idVal : map.keySet()) {
      if (idVal.replaceAll(" ", "-").toLowerCase().startsWith(id.replaceAll(" ", "-").toLowerCase())) {
        return map.get(idVal);
      }
    }
    return null;
  }
  

  /**
   * Get a list of all objects of the given class
   * @param match The match the objects belong to.
   * @param clazz The class of the objects you want to retrieve.
   * @param <T> The object type.
   * @return A List with all the objects of that type.
   */
  public <T> List<T> getList(Match match, Class<T> clazz) {
    List<T> result = Lists.newArrayList();
    for (Object obj : ids.get(match).values()) {
      if (clazz.isInstance(obj)) {
        result.add((T) obj);
      }
    }
    return result;
  }

  /**
   * Get a id object map for all objects of the given class
   * @param match The match the objects belong to.
   * @param clazz The class of the objects you want to retrieve.
   * @param <T> The object type.
   * @return A map with all the objects of that type and their id's.
   */
  public <T> Map<String, T> getMap(Match match, Class<T> clazz) {
    Map<String, T> result = new HashMap<>();
    for (Map.Entry<String, Object> entry : ids.get(match).entrySet()) {
      if (clazz.isInstance(entry.getValue())) {
        result.put(entry.getKey(), (T) entry.getValue());
      }
    }
    return result;
  }

}
