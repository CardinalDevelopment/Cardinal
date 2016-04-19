package in.twizmwaz.cardinal.module.scoreboard.slot;

import in.twizmwaz.cardinal.module.scoreboard.ScoreboardSlot;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BlankScoreboardSlot implements ScoreboardSlot {

  private final String base;
  private final int position;

  @Override
  public String getPrefix() {
    return "";
  }

  @Override
  public String getSuffix() {
    return "";
  }

  public static String getNextBlankBase(List<String> used) {
    String base = " ";
    while (used.contains(base)) {
      base += " ";
    }
    return base;
  }

}
