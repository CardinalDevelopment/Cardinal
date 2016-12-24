package in.twizmwaz.cardinal.module.group.groups;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.match.MatchThread;

public class CompetitorGroupData extends GroupData<CompetitorGroup> {

  /**
   * Creates new group data with a match thread, match, and player container.
   *
   * @param matchThread The match thread.
   * @param match       The match.
   * @param group       The player group.
   */
  public CompetitorGroupData(MatchThread matchThread, Match match, CompetitorGroup group) {
    super(matchThread, match, group);
  }

}
