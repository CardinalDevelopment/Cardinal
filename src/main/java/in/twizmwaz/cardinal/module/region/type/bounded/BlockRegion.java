package in.twizmwaz.cardinal.module.region.type.bounded;

import in.twizmwaz.cardinal.module.region.type.BoundedRegion;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BlockRegion extends BoundedRegion {

  private final Vector vector;

  public BlockRegion(String name, Vector vector) {
    super(name);
    this.vector = vector;
  }

  @Override
  public List<Block> getBlocks() {
    List<Block> blocks = new ArrayList<>();
    blocks.add(getBlock());
    return blocks;
  }

  @Override
  public boolean contains(Vector vector) {
    return vector.getBlockX() == getVector().getBlockX() &&
            vector.getBlockY() == getVector().getBlockY() &&
            vector.getBlockZ() == getVector().getBlockZ();
  }

  @Override
  public BlockRegion getCenterBlock() {
    return this;
  }

  public Vector getVector() {
    return vector.clone().add(0.5, 0.5, 0.5);
  }

  public Location getLocation() {
    return getVector().toLocation(null); //TODO: Get match world
  }

  public Block getBlock() {
    return getLocation().getBlock();
  }

}
