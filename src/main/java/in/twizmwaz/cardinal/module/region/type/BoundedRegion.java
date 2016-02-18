package in.twizmwaz.cardinal.module.region.type;

import in.twizmwaz.cardinal.module.region.Region;
import org.bukkit.block.Block;

import java.util.List;

public abstract class BoundedRegion extends Region {

  public BoundedRegion(String id) {
    super(id);
  }

  public abstract List<Block> getBlocks();

}
