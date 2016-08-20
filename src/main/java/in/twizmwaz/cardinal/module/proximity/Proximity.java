package in.twizmwaz.cardinal.module.proximity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Proximity {

  private final Identifier id;
  private final ProximityRule rule;

  private double proximity;

  public enum Identifier {

    BEFORE_TOUCH,
    BEFORE_COMPLETE

  }

}
