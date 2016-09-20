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

package in.twizmwaz.cardinal.module.team;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.id.IdModule;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.ParseUtil;
import in.twizmwaz.cardinal.util.Strings;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jdom2.Element;
import org.jdom2.located.Located;

import java.util.List;

@ModuleEntry(depends = {IdModule.class})
public class TeamModule extends AbstractModule {

  @Override
  public boolean loadMatch(Match match) {
    Element element = match.getMap().getDocument().getRootElement().getChild("teams");
    if (element != null) {
      element.getChildren().forEach(child -> {
        Located located = (Located) child;
        String name = child.getText();

        String id = child.getAttributeValue("id");
        if (id == null) {
          id = name;
        }

        String colorRaw = child.getAttributeValue("color");
        ChatColor color;
        if (colorRaw == null) {
          Located locatedElement = (Located) child;
          String[] errorMessage = new String[]{"Color missing for " + name,
              "Element at " + located.getLine() + ", " + located.getColumn()};
          errors.add(new ModuleError(this, match.getMap(), errorMessage, false));
          color = ChatColor.WHITE;
        } else {
          color = ChatColor.valueOf(colorRaw.replace(" ", "_").replace("-", "_").toUpperCase());
        }

        String overheadRaw = child.getAttributeValue("overhead-color");
        ChatColor overHeadColor;
        if (overheadRaw == null) {
          overHeadColor = color;
        } else {
          overHeadColor = ChatColor.valueOf(child.getAttributeValue("overhead-color").replace(" ", "_").toUpperCase());
        }

        boolean plural = Numbers.parseBoolean(ParseUtil.getFirstAttribute("plural", child, element));

        String nameTagRaw = ParseUtil.getFirstAttribute("show-name-tags", child, element);
        NameTagVisibility showNameTags = NameTagVisibility.TRUE;
        if (nameTagRaw != null) {
          NameTagVisibility.valueOf(nameTagRaw);
        }

        int min = Numbers.parseInteger(ParseUtil.getFirstAttribute("min", child, element));

        int max = Numbers.parseInteger(ParseUtil.getFirstAttribute("max", child, element));

        int macOverfill = Numbers.parseInteger(ParseUtil.getFirstAttribute("max-overfill", child, element));
        if (macOverfill == 0) {
          macOverfill = Math.round(max * 1.25f);
        }
        Team team = new Team(id, color, overHeadColor, plural, showNameTags, min, max, macOverfill, name);
        if (!IdModule.get().add(match, id, team)) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{"Team id is not valid or already in use",
                  "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          IdModule.get().add(match, null, team, true);
        }
      });
    }
    match.getPlayerContainers().addAll(IdModule.get().getList(match, Team.class));
    return true;
  }

  /**
   * @param id The input ID.
   * @return The team that has the input ID.
   */
  public Team getTeamById(@NonNull Match match, @NonNull String id) {
    return IdModule.get().get(match, id, Team.class, false);
  }

  /**
   * @param name The input name.
   * @return The team that ha the input name.
   */
  public Team getTeamByName(@NonNull Match match, @NonNull String name) {
    for (Team team : getTeams(match)) {
      if (Strings.getSimplifiedName(team.getName()).startsWith(Strings.getSimplifiedName(name))) {
        return team;
      }
    }
    return null;
  }

  /**
   * @param player The input player.
   * @return The team which the player is on.
   */
  public Team getTeamByPlayer(@NonNull Match match, @NonNull Player player) {
    for (Team team : getTeams(match)) {
      if (team.getPlayers().contains(player)) {
        return team;
      }
    }
    return null;
  }

  /**
   * Returns the the team in a match.
   *
   * @param match The match that contains the teams.
   * @return The teams from this match.
   */
  public List<Team> getTeams(@NonNull Match match) {
    return IdModule.get().getList(match, Team.class);
  }

}
