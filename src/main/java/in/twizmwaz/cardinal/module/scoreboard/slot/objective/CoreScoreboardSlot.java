package in.twizmwaz.cardinal.module.scoreboard.slot.objective;

import in.twizmwaz.cardinal.module.objective.core.Core;
import in.twizmwaz.cardinal.module.scoreboard.slot.ObjectiveScoreboardSlot;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.util.Strings;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

@Setter
public class CoreScoreboardSlot extends ObjectiveScoreboardSlot {

  private final Core core;
  private final Team viewing;
  private boolean proximity;

  public CoreScoreboardSlot(@NonNull Core core, int position, @NonNull Team viewing, boolean proximity) {
    super(core, position);
    this.core = core;
    this.viewing = viewing;
    this.proximity = proximity;
  }

  @Override
  public String getPrefix() {
    if (core.isComplete()) {
      return ChatColor.GREEN + "\u2714";
    } else if (core.isTouched() && !core.getTeam().equals(viewing)) {
      return ChatColor.YELLOW + "\u2733";
    } else if (!core.getTeam().equals(viewing) && proximity) {
      return ChatColor.RED + "\u2B1C " + getFormattedProximity();
    } else {
      return ChatColor.RED + "\u2B1C";
    }
  }

  private String getFormattedProximity() {
    //TODO: Core proximity
    return null;
  }

  @Override
  public String getBase() {
    return " " + Strings.trim(core.getName(), 0, 15);
  }

  @Override
  public String getSuffix() {
    return Strings.trim(core.getName(), 15, 31);
  }

}
