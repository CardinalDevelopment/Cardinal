package in.twizmwaz.cardinal.module.scoreboard.slot.objective;

import in.twizmwaz.cardinal.module.objective.wool.Wool;
import in.twizmwaz.cardinal.module.scoreboard.slot.ObjectiveScoreboardSlot;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.util.Colors;
import in.twizmwaz.cardinal.util.Strings;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

@Setter
public class WoolScoreboardSlot extends ObjectiveScoreboardSlot {

  private final Wool wool;
  private final Team viewing;
  private boolean proximity;

  public WoolScoreboardSlot(@NonNull Wool wool, int position, @NonNull Team viewing, boolean proximity) {
    super(wool, position);
    this.wool = wool;
    this.viewing = viewing;
    this.proximity = proximity;
  }

  @Override
  public String getPrefix() {
    ChatColor color = Colors.convertDyeToChatColor(wool.getColor());
    if (wool.isComplete()) {
      return color + "\u2B1B";
    } else if (wool.isTouched() && (wool.getTeam().equals(viewing) || Team.isObservers(viewing))) {
      return color + "\u2592" + (proximity ? " " + getFormattedProximity() : "");
    } else if (wool.getTeam().equals(viewing) || Team.isObservers(viewing) && proximity) {
      return color + "\u2B1C " + getFormattedProximity();
    } else {
      return color + "\u2B1C";
    }
  }

  private String getFormattedProximity() {
    //TODO: Wool proximity
    return null;
  }

  @Override
  public String getBase() {
    return " " + Strings.trim(wool.getName(), 0, 15);
  }

  @Override
  public String getSuffix() {
    return Strings.trim(wool.getName(), 15, 31);
  }

}
