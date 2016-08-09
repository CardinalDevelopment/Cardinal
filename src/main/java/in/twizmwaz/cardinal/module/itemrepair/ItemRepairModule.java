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

package in.twizmwaz.cardinal.module.itemrepair;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.repository.LoadedMap;
import in.twizmwaz.cardinal.util.MaterialType;
import lombok.NonNull;
import org.bukkit.event.HandlerList;
import org.jdom2.Element;
import org.jdom2.located.Located;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleEntry
public class ItemRepairModule extends AbstractModule {

  private final Map<Match, ItemRepairHandler> handlers = new HashMap<>();

  @Override
  public boolean loadMatch(@NonNull Match match) {
    List<MaterialType> types = new ArrayList<>();

    LoadedMap map = match.getMap();
    for (Element element : map.getDocument().getRootElement().getChildren("item-repair")) {
      for (Element typeElement : element.getChildren("type")) {
        Located located = (Located) typeElement;
        String text = typeElement.getText();
        if (text == null) {
          errors.add(new ModuleError(this, map, new String[]{
              "No type specified for item repair",
              "Element at " + located.getLine() + ", " + located.getColumn()
          }, false));
          continue;
        }
        try {
          types.add(MaterialType.parse(text));
        } catch (IllegalArgumentException e) {
          errors.add(new ModuleError(this, map, new String[]{
              "Invalid type specified for item repair",
              "Element at " + located.getLine() + ", " + located.getColumn()
          }, false));
        }
      }
    }

    ItemRepairHandler handler = new ItemRepairHandler(match, types);
    Cardinal.registerEvents(handler);
    handlers.put(match, handler);

    return true;
  }

  @Override
  public void clearMatch(@NonNull Match match) {
    HandlerList.unregisterAll(handlers.get(match));
    handlers.remove(match);
  }

}
