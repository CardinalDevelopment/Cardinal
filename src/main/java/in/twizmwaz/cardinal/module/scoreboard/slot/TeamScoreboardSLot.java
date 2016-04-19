package in.twizmwaz.cardinal.module.scoreboard.slot;

import in.twizmwaz.cardinal.module.scoreboard.ScoreboardSlot;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.util.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeamScoreboardSlot implements ScoreboardSlot {

  private final Team team;
  private final String base;
  private final int position;

  @Override
  public String getPrefix() {
    return Strings.trim(getFormattedText(), 0, 16);
  }

  @Override
  public String getSuffix() {
    return Strings.trim(getFormattedText(), 16, 32);
  }

  private String getFormattedText() {
    return team.getColor() + team.getName();
  }

  public static String getNextTeamBase(Team team, List<String> used) {
    String base = team.getColor() + "";
    while (used.contains(base)) {
      base += team.getColor();
    }
    return base;
  }

}
