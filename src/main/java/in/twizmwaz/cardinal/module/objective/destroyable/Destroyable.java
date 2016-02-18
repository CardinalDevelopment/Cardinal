package in.twizmwaz.cardinal.module.objective.destroyable;

import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.ProximityMetric;
import in.twizmwaz.cardinal.module.region.type.BoundedRegion;
import in.twizmwaz.cardinal.module.team.Team;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import java.util.List;

@Getter
public class Destroyable extends Objective implements Listener {

  private final String name;
  private final BoundedRegion region;
  private final List<ImmutablePair<Material, Integer>> materials;
  private final Team owner;
  private final double completion;
  private final boolean modeChanges;
  private final boolean showProgress;
  private final boolean repairable;
  private final boolean sparks;
  private final ProximityMetric proximityMetric;
  private final boolean proximityHorizontal;


  public Destroyable(String id, String name, boolean required, BoundedRegion region, List<ImmutablePair<Material, Integer>> materials, Team owner, double completion, boolean modeChanges, boolean showProgress, boolean repairable, boolean sparks, boolean show, ProximityMetric proximityMetric, boolean proximityHorizontal) {
    super(id, required, show);
    this.name = name;
    this.region = region;
    this.materials = materials;
    this.owner = owner;
    this.completion = completion;
    this.modeChanges = modeChanges;
    this.showProgress = showProgress;
    this.repairable = repairable;
    this.sparks = sparks;
    this.proximityMetric = proximityMetric;
    this.proximityHorizontal = proximityHorizontal;
  }

}
