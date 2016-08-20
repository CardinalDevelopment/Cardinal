package in.twizmwaz.cardinal.event.player;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
public class CardinalDeathEvent extends PlayerEvent {

  @Getter private static final HandlerList handlerList = new HandlerList();

  private final Player killer;

  public CardinalDeathEvent(@NonNull Player player, Player killer) {
    super(player);

    this.killer = killer;
  }

  @Override
  public HandlerList getHandlers() {
    return handlerList;
  }

}
