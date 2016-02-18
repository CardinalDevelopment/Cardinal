package in.twizmwaz.cardinal.module.objective;

import lombok.Data;

@Data
public abstract class Objective {

  private final String id;
  private final boolean required;
  private final boolean show;

}
