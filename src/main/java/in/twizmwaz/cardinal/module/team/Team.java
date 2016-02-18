package in.twizmwaz.cardinal.module.team;

import lombok.Data;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Data
public class Team extends ArrayList<Player> {

  private final String id;
  private final ChatColor color;
  private final ChatColor overheadColor;
  private final boolean plural;
  private final NameTagVisibility nameTagVisibility;
  private final int min;
  private final int max;
  private final int maxOverfill;

  private String name;

  @Override
  public boolean equals(Object object) {
    return super.equals(object) && object instanceof Team && ((Team) object).getId().equals(id);
  }

}
