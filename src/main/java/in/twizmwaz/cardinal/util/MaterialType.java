package in.twizmwaz.cardinal.util;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Optional;

@Getter
public class MaterialType {

  private final Material material;
  private final Optional<Byte> data;

  public MaterialType(@NonNull Material material) {
    this.material = material;
    data = Optional.empty();
  }

  public MaterialType(@NonNull Material material, byte data) {
    this.material = material;
    this.data = Optional.of(data);
  }

  public boolean isType(@NonNull MaterialData data) {
    return data.getItemType().equals(material) && (!this.data.isPresent() || this.data.get() == data.getData());
  }

  public static MaterialType parse(@NonNull String str) {
    if (str.contains(":")) {
      String[] materialData = str.split(":");
      return new MaterialType(Material.matchMaterial(materialData[0]), Byte.parseByte(materialData[1]));
    } else {
      return new MaterialType(Material.matchMaterial(str));
    }
  }

}
