package in.twizmwaz.cardinal.module.group.groups;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.match.MatchThread;

public class PlayerGroupData extends GroupData<PlayerGroup> {

  /**
   * Creates new group data with a match thread, match, and player container.
   *
   * @param matchThread The match thread.
   * @param match       The match.
   * @param group       The player group.
   */
  public PlayerGroupData(MatchThread matchThread, Match match, PlayerGroup group) {
    super(matchThread, match, group);
  }

}
