package in.twizmwaz.cardinal.module.countdown;

import com.google.common.collect.Maps;
import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.chat.component.builder.UnlocalizedComponentBuilder;
import ee.ellytr.chat.component.formattable.UnlocalizedComponent;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.MatchState;
import in.twizmwaz.cardinal.match.MatchThread;
import in.twizmwaz.cardinal.module.cycle.CycleModule;
import in.twizmwaz.cardinal.module.repository.LoadedMap;
import in.twizmwaz.cardinal.module.rotation.RotationModule;
import in.twizmwaz.cardinal.util.Channels;
import in.twizmwaz.cardinal.util.Components;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.Map;

@Getter
public class CycleCountdown implements Cancellable, Runnable {

  private final Map<Player, BossBar> bossBars = Maps.newHashMap();

  @Setter
  private MatchState originalState;
  private boolean cancelled = true;
  private int time, originalTime;

  @Override
  public void run() {
    if (time == 0) {
      cancelled = true;

      CycleModule cycleModule = Cardinal.getModule(CycleModule.class);
      MatchThread matchThread = Cardinal.getInstance().getMatchThread();
      LoadedMap map = cycleModule.getNextMap(matchThread);
      cycleModule.cycle(matchThread);
      Channels.getGlobalChannel().sendMessage(new LocalizedComponentBuilder(
          ChatConstant.getConstant("cycle.cycled"),
          new UnlocalizedComponentBuilder(map.getName()).color(ChatColor.AQUA).build())
          .color(ChatColor.DARK_AQUA).build());
    } else if (!cancelled) {
      Channels.getGlobalChannel().sendMessage(new LocalizedComponentBuilder(
          ChatConstant.getConstant("cycle.cycling"),
          new UnlocalizedComponentBuilder(Cardinal.getModule(CycleModule.class)
              .getNextMap(Cardinal.getInstance().getMatchThread()).getName()).color(ChatColor.AQUA).build(),
          Components.getTimeComponentBuilder(time).color(ChatColor.DARK_RED).build()
      ).color(ChatColor.DARK_AQUA).build());
      time --;
      Bukkit.getScheduler().runTaskLaterAsynchronously(Cardinal.getInstance(), this, 20);
    }
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
    if (!cancelled) {
      Bukkit.getScheduler().runTaskAsynchronously(Cardinal.getInstance(), this);
    }
  }

  public void setTime(int time) {
    this.time = time;
    originalTime = time;
  }

}
