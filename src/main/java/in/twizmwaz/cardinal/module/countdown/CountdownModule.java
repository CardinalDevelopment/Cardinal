package in.twizmwaz.cardinal.module.countdown;

import com.google.common.collect.Maps;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.MatchThread;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.event.ModuleLoadCompleteEvent;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

@ModuleEntry
public class CountdownModule extends AbstractModule implements Listener {

  private Map<MatchThread, CycleCountdown> cycleCountdowns = Maps.newHashMap();

  public CountdownModule() {
    Cardinal.registerEvents(this);
  }

  @EventHandler
  public void onModuleLoadComplete(ModuleLoadCompleteEvent event) {
    cycleCountdowns.put(Cardinal.getInstance().getMatchThread(), new CycleCountdown());
  }

  public CycleCountdown getCycleCountdown(@NonNull MatchThread matchThread) {
    return cycleCountdowns.get(matchThread);
  }

}
