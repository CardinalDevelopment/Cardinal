package in.twizmwaz.cardinal.module.region.type.modifications;

import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionBounds;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.util.Vector;

@AllArgsConstructor
public class TranslatedRegion implements Region {

  private Region region;
  private Vector offset;
  @Getter
  private RegionBounds bounds;

  public TranslatedRegion(Region region, Vector offset) {
    this(region, offset, region.getBounds().translate(offset));
  }

  @Override
  public boolean evaluate(Vector vector) {
    return region.evaluate(vector.minus(offset));
  }

}
