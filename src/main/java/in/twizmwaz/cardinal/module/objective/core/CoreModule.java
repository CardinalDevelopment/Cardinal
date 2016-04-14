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

package in.twizmwaz.cardinal.module.objective.core;

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
import in.twizmwaz.cardinal.module.region.type.BoundedRegion;
import in.twizmwaz.cardinal.module.region.type.bounded.BlockRegion;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.module.team.TeamModule;
import in.twizmwaz.cardinal.util.MaterialPattern;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.ParseUtil;
import in.twizmwaz.cardinal.util.Strings;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@ModuleEntry
public class CoreModule extends AbstractModule {

  private Map<Match, List<Core>> cores = Maps.newHashMap();

  public CoreModule() {
    this.depends = new Class[]{TeamModule.class, RegionModule.class};
  }

  @Override
  public boolean loadMatch(Match match) {
    List<Core> cores = Lists.newArrayList();
    Document document = match.getMap().getDocument();
    for (Element coresElement : document.getRootElement().getChildren("cores")) {
      for (Element coreElement : coresElement.getChildren("core")) {
        String id = ParseUtil.getFirstAttribute("id", coreElement, coresElement);

        String nameValue = ParseUtil.getFirstAttribute("name", coreElement, coresElement);
        String name = nameValue == null ? "Core" : nameValue;

        String requiredValue = ParseUtil.getFirstAttribute("required", coreElement, coresElement);
        boolean required = requiredValue == null || Numbers.parseBoolean(requiredValue);

        RegionModule regionModule = Cardinal.getModule(RegionModule.class);
        Region region;
        try {
          region = regionModule.getRegion(match, coreElement);
          if (region == null) {
            region = regionModule.getRegion(match, coresElement);
          }
        } catch (RegionException e) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{RegionModule.getRegionError(e, "region", "core")}, false));
          continue;
        }
        if (region == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"Invalid region specified for core"}, false));
          continue;
        }
        if (!(region instanceof BoundedRegion)) {
          errors.add(new ModuleError(this, match.getMap(),
              new String[]{"Region specified for wool must be a bounded region"}, false));
          continue;
        }
        BoundedRegion boundedRegion = (BoundedRegion) region;

        String leakValue = ParseUtil.getFirstAttribute("leak", coreElement, coresElement);
        int leak = 5;
        if (leakValue != null) {
          try {
            leak = Numbers.parseInteger(leakValue);
          } catch (NumberFormatException e) {
            errors.add(new ModuleError(this, match.getMap(),
                new String[]{"Invalid leak distance specified for core"}, false));
            continue;
          }
        }

        MaterialPattern material = new MaterialPattern(new AbstractMap.SimpleEntry<>(Material.OBSIDIAN,
            MaterialPattern.ANY_DATA_VALUE));
        String materialValue = ParseUtil.getFirstAttribute("material", coreElement, coresElement);
        if (materialValue != null) {
          try {
            material = MaterialPattern.getSingleMaterialPattern(materialValue);
          } catch (NumberFormatException e) {
            errors.add(new ModuleError(this, match.getMap(),
                new String[]{"Invalid data value of material specified for core"}, false));
            continue;
          }
        }
        String teamValue = ParseUtil.getFirstAttribute("team", coreElement, coresElement);
        if (teamValue == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"No team specified for wool"}, false));
          continue;
        }
        Team team = Cardinal.getModule(TeamModule.class).getTeamById(match, teamValue);
        if (team == null) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{"Invalid team specified for core"}, false));
          continue;
        }

        String modeChangesValue = ParseUtil.getFirstAttribute("mode-changes", coreElement, coresElement);
        boolean modeChanges = modeChangesValue == null || Numbers.parseBoolean(modeChangesValue);

        String showValue = ParseUtil.getFirstAttribute("show", coreElement, coresElement);
        boolean show = showValue == null || Numbers.parseBoolean(showValue);

        ProximityMetric proximityMetric = ProximityMetric.CLOSEST_PLAYER;
        String woolProximityMetricValue = ParseUtil.getFirstAttribute("proximity-metric", coreElement, coresElement);
        if (woolProximityMetricValue != null) {
          try {
            proximityMetric =
                ProximityMetric.valueOf(Strings.getTechnicalName(woolProximityMetricValue));
          } catch (IllegalArgumentException e) {
            errors.add(new ModuleError(this, match.getMap(),
                new String[]{"Invalid proximity metric specified for core"}, false));
            continue;
          }
        }

        String proximityHorizontalValue = ParseUtil.getFirstAttribute("proximity-horizontal", coreElement,
            coresElement);
        boolean proximityHorizontal = proximityHorizontalValue != null
            && Numbers.parseBoolean(proximityHorizontalValue);

        Core core = new Core(match, id, name, required, boundedRegion, leak, material, team, modeChanges, show,
            proximityMetric, proximityHorizontal);
        Cardinal.registerEvents(core);
        cores.add(core);
      }
    }
    this.cores.put(match, cores);
    return true;
  }

  @Override
  public void clearMatch(Match match) {
    List<Core> cores = this.cores.get(match);
    cores.forEach(HandlerList::unregisterAll);
    cores.clear();
    this.cores.remove(match);
  }

  /**
   * @param vector The vector that this method bases the closest core off of.
   * @return The core closest to the given vector.
   */
  public Core getClosestCore(Match match, Vector vector) {
    Core closestCore = null;
    double closestDistance = Double.POSITIVE_INFINITY;
    for (Core core : this.cores.get(match)) {
      BlockRegion center = core.getRegion().getCenterBlock();
      double distance = vector.distance(center.getVector());
      if (distance < closestDistance) {
        closestCore = core;
        closestDistance = distance;
      }
    }
    return closestCore;
  }

  public List<Core> getCores(@NonNull Match match) {
    return cores.get(match);
  }

}
