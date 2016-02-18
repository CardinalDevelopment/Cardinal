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
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.objective.ProximityMetric;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.module.team.TeamModule;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.ParseUtil;
import in.twizmwaz.cardinal.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class WoolModule extends AbstractModule {

  private List<Wool> wools;

  public WoolModule() {
    super("wool");
    wools = Lists.newArrayList();
  }

  @Override
  public boolean loadMatch(Document document) {
    for (Element woolsElement : document.getRootElement().getChildren("wools")) {
      for (Element woolElement : woolsElement.getChildren("wool")) {
        String id = ParseUtil.getFirstAttribute("id", woolElement, woolsElement);

        String requiredValue = ParseUtil.getFirstAttribute("required", woolElement, woolsElement);
        boolean required = requiredValue == null || Numbers.parseBoolean(requiredValue);

        String teamValue = ParseUtil.getFirstAttribute("team", woolElement, woolsElement);
        if (teamValue == null) {
          errors.add(new ModuleError(this, new String[]{"No team specified for wool"}, false));
          continue;
        }
        Team team = new TeamModule().getTeamById(teamValue); //TODO: Get TeamModule from match
        if (team == null) {
          errors.add(new ModuleError(this, new String[]{"Invalid team specified for wool"},
                  false));
          continue;
        }

        String colorValue = ParseUtil.getFirstAttribute("color", woolElement, woolsElement);
        if (colorValue == null) {
          errors.add(new ModuleError(this, new String[]{"No color specified for wool"}, false));
          continue;
        }
        DyeColor color;
        try {
          color = DyeColor.valueOf(colorValue);
        } catch (IllegalArgumentException e) {
          errors.add(new ModuleError(this, new String[]{"Invalid color specified for wool"},
                  false));
          continue;
        }

        String monumentValue = ParseUtil.getFirstAttribute("monument", woolElement, woolsElement);
        Region monument = null; //TODO: Get region from id
        if (monument == null) {
          errors.add(new ModuleError(this, new String[]{"Invalid monument specified for wool"},
                  false));
          continue;
        }

        String craftableValue = ParseUtil.getFirstAttribute("craftable", woolElement,
                woolsElement);
        boolean craftable = craftableValue != null && Numbers.parseBoolean(craftableValue);

        String showValue = ParseUtil.getFirstAttribute("show", woolElement, woolsElement);
        boolean show = showValue == null || Numbers.parseBoolean(showValue);

        String locationValue = ParseUtil.getFirstAttribute("location", woolElement, woolsElement);
        String[] coordinates = locationValue.split(",");
        if (coordinates.length != 3) {
          errors.add(new ModuleError(this,
                  new String[]{"Invalid location format specified for wool"}, false));
          continue;
        }
        Vector location;
        try {
          location = new Vector(Double.parseDouble(coordinates[0].trim()),
                  Double.parseDouble(coordinates[1].trim()),
                  Double.parseDouble(coordinates[2].trim()));
        } catch (NumberFormatException e) {
          errors.add(new ModuleError(this, new String[]{"Invalid location specified for wool"},
                  false));
          continue;
        }

        ProximityMetric woolProximityMetric = ProximityMetric.CLOSEST_KILL;
        String woolProximityMetricValue = ParseUtil.getFirstAttribute("woolproximity-metric",
                woolElement, woolsElement);
        if (woolProximityMetricValue != null) {
          try {
            woolProximityMetric =
                    ProximityMetric.valueOf(Strings.getTechnicalName(woolProximityMetricValue));
          } catch (IllegalArgumentException e) {
            errors.add(new ModuleError(this,
                    new String[]{"Invalid wool proximity metric specified for wool"}, false));
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
            errors.add(new ModuleError(this,
                    new String[]{"Invalid monument proximity metric specified for wool"}, false));
            continue;
          }
        }

        String monumentProximityHorizontalValue =
                ParseUtil.getFirstAttribute("monumentproximity-horizontal", woolElement,
                        woolsElement);
        boolean monumentProximityHorizontal = monumentProximityHorizontalValue != null
                && Numbers.parseBoolean(monumentProximityHorizontalValue);

        Wool wool = new Wool(id, required, team, color, monument, craftable, show, location,
                woolProximityMetric, woolProximityHorizontal, monumentProximityMetric,
                monumentProximityHorizontal);
        Bukkit.getPluginManager().registerEvents(wool, Cardinal.getInstance());
        wools.add(wool);
      }
    }
    return true;
  }

  @Override
  public void clearMatch() {
    wools.forEach(HandlerList::unregisterAll);
    wools.clear();
  }

}
