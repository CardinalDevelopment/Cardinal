package in.twizmwaz.cardinal.module.group;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.event.player.PlayerChangeGroupEvent;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.group.groups.PlayerGroup;
import in.twizmwaz.cardinal.module.group.groups.GroupData;
import in.twizmwaz.cardinal.module.team.SinglePlayerGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@ModuleEntry
public class GroupModule extends AbstractModule implements Listener {

  public GroupModule() {
    Cardinal.registerEvents(this);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerGroupChange(PlayerChangeGroupEvent event) {
    GroupData from = event.getFrom();
    GroupData to = event.getTo();
    Player player = event.getPlayer();

    changeGroup(from.getMatchThread(), to.getMatchThread(), player);
    changeGroup(from.getMatch(), to.getMatch(), player);
    if (from.getMatch() == to.getMatch()) {
      changeGroup(from.getGroup(), to.getGroup(), player);
    } else {
      // If match is not the same, removing the player from the match already kicked the player from the team.
      addPlayer(to.getGroup(), player);
    }
  }

  private static void changeGroup(PlayerGroup from, PlayerGroup to, Player player) {
    if (from != to) {
      if (from != null && from.hasPlayer(player)) {
        from.removePlayer(player);
      }
      addPlayer(to, player);
    }
  }

  private static void addPlayer(PlayerGroup newContainer, Player player) {
    if (newContainer != null && !(newContainer instanceof SinglePlayerGroup)) {
      newContainer.addPlayer(player);
    }
  }

}
