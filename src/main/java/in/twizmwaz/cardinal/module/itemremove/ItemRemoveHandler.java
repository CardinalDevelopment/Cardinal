package in.twizmwaz.cardinal.module.itemremove;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.util.MaterialType;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.material.MaterialData;

import java.util.List;

@RequiredArgsConstructor
public class ItemRemoveHandler implements Listener {

  private final Match match;
  private final List<MaterialType> types;

  @EventHandler(ignoreCancelled = true)
  public void onItemSpawn(ItemSpawnEvent event) {
    MaterialData data = event.getEntity().getItemStack().getData();
    for (MaterialType type : types) {
      if (type.isType(data)) {
        event.setCancelled(true);
        break;
      }
    }
  }

}
