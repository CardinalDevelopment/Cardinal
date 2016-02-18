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

package in.twizmwaz.cardinal.module.cycle;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.Module;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.event.ModuleLoadCompleteEvent;
import in.twizmwaz.cardinal.module.rotation.RotationModule;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;
import org.bukkit.event.EventHandler;

import java.io.File;
import java.util.UUID;

public final class CycleModule extends AbstractModule {

  @Getter
  @Setter(AccessLevel.PACKAGE)
  private World matchWorld;
  @Getter
  @Setter(AccessLevel.PACKAGE)
  private File matchFile;

  @Getter
  private CycleRunnable cycle;

  public CycleModule() {
    super("cycle");
  }

  /**
   * Creates a new cycle object for the initial server cycle.
   *
   * @param event The event listened for.
   */
  @EventHandler
  public void onModuleLoad(ModuleLoadCompleteEvent event) {
    cycle = new CycleRunnable(this, UUID.randomUUID(),
        ((RotationModule) Cardinal.getModule("rotation")).getNext());
  }

  /**
   * Initiates the cycling process.
   *
   * @return If the cycle was successful.
   */
  public boolean cycle() {
    Cardinal.getInstance().getModuleHandler().clearMatch();
    cycle.run();
    return true;
  }

  @ModuleEntry("cycle")
  public static Module makeModule() {
    return new CycleModule();
  }

}
