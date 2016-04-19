package in.twizmwaz.cardinal.module.scoreboard.slot;

import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.scoreboard.ScoreboardSlot;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class ObjectiveScoreboardSlot implements ScoreboardSlot {

  private final Objective objective;
  private final int position;

}
