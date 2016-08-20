package in.twizmwaz.cardinal.module.proximity;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.objective.Objective;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class AbstractProximal extends Objective implements Proximal {

  private final List<Proximity> proximityList;
  private Proximity currentProximity;

  public AbstractProximal(
      @NonNull Match match, @NonNull String id, boolean required, boolean show,
      @NonNull List<Proximity> proximityList
  ) {
    super(match, id, required, show);

    this.proximityList = proximityList;
    this.currentProximity = proximityList.get(0);
  }

}
