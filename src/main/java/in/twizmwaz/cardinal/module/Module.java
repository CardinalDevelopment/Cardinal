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

import in.twizmwaz.cardinal.match.Match;
import lombok.NonNull;

import java.util.Collection;

/**
 * Represents a Cardinal module. A Cardinal2 module must be able to to load and execute multiple
 * maps simultaneously.
 */
public interface Module {

  /**
   * @return The classes of modules to load matches before this module.
   */
  Class[] getDepends();

  /**
   * @return The classes of modules to load matches after this module.
   */
  Class[] getLoadBefore();

  /**
   * Clears match information from a module.
   *
   * @param match Match to be cleared from the module.
   */
  void clearMatch(Match match);

  /**
   * Instructs the module to load information for the match.
   *
   * @param match Match for the XML document to load from.
   * @return Returns true if the module loaded without interruption. Returns false for a
   *         match-blocking failure.
   */
  boolean loadMatch(Match match);

  /**
   * @return Errors, if any, generated when loading the current map.
   */
  @NonNull
  Collection<ModuleError> getErrors();

}
