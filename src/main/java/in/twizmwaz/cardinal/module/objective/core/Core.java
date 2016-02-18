package in.twizmwaz.cardinal.module.objective.core;

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.ProximityMetric;
import in.twizmwaz.cardinal.module.region.type.BoundedRegion;
import in.twizmwaz.cardinal.module.team.Team;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Getter
public class Core extends Objective implements Listener {

  private final String name;
  private final BoundedRegion region;
  private final int leak;
  private final ImmutablePair<Material, Integer> material;
  private final Team team;
  private final boolean modeChanges;
  private final ProximityMetric proximityMetric;
  private final boolean proximityHorizontal;

  private boolean complete;
  private List<Block> core;
  private List<Block> lava;

  public Core(String id, String name, boolean required, BoundedRegion region, int leak, ImmutablePair<Material, Integer> material, Team team, boolean modeChanges, boolean show, ProximityMetric proximityMetric, boolean proximityHorizontal) {
    super(id, required, show);
    this.name = name;
    this.region = region;
    this.leak = leak;
    this.material = material;
    this.team = team;
    this.modeChanges = modeChanges;
    this.proximityMetric = proximityMetric;
    this.proximityHorizontal = proximityHorizontal;

    core = Lists.newArrayList();
    lava = Lists.newArrayList();
    for (Block block : region.getBlocks()) {
      if (isPartOf(block)) {
        core.add(block);
      }
      Material type = block.getType();
      if (type.equals(Material.STATIONARY_LAVA) || type.equals(Material.LAVA)) {
        lava.add(block);
      }
    }
  }

  @EventHandler
  public void onBlockFromTo(BlockFromToEvent event) {
    Block from = event.getBlock();
    Block to = event.getToBlock();
    Material type = from.getType();
    if ((type.equals(Material.STATIONARY_LAVA) || type.equals(Material.LAVA)) && to.getType().equals(Material.AIR)) {
      if (new CoreModule().getClosestCore(to.getLocation().toVector()).equals(this) && !complete) { //TODO: Get core module from match
        Block bottomBlock = getBottomBlock();
        if (bottomBlock != null) {
          int distance = getBottomBlock().getY() - to.getY();
          if (distance >= leak) {
            complete = true;
          }
        }
      }
    }
  }

  private boolean isPartOf(@Nonnull Block block) {
    Material type = block.getType();
    if (material.getRight() == -1) {
      return material.getLeft().equals(type);
    }
    int dataValue = (int) block.getState().getMaterialData().getData();
    return material.getLeft().equals(type) && dataValue == material.getRight();
  }

  @Nullable
  private Block getBottomBlock() {
    Block bottomBlock = null;
    int bottomY = Integer.MAX_VALUE;
    for (Block block : core) {
      int y = block.getY();
      if (y < bottomY) {
        bottomBlock = block;
        bottomY = y;
      }
    }
    return bottomBlock;
  }

}
