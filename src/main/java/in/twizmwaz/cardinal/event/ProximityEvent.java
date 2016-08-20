package in.twizmwaz.cardinal.event;

import in.twizmwaz.cardinal.module.proximity.Proximal;
import in.twizmwaz.cardinal.module.proximity.Proximity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class ProximityEvent extends Event implements Cancellable {

  private final Proximal proximal;
  private final Proximity proximity;

  private boolean cancelled;

}
