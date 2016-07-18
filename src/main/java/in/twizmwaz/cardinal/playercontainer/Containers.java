package in.twizmwaz.cardinal.playercontainer;

import in.twizmwaz.cardinal.event.player.PlayerContainerChangeStateEvent;
import in.twizmwaz.cardinal.module.team.Team;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Containers {

  public static void handleStateChangeEvent(Player player, PlayerContainerData oldData, PlayerContainerData newData) {
    PlayerContainerChangeStateEvent event = new PlayerContainerChangeStateEvent(player, oldData, newData);
    Bukkit.getPluginManager().callEvent(event);
    if (!event.isCancelled()) {
      if (isNonNullAndHasPlayer(oldData.getMatchThread(), player)) {
        oldData.getMatchThread().removePlayer(player);
      }
      if (isNonNullAndHasPlayer(oldData.getMatch(), player)) {
        oldData.getMatch().removePlayer(player);
      }
      if (isNonNullAndHasPlayer(oldData.getPlaying(), player) && oldData.getPlaying() instanceof Team) {
        oldData.getPlaying().removePlayer(player);
      }

      if (newData.getMatchThread() != null) {
        newData.getMatchThread().addPlayer(player);
      }
      if (newData.getMatch() != null) {
        newData.getMatch().addPlayer(player);
      }
      if (newData.getPlaying() != null && newData.getPlaying() instanceof Team) {
        newData.getPlaying().addPlayer(player);
      }
    }
  }

  private static boolean isNonNullAndHasPlayer(PlayerContainer container, Player player) {
    return container != null && container.hasPlayer(player);
  }

}
