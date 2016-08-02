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

package in.twizmwaz.cardinal.module;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.event.match.MatchModuleLoadCompleteEvent;
import in.twizmwaz.cardinal.match.Match;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Object to control modules.
 */
@RequiredArgsConstructor
public class ModuleHandler {

  @Getter
  private final ModuleRegistry registry;

  public void clearMatch(@NonNull Match match) {
    Validate.notNull(match);
    registry.getModules().entrySet().forEach(entry -> entry.getValue().clearMatch(match));
  }

  /**
   * @param match The match modules should load from
   * @return If the modules loaded successfully.
   */
  public boolean loadMatch(@NonNull Match match) {
    Validate.notNull(match);
    for (Module module : registry.getLoadOrder()) {
      Cardinal.getInstance().getLogger().info("Loading module \"" + module.getClass().getSimpleName() + "\"...");
      try {
        if (!module.loadMatch(match)) {
          Cardinal.getInstance().getLogger().warning("An error occurred when attempting to load "
              + module.getClass().getSimpleName() + " for " + match.getMap().getName());
          sendErrorMessages(match, module);
          return false;
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
    Bukkit.getPluginManager().callEvent(new MatchModuleLoadCompleteEvent(match));
    for (Module module : registry.getLoadOrder()) {
      sendErrorMessages(match, module);
    }
    Cardinal.getInstance().getLogger().info("Modules for " + match.getMap().getName() + " loaded successfully.");
    return true;
  }

  private void sendErrorMessages(Match match, Module module) {
    for (ModuleError moduleError : module.getErrors().stream().filter(error ->
        error.getMap().equals(match.getMap())).collect(Collectors.toList())) {
      Logger logger = Cardinal.getPluginLogger();
      logger.warning("Error loading module \"" + module.getClass().getSimpleName() + "\":");
      for (String message : moduleError.getMessage()) {
        logger.log(moduleError.isCritical() ? Level.SEVERE : Level.INFO, '\t' + message);
      }
    }
  }

}
