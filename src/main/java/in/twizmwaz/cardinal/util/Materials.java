package in.twizmwaz.cardinal.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.Material;

import java.util.List;
import javax.annotation.Nonnull;

public class Materials {

  @Nonnull
  public static List<ImmutablePair<Material, Integer>> getMaterialPattern(@Nonnull String in) {
    List<ImmutablePair<Material, Integer>> pattern = Lists.newArrayList();
    if (in.contains(";")) {
      for (String singlePattern : in.split(";")) {
        pattern.add(getSingleMaterialPattern(singlePattern));
      }
    } else {
      pattern.add(getSingleMaterialPattern(in));
    }
    return pattern;
  }

  @Nonnull
  public static ImmutablePair<Material, Integer> getSingleMaterialPattern(@Nonnull String in) {
    if (in.contains(":")) {
      String[] parts = in.split(":");
      return new ImmutablePair<>(Material.matchMaterial(parts[0]), Numbers.parseInteger(parts[1]));
    } else {
      return new ImmutablePair<>(Material.matchMaterial(in), -1);
    }
  }

}
