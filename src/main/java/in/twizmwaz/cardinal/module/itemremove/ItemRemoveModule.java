package in.twizmwaz.cardinal.module.itemremove;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
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

public class ItemRemoveModule extends AbstractModule {

  private final Map<Match, ItemRemoveHandler> handlers = new HashMap<>();

  @Override
  public boolean loadMatch(@NonNull Match match) {
    List<MaterialType> types = new ArrayList<>();

    LoadedMap map = match.getMap();
    for (Element element : map.getDocument().getRootElement().getChildren("item-remove")) {
      for (Element typeElement : element.getChildren("type")) {
        Located located = (Located) typeElement;
        String text = typeElement.getText();
        if (text == null) {
          errors.add(new ModuleError(this, map, new String[]{
            "No type specified for item remove at " + located.getLine() + ", " + located.getColumn()
          }, false));
          continue;
        }
        try {
          types.add(MaterialType.parse(text));
        } catch (IllegalArgumentException e) {
          errors.add(new ModuleError(this, map, new String[]{
            "Invalid type specified for item remove at " + located.getLine() + ", " + located.getColumn()
          }, false));
        }
      }
    }

    ItemRemoveHandler handler = new ItemRemoveHandler(match, types);
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
