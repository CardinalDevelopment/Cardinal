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

package in.twizmwaz.cardinal.module.objective.wool;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.objective.ProximityMetric;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.RegionException;
import in.twizmwaz.cardinal.module.region.RegionModule;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.module.team.TeamModule;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.ParseUtil;
import in.twizmwaz.cardinal.util.Strings;
import lombok.NonNull;
import org.bukkit.DyeColor;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.located.Located;

import java.util.List;
import java.util.Map;

@ModuleEntry
public class WoolModule extends AbstractModule {

  private Map<Match, List<Wool>> wools = Maps.newHashMap();

  public WoolModule() {
    this.depends = new Class[]{TeamModule.class, RegionModule.class};
  }

  @Override
  public boolean loadMatch(Match match) {
    List<Wool> wools = Lists.newArrayList();
    Document document = match.getMap().getDocument();
    for (Element woolsElement : document.getRootElement().getChildren("wools")) {
      for (Element woolElement : woolsElement.getChildren("wool")) {
        Located located = (Located) woolElement;
        String colorValue = ParseUtil.getFirstAttribute("color", woolElement, woolsElement);
        String id = ParseUtil.getFirstAttribute("id", woolElement, woolsElement);
        if (id == null) {
          id = colorValue;
        }

        String requiredValue = ParseUtil.getFirstAttribute("required", woolElement, woolsElement);
        boolean required = requiredValue == null || Numbers.parseBoolean(requiredValue);

        String teamValue = ParseUtil.getFirstAttribute("team", woolElement, woolsElement);
        if (teamValue == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"No team specified for wool",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }
        Team team = Cardinal.getModule(TeamModule.class).getTeamById(match, teamValue);
        if (team == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"Invalid team specified for wool",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }

        if (colorValue == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"No color specified for wool",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }
        DyeColor color;
        try {
          color = DyeColor.valueOf(Strings.getTechnicalName(colorValue));
        } catch (IllegalArgumentException e) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"Invalid color specified for wool",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }

        RegionModule regionModule = Cardinal.getModule(RegionModule.class);
        Region monument;
        try {
          monument = regionModule.getRegion(match, woolElement, "monument");
          if (monument == null) {
            monument = regionModule.getRegion(match, woolsElement, "monument");
          }
        } catch (RegionException e) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{RegionModule.getRegionError(e, "monument", "wool"),
                  "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }
        if (monument == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"Invalid monument specified for wool",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }

        String craftableValue = ParseUtil.getFirstAttribute("craftable", woolElement, woolsElement);
        boolean craftable = craftableValue != null && Numbers.parseBoolean(craftableValue);

        String showValue = ParseUtil.getFirstAttribute("show", woolElement, woolsElement);
        boolean show = showValue == null || Numbers.parseBoolean(showValue);

        String locationValue = ParseUtil.getFirstAttribute("location", woolElement, woolsElement);
        if (locationValue == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"No location specified for wool",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }
        String[] coordinates = locationValue.split(",");
        if (coordinates.length != 3) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"Invalid location format specified for wool",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }
        Vector location;
        try {
          location = new Vector(Double.parseDouble(coordinates[0].trim()),
              Double.parseDouble(coordinates[1].trim()),
              Double.parseDouble(coordinates[2].trim()));
        } catch (NumberFormatException e) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"Invalid location specified for wool",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }

        ProximityMetric woolProximityMetric = ProximityMetric.CLOSEST_KILL;
        String woolProximityMetricValue = ParseUtil.getFirstAttribute("woolproximity-metric",
            woolElement, woolsElement);
        if (woolProximityMetricValue != null) {
          try {
            woolProximityMetric = ProximityMetric.valueOf(Strings.getTechnicalName(woolProximityMetricValue));
          } catch (IllegalArgumentException e) {
            errors.add(new ModuleError(this, match.getMap(),
                new String[]{"Invalid wool proximity metric specified for wool",
                    "Element at " + located.getLine() + ", " + located.getColumn()}, false));
            continue;
          }
        }

        String woolProximityHorizontalValue =
            ParseUtil.getFirstAttribute("woolproximity-horizontal", woolElement, woolsElement);
        boolean woolProximityHorizontal = woolProximityHorizontalValue != null
            && Numbers.parseBoolean(woolProximityHorizontalValue);

        ProximityMetric monumentProximityMetric = ProximityMetric.CLOSEST_BLOCK;
        String monumentProximityMetricValue =
            ParseUtil.getFirstAttribute("monumentproximity-metric", woolElement, woolsElement);
        if (monumentProximityMetricValue != null) {
          try {
            monumentProximityMetric = ProximityMetric.valueOf(
                Strings.getTechnicalName(monumentProximityMetricValue));
          } catch (IllegalArgumentException e) {
            errors.add(new ModuleError(this, match.getMap(),
                new String[]{"Invalid monument proximity metric specified for wool",
                    "Element at " + located.getLine() + ", " + located.getColumn()}, false));
            continue;
          }
        }

        String monumentProximityHorizontalValue =
            ParseUtil.getFirstAttribute("monumentproximity-horizontal", woolElement, woolsElement);
        boolean monumentProximityHorizontal = monumentProximityHorizontalValue != null
            && Numbers.parseBoolean(monumentProximityHorizontalValue);

        Wool wool = new Wool(match, id, required, team, color, monument, craftable, show, location, woolProximityMetric,
            woolProximityHorizontal, monumentProximityMetric, monumentProximityHorizontal);
        Cardinal.registerEvents(wool);
        wools.add(wool);
      }
    }
    this.wools.put(match, wools);
    return true;
  }

  @Override
  public void clearMatch(Match match) {
    wools.get(match).forEach(HandlerList::unregisterAll);
    wools.remove(match);
  }

  public List<Wool> getWools(@NonNull Match match) {
    return wools.get(match);
  }

}
