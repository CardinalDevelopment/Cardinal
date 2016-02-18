package in.twizmwaz.cardinal.module.objective.wool;

import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.ProximityMetric;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.util.Strings;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

@Getter
public class Wool extends Objective implements Listener {

  private final Team team;
  private final DyeColor color;
  private final Region monument;
  private final boolean craftable;
  private final Vector location;
  private final ProximityMetric woolProximityMetric;
  private final boolean woolProximityHorizontal;
  private final ProximityMetric monumentProximityMetric;
  private final boolean monumentProximityHorizontal;

  private boolean complete;

  public Wool(String id, boolean required, Team team, DyeColor color, Region monument, boolean craftable, boolean show, Vector location, ProximityMetric woolProximityMetric, boolean woolProximityHorizontal, ProximityMetric monumentProximityMetric, boolean monumentProximityHorizontal) {
    super(id, required, show);
    this.team = team;
    this.color = color;
    this.monument = monument;
    this.craftable = craftable;
    this.location = location;
    this.woolProximityMetric = woolProximityMetric;
    this.woolProximityHorizontal = woolProximityHorizontal;
    this.monumentProximityMetric = monumentProximityMetric;
    this.monumentProximityHorizontal = monumentProximityHorizontal;

    complete = false;
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    Block block = event.getBlock();
    if (monument.contains(block.getLocation().toVector()) && !complete) {
      if (block.getType().equals(Material.WOOL)) {
        if (((org.bukkit.material.Wool) block.getState().getMaterialData()).getColor().equals(color)) {
          complete = true;
        } else {
          event.setCancelled(true);
        }
      } else {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    if (monument.contains(event.getBlock().getLocation().toVector())) {
      event.setCancelled(true);
    }
  }

  @Nonnull
  public String getName() {
    return WordUtils.capitalizeFully(Strings.getSimpleName(color.name())) + " Wool";
  }

}
