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

package in.twizmwaz.cardinal.module.contributor;

import com.google.common.collect.Sets;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import lombok.NonNull;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.UUID;

@ModuleEntry
public class ContributorModule extends AbstractModule {

  private final Set<IdentifiedContributor> identified = Sets.newHashSet();
  private final Set<NamedContributor> named = Sets.newHashSet();

  /**
   * Finds or create a contributor for a given {@link String}, useful for groups or other non-players.
   *
   * @param name The name of the contributor.
   * @return The contributor.
   */
  public Contributor forName(@NonNull String name) {
    for (Contributor contributor : identified) {
      if (contributor.getName().equalsIgnoreCase(name)) {
        return contributor;
      }
    }
    for (Contributor contributor : named) {
      if (contributor.getName().equalsIgnoreCase(name)) {
        return contributor;
      }
    }
    NamedContributor contributor = new NamedContributor(name);
    named.add(contributor);
    return contributor;
  }

  /**
   * Finds or creates a contributor for a given {@link UUID}, useful for players.
   *
   * @param uuid The UUID of the contributor.
   * @return The contributor.
   */
  public Contributor forUuid(@NonNull UUID uuid) {
    for (IdentifiedContributor contributor : identified) {
      if (contributor.getUuid().equals(uuid)) {
        return contributor;
      }
    }
    IdentifiedContributor contributor = new IdentifiedContributor(uuid);
    Bukkit.getScheduler().runTaskAsynchronously(Cardinal.getInstance(), new ContributorNamer(contributor));
    identified.add(contributor);
    return contributor;
  }

}
