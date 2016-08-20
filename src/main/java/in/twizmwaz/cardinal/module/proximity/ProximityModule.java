package in.twizmwaz.cardinal.module.proximity;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.event.proximity.ProximityChangeEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.objective.core.Core;
import in.twizmwaz.cardinal.module.objective.core.CoreModule;
import in.twizmwaz.cardinal.module.objective.destroyable.Destroyable;
import in.twizmwaz.cardinal.module.objective.destroyable.DestroyableModule;
import in.twizmwaz.cardinal.module.objective.wool.Wool;
import in.twizmwaz.cardinal.module.objective.wool.WoolModule;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

@ModuleEntry
public class ProximityModule extends AbstractModule implements Listener {

  public ProximityModule() {
    Cardinal.registerEvents(this);
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerMove(PlayerMoveEvent event) {
    Match match = Cardinal.getMatch(event.getPlayer());

    Cardinal.getModule(CoreModule.class).getCores(match)
        .forEach(core -> handleCoreProximity(event, core));
    Cardinal.getModule(DestroyableModule.class).getDestroyables(match)
        .forEach(destroyable -> handleDestroyableProximity(event, destroyable));
    Cardinal.getModule(WoolModule.class).getWools(match)
        .forEach(wool -> handleWoolProximity(event, wool));
  }

  private void handleCoreProximity(@NonNull Event event, @NonNull Core core) {
    Proximity proximity = core.getCurrentProximity();
    if (proximity != null && proximity.getRule().getMetric().equals(ProximityMetric.CLOSEST_PLAYER)) {
      Location location;
      if (event instanceof PlayerMoveEvent) {
        location = ((PlayerMoveEvent) event).getPlayer().getLocation();
      } else {
        throw new IllegalArgumentException("Event cannot be used to update proximity");
      }

      double distance = location.distance(core.getRegion().getBounds().getCenter());
      if (distance < proximity.getProximity()) {
        ProximityChangeEvent proximityEvent = new ProximityChangeEvent(core, proximity, distance);
        Bukkit.getPluginManager().callEvent(proximityEvent);
        if (!proximityEvent.isCancelled()) {
          proximity.setProximity(proximityEvent.getNewProximity());
        }
      }
    }
  }

  private void handleDestroyableProximity(@NonNull Event event, @NonNull Destroyable destroyable) {
    Proximity proximity = destroyable.getCurrentProximity();
    if (proximity != null && proximity.getRule().getMetric().equals(ProximityMetric.CLOSEST_PLAYER)) {
      Location location;
      if (event instanceof PlayerMoveEvent) {
        location = ((PlayerMoveEvent) event).getPlayer().getLocation();
      } else {
        throw new IllegalArgumentException("Event cannot be used to update proximity");
      }

      double distance = location.distance(destroyable.getRegion().getBounds().getCenter());
      if (distance < proximity.getProximity()) {
        ProximityChangeEvent proximityEvent = new ProximityChangeEvent(destroyable, proximity, distance);
        Bukkit.getPluginManager().callEvent(proximityEvent);
        if (!proximityEvent.isCancelled()) {
          proximity.setProximity(proximityEvent.getNewProximity());
        }
      }
    }
  }

  private void handleWoolProximity(@NonNull Event event, @NonNull Wool wool) {
    Proximity proximity = wool.getCurrentProximity();
    if (proximity != null) {
      ProximityMetric metric;
      Location location;
      if (event instanceof PlayerMoveEvent) {
        metric = ProximityMetric.CLOSEST_PLAYER;
        location = ((PlayerMoveEvent) event).getPlayer().getLocation();
      } else {
        throw new IllegalArgumentException("Event cannot be used to update proximity");
      }

      if (proximity.getRule().getMetric().equals(metric)) {
        Proximity.Identifier proximityId = proximity.getId();
        Vector proximityLocation = null;
        if (proximityId.equals(Proximity.Identifier.BEFORE_TOUCH)) {
          proximityLocation = wool.getLocation();
        } else if (proximityId.equals(Proximity.Identifier.BEFORE_COMPLETE)) {
          proximityLocation = wool.getMonument().getBounds().getCenter();
        }
        double distance = location.distance(proximityLocation);
        if (distance < proximity.getProximity()) {
          ProximityChangeEvent proximityEvent = new ProximityChangeEvent(wool, proximity, distance);
          Bukkit.getPluginManager().callEvent(proximityEvent);
          if (!proximityEvent.isCancelled()) {
            proximity.setProximity(proximityEvent.getNewProximity());
          }
        }
      }
    }
  }

}
