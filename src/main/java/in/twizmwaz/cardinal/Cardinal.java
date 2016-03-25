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

import ee.ellytr.chat.LocaleRegistry;
import ee.ellytr.command.CommandExecutor;
import ee.ellytr.command.CommandRegistry;
import ee.ellytr.command.exception.CommandException;
import in.twizmwaz.cardinal.command.CommandCardinal;
import in.twizmwaz.cardinal.command.CommandCycle;
import in.twizmwaz.cardinal.match.MatchThread;
import in.twizmwaz.cardinal.module.Module;
import in.twizmwaz.cardinal.module.ModuleHandler;
import in.twizmwaz.cardinal.module.ModuleLoader;
import in.twizmwaz.cardinal.module.ModuleRegistry;
import in.twizmwaz.cardinal.module.event.ModuleLoadCompleteEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Logger;

public final class Cardinal extends JavaPlugin {

  @Getter
  private static Cardinal instance;
  @Getter
  private ModuleLoader moduleLoader;
  @Getter
  @Setter(AccessLevel.PRIVATE)
  private ModuleHandler moduleHandler;
  @Getter
  private MatchThread matchThread;
  @Getter
  private CommandRegistry commandRegistry;
  @Getter
  private CommandExecutor commandExecutor;

  /**
   * Creates a new Cardinal object.
   */
  public Cardinal() {
    if (instance != null) {
      throw new IllegalStateException("The Cardinal object has already been created.");
    }
    instance = this;
    this.matchThread = new MatchThread();

    commandRegistry = new CommandRegistry(this);
    commandRegistry.addClass(CommandCardinal.class);
    commandRegistry.addClass(CommandCycle.class);
    commandExecutor = new CommandExecutor(commandRegistry.getFactory());

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

  private void registerLocales() {
    LocaleRegistry registry = new LocaleRegistry();
    registry.addLocaleFile("en", getResource("lang/cardinal/en.properties"));
    registry.register();
  }

}
