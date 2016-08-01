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

package in.twizmwaz.cardinal.module.objective.destroyable;

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
import in.twizmwaz.cardinal.util.MaterialPattern;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.ParseUtil;
import in.twizmwaz.cardinal.util.Strings;
import lombok.NonNull;
import org.bukkit.event.HandlerList;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.located.Located;

import java.util.List;
import java.util.Map;

@ModuleEntry(depends = {TeamModule.class, RegionModule.class})
public class DestroyableModule extends AbstractModule {

  private Map<Match, List<Destroyable>> destroyables = Maps.newHashMap();

  @Override
  public boolean loadMatch(Match match) {
    Document document = match.getMap().getDocument();
    List<Destroyable> destroyables = Lists.newArrayList();
    for (Element destroyablesElement : document.getRootElement().getChildren("destroyables")) {
      for (Element destroyableElement : destroyablesElement.getChildren("destroyable")) {
        Located located = (Located) destroyableElement;
        String id = ParseUtil.getFirstAttribute("id", destroyableElement, destroyablesElement);

        String name = ParseUtil.getFirstAttribute("name", destroyableElement, destroyablesElement);
        if (name == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"No name specified for destroyable",
              "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }

        String requiredValue = ParseUtil.getFirstAttribute("required", destroyableElement, destroyablesElement);
        boolean required = requiredValue == null || Numbers.parseBoolean(requiredValue);

        RegionModule regionModule = Cardinal.getModule(RegionModule.class);
        Region region;
        try {
          region = regionModule.getRegion(match, destroyableElement);
        } catch (RegionException e) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{RegionModule.getRegionError(e, "region", "destroyable"),
                  "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }
        if (region == null && destroyablesElement.getAttribute("region") != null) {
          region = regionModule.getRegionById(match, destroyablesElement.getAttributeValue("region"));
        }
        if (region == null) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{"No region specified for destroyable",
                  "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }
        if (!region.isBounded()) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{"Region specified for destroyable must be a bounded region",
                  "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }

        MaterialPattern materials = new MaterialPattern();
        String materialsValue = ParseUtil.getFirstAttribute("materials", destroyableElement,
            destroyablesElement);
        if (materialsValue != null) {
          materials = MaterialPattern.getMaterialPattern(materialsValue);
        }

        String ownerValue = ParseUtil.getFirstAttribute("owner", destroyableElement, destroyablesElement);
        if (ownerValue == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"No owner specified for destroyable"}, false));
          continue;
        }
        Team owner = Cardinal.getModule(TeamModule.class).getTeamById(match, ownerValue);
        if (owner == null) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{"Invalid owner specified for destroyable",
                  "Element at " + located.getLine() + ", " + located.getColumn()}, false));
          continue;
        }

        double completion = 100;
        String completionValue = ParseUtil.getFirstAttribute("completion", destroyableElement, destroyablesElement);
        if (completionValue != null) {
          completion = Numbers.parseDouble(completionValue.replaceAll("%", ""));
        }

        String modeChangesValue = ParseUtil.getFirstAttribute("mode-changes", destroyableElement, destroyablesElement);
        boolean modeChanges = modeChangesValue != null && Numbers.parseBoolean(modeChangesValue);

        String showProgressValue = ParseUtil.getFirstAttribute("show-progress", destroyableElement,
            destroyablesElement);
        boolean showProgress = showProgressValue != null && Numbers.parseBoolean(showProgressValue);

        String repairableValue = ParseUtil.getFirstAttribute("repairable", destroyableElement, destroyablesElement);
        boolean repairable = repairableValue == null || Numbers.parseBoolean(repairableValue);

        String sparksValue = ParseUtil.getFirstAttribute("sparks", destroyableElement, destroyablesElement);
        boolean sparks = sparksValue != null && Numbers.parseBoolean(sparksValue);

        String showValue = ParseUtil.getFirstAttribute("show", destroyableElement, destroyablesElement);
        boolean show = showValue == null || Numbers.parseBoolean(showValue);

        ProximityMetric proximityMetric = ProximityMetric.CLOSEST_PLAYER;
        String woolProximityMetricValue = ParseUtil.getFirstAttribute("proximity-metric", destroyableElement,
            destroyablesElement);
        if (woolProximityMetricValue != null) {
          try {
            proximityMetric =
                ProximityMetric.valueOf(Strings.getTechnicalName(woolProximityMetricValue));
          } catch (IllegalArgumentException e) {
            errors.add(new ModuleError(this, match.getMap(),
                new String[]{"Invalid proximity metric specified for destroyable",
                    "Element at " + located.getLine() + ", " + located.getColumn()}, false));
            continue;
          }
        }

        String proximityHorizontalValue = ParseUtil.getFirstAttribute("proximity-horizontal", destroyableElement,
            destroyablesElement);
        boolean proximityHorizontal = proximityHorizontalValue != null
            && Numbers.parseBoolean(proximityHorizontalValue);

        Destroyable destroyable = new Destroyable(match, id, name, required, region, materials, owner,
            completion, modeChanges, showProgress, repairable, sparks, show, proximityMetric, proximityHorizontal);
        Cardinal.registerEvents(destroyable);
        destroyables.add(destroyable);
      }
    }
    this.destroyables.put(match, destroyables);
    return true;
  }

  @Override
  public void clearMatch(Match match) {
    List<Destroyable> destroyables = this.destroyables.get(match);
    destroyables.forEach(HandlerList::unregisterAll);
    destroyables.clear();
    this.destroyables.remove(match);
  }

  public List<Destroyable> getDestroyables(@NonNull Match match) {
    return destroyables.get(match);
  }

}
