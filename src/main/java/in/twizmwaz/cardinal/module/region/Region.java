package in.twizmwaz.cardinal.module.region;

import in.twizmwaz.cardinal.module.region.type.bounded.BlockRegion;
import lombok.Data;
import org.bukkit.util.Vector;

@Data
public abstract class Region {

  private final String id;

  public abstract boolean contains(Vector vector);

  public abstract BlockRegion getCenterBlock();

}
