package in.twizmwaz.cardinal.event.proximity;

import in.twizmwaz.cardinal.event.ProximityEvent;
import in.twizmwaz.cardinal.module.proximity.Proximal;
import in.twizmwaz.cardinal.module.proximity.Proximity;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class ProximityChangeEvent extends ProximityEvent {

  @Getter private static final HandlerList handlerList = new HandlerList();

  private double newProximity;

  public ProximityChangeEvent(@NonNull Proximal proximal, @NonNull Proximity proximity, double newProximity) {
    super(proximal, proximity);

    this.newProximity = newProximity;
  }

  @Override
  public HandlerList getHandlers() {
    return handlerList;
  }

}
