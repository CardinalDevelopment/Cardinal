package in.twizmwaz.cardinal.command;

import com.google.common.base.Strings;
import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.chat.component.builder.UnlocalizedComponentBuilder;
import ee.ellytr.chat.component.formattable.LocalizedComponent;
import ee.ellytr.chat.component.formattable.UnlocalizedComponent;
import ee.ellytr.command.CommandContext;
import ee.ellytr.command.command.Command;
import ee.ellytr.command.command.PlayerCommand;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.MatchThread;
import in.twizmwaz.cardinal.module.cycle.CycleModule;
import in.twizmwaz.cardinal.module.repository.LoadedMap;
import in.twizmwaz.cardinal.module.repository.RepositoryModule;
import in.twizmwaz.cardinal.util.Channels;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandSetNext {

  @Command(aliases = {"setnext", "sn"}, description = "Sets the next map.")
  @PlayerCommand
  public static void setNext(CommandContext cmd) {
    //TODO: This one line will be most of the method once MultiArgs are implemented.
    //Cardinal.getModule(CycleModule.class).getNextCycle().get(Cardinal.getInstance().getMatchThread()).setMap(map);

    String input = StringUtils.join(cmd.getArgs(), ' ').toLowerCase();
    Map<String, LoadedMap> maps = Cardinal.getModule(RepositoryModule.class).getLoadedMaps();
    Set<String> mapNames = maps.keySet();
    List<String> candidates = mapNames.stream().filter(m -> m.toLowerCase().startsWith(input))
        .collect(Collectors.toList());
    if (candidates.size() > 0) {
      String map = candidates.get(0);
      if (Strings.isNullOrEmpty(map)) {
        Channels.getPlayerChannel((Player) cmd.getSender()).sendMessage(
            new LocalizedComponentBuilder(ChatConstant.getConstant("cycle.set.notFound")).color(ChatColor.RED).build());
      } else {
        MatchThread thread = Cardinal.getMatch((Player) cmd.getSender()).getThread();
        Cardinal.getModule(CycleModule.class).getNextCycle(thread).setMap(maps.get(map));
        BaseComponent mapComponent = new UnlocalizedComponentBuilder(maps.get(map).getName())
            .color(ChatColor.GOLD).build();
        Channels.getPlayerChannel((Player) cmd.getSender()).sendMessage(new LocalizedComponentBuilder(
            ChatConstant.getConstant("cycle.set.success"), mapComponent).color(ChatColor.DARK_PURPLE).build());
      }
    } else {
      Channels.getPlayerChannel((Player) cmd.getSender()).sendMessage(
          new LocalizedComponentBuilder(ChatConstant.getConstant("cycle.set.notFound")).color(ChatColor.RED).build());
    }

  }

}
