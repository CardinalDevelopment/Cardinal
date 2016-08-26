/*
 * Copyright (c) 2016, Kevin Phoenix
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package in.twizmwaz.cardinal;

import com.google.common.collect.Lists;
import ee.ellytr.chat.LocaleRegistry;
import ee.ellytr.command.CommandExecutor;
import ee.ellytr.command.CommandRegistry;
import ee.ellytr.command.ProviderRegistry;
import ee.ellytr.command.exception.CommandException;
import in.twizmwaz.cardinal.command.CommandCardinal;
import in.twizmwaz.cardinal.command.CommandCycle;
import in.twizmwaz.cardinal.command.CommandJoin;
import in.twizmwaz.cardinal.command.CommandNext;
import in.twizmwaz.cardinal.command.CommandStart;
import in.twizmwaz.cardinal.command.provider.LoadedMapProvider;
import in.twizmwaz.cardinal.command.provider.TeamProvider;
import in.twizmwaz.cardinal.event.matchthread.MatchThreadMakeEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.match.MatchThread;
import in.twizmwaz.cardinal.module.Module;
import in.twizmwaz.cardinal.module.ModuleHandler;
import in.twizmwaz.cardinal.module.ModuleLoader;
import in.twizmwaz.cardinal.module.ModuleRegistry;
import in.twizmwaz.cardinal.module.event.ModuleLoadCompleteEvent;
import in.twizmwaz.cardinal.module.repository.LoadedMap;
import in.twizmwaz.cardinal.module.team.Team;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@Getter
public final class Cardinal extends JavaPlugin {

  @Getter
  private static Cardinal instance;

  private ModuleLoader moduleLoader;
  @Setter(AccessLevel.PRIVATE)
  private ModuleHandler moduleHandler;
  private List<MatchThread> matchThreads;
  private CommandRegistry commandRegistry;
  private CommandExecutor commandExecutor;

  /**
   * Creates a new Cardinal object.
   */
  public Cardinal() {
    if (instance != null) {
      throw new IllegalStateException("The Cardinal object has already been created.");
    }
    instance = this;
    matchThreads = Lists.newArrayList();
    MatchThread matchThread = new MatchThread();
    matchThreads.add(matchThread);
    Bukkit.getPluginManager().callEvent(new MatchThreadMakeEvent(matchThread));

    registerCommands();
    registerLocales();
  }

  @Override
  public void onEnable() {
    Validate.notNull(Cardinal.getInstance());
    commandRegistry.register();
    if (!getDataFolder().exists()) {
      getDataFolder().mkdir();
    }
    moduleLoader = new ModuleLoader();
    try {
      moduleLoader.findEntries(getFile());
    } catch (IOException ex) {
      getLogger().severe("A fatal exception occurred while trying to load internal modules.");
      ex.printStackTrace();
      setEnabled(false);
      return;
    }
    Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
          ModuleRegistry registry =
              new ModuleRegistry(moduleLoader.makeModules(moduleLoader.getModuleEntries()));
          setModuleHandler(new ModuleHandler(registry));
          Bukkit.getPluginManager().callEvent(new ModuleLoadCompleteEvent(moduleHandler));
        }
    );
    this.getLogger().info("Cardinal has loaded");
  }

  @Override
  public void onDisable() {

  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    try {
      commandExecutor.execute(command.getName(), sender, args);
    } catch (CommandException ex) {
      ex.printStackTrace();
    }
    return true;
  }

  @NonNull
  public static Logger getPluginLogger() {
    return Cardinal.getInstance().getLogger();
  }

  public static <T extends Module> T getModule(@NonNull Class<T> clazz) {
    return instance.moduleHandler.getRegistry().getModule(clazz);
  }

  public static void registerEvents(Listener listener) {
    Bukkit.getPluginManager().registerEvents(listener, getInstance());
  }

  private void registerCommands() {
    commandRegistry = new CommandRegistry(this);
    commandRegistry.addClass(CommandCardinal.class);
    commandRegistry.addClass(CommandCycle.class);
    commandRegistry.addClass(CommandJoin.class);
    commandRegistry.addClass(CommandNext.class);
    commandRegistry.addClass(CommandStart.class);

    ProviderRegistry providerRegistry = commandRegistry.getProviderRegistry();
    providerRegistry.registerProvider(Team.class, new TeamProvider());
    providerRegistry.registerProvider(LoadedMap.class, new LoadedMapProvider());

    commandRegistry.register();

    commandExecutor = new CommandExecutor(commandRegistry.getFactory());
  }

  /**
   * @param who A command sender.
   * @return The match thread that contains this player.
   */
  @NonNull
  public static MatchThread getMatchThread(@NonNull CommandSender who) {
    if (!(who instanceof Player)) {
      return getInstance().getMatchThreads().get(0);
    }

    Player player = (Player) who;
    for (MatchThread matchThread : getInstance().getMatchThreads()) {
      if (matchThread.hasPlayer(player)) {
        return matchThread;
      }
    }
    return null;
  }

  /**
   * @param match The match.
   * @return The match thread that is running this match.
   */
  public static MatchThread getMatchThread(@NonNull Match match) {
    for (MatchThread matchThread : getInstance().getMatchThreads()) {
      if (matchThread.getCurrentMatch().equals(match)) {
        return matchThread;
      }
    }
    return null;
  }

  /**
   * @param player The player.
   * @return The match that the player is in.
   */
  @NonNull
  public static Match getMatch(@NonNull Player player) {
    MatchThread matchThread = getMatchThread(player);
    if (matchThread == null) {
      return null;
    }
    return matchThread.getCurrentMatch();
  }

  /**
   * @param world The world.
   * @return The match that uses that world.
   */
  @NonNull
  public static Match getMatch(@NonNull World world) {
    for (MatchThread matchThread : getInstance().getMatchThreads()) {
      if (matchThread.getCurrentMatch().getWorld().equals(world)) {
        return matchThread.getCurrentMatch();
      }
    }
    return null;
  }

  private void registerLocales() {
    LocaleRegistry registry = new LocaleRegistry();
    registry.addLocaleFile(new Locale("en", "US"), getResource("lang/cardinal/en_US.properties"));
    registry.register();
  }

}
