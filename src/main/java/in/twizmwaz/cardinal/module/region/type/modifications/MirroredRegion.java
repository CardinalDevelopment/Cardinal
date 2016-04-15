package in.twizmwaz.cardinal.module.region.type.modifications;

import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionBounds;
import in.twizmwaz.cardinal.util.Vectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.util.Vector;

@AllArgsConstructor
public class MirroredRegion implements Region {

  private Region region;
  private Vector origin;
  private Vector normal;
  @Getter
  private RegionBounds bounds;

  public MirroredRegion(Region region, Vector origin, Vector normal) {
    this(region, origin, normal.normalize(), region.getBounds().mirror(origin, normal.normalize()));
  }

  @Override
  public boolean evaluate(Vector vector) {
    return region.evaluate(Vectors.getMirroredVector(vector, origin, normal));
  }

}
