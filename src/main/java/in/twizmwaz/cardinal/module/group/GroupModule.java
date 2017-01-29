package in.twizmwaz.cardinal.module.group;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.event.player.PlayerChangeGroupEvent;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.group.groups.PlayerGroup;
import in.twizmwaz.cardinal.module.group.groups.GroupData;
import in.twizmwaz.cardinal.module.team.SinglePlayerGroup;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@ModuleEntry
public class GroupModule extends AbstractModule implements Listener {

  public GroupModule() {
    Cardinal.registerEvents(this);
  }

  /**
   * Handles a player changing groups if the called event was not cancelled.
   *
   * @param event The event that is called when a player is changing groups.
   */
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

  /**
   * Change a player's group.
   *
   * <p>A check in this function prevents redundancy when changing a player between two groups that are the same;
   * for example, if a player switches teams within a match, this function will not completely run for the groups
   * representing the match thread, as well as the match.</p>
   *
   * @param from The group on which the player was previously.
   * @param to The group to which the player is changing.
   * @param player The player that is changing groups.
   */
  private static void changeGroup(PlayerGroup from, PlayerGroup to, Player player) {
    if (from != to) {
      if (from != null && from.hasPlayer(player)) {
        from.removePlayer(player);
      }
      addPlayer(to, player);
    }
  }

  /**
   * Add a player to a generic group.
   *
   * @param group The group to which the {@param player} will be added.
   * @param player The player that will be added to the {@param group}.
   */
  private static void addPlayer(@NonNull PlayerGroup group, @NonNull Player player) {
    if (!(group instanceof SinglePlayerGroup)) {
      group.addPlayer(player);
    }
  }

}
