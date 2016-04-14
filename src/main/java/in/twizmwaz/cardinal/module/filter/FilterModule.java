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

package in.twizmwaz.cardinal.module.filter;

import com.google.common.collect.Maps;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.filter.exception.FilterPropertyException;
import in.twizmwaz.cardinal.module.filter.exception.property.InvalidFilterPropertyException;
import in.twizmwaz.cardinal.module.filter.exception.property.MissingFilterPropertyException;
import in.twizmwaz.cardinal.module.filter.parser.EntityFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.MaterialFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.ObjectiveFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.RandomFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.SpawnFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.TeamFilterParser;
import in.twizmwaz.cardinal.module.filter.type.CanFlyFilter;
import in.twizmwaz.cardinal.module.filter.type.CrouchingFilter;
import in.twizmwaz.cardinal.module.filter.type.FlyingFilter;
import in.twizmwaz.cardinal.module.filter.type.ObjectiveFilter;
import in.twizmwaz.cardinal.module.filter.type.RandomFilter;
import in.twizmwaz.cardinal.module.filter.type.SingletonFilter;
import in.twizmwaz.cardinal.module.filter.type.SprintingFilter;
import in.twizmwaz.cardinal.module.filter.type.TeamFilter;
import in.twizmwaz.cardinal.module.filter.type.WalkingFilter;
import in.twizmwaz.cardinal.module.objective.core.CoreModule;
import in.twizmwaz.cardinal.module.objective.destroyable.DestroyableModule;
import in.twizmwaz.cardinal.module.objective.wool.WoolModule;
import in.twizmwaz.cardinal.module.team.TeamModule;
import lombok.NonNull;
import org.jdom2.Element;

import java.util.Map;

public class FilterModule extends AbstractModule {

  private Map<Match, Map<String, Filter>> filters = Maps.newHashMap();

  public FilterModule() {
    depends = new Class[]{CoreModule.class, DestroyableModule.class, TeamModule.class, WoolModule.class};
  }

  @Override
  public boolean loadMatch(@NonNull Match match) {
    for (Element filtersElement : match.getMap().getDocument().getRootElement().getChildren("filters")) {
      for (Element filterElement : filtersElement.getChildren()) {
        try {
          getFilter(match, filterElement);
        } catch (FilterException e) {
          errors.add(new ModuleError(this, match.getMap(), new String[]{getFilterError(e, "filter", null)}, false));
        }
      }
    }
    return true;
  }

  @Override
  public void clearMatch(@NonNull Match match) {
    filters.remove(match);
  }

  public Filter getFilterById(@NonNull String id) {
    return filters.get(Cardinal.getInstance().getMatchThread().getCurrentMatch()).get(id);
  }

  /**
   * Parses an element for a filter.
   *
   * @param match               The match for this filter to be included in.
   * @param element             The element.
   * @param alternateAttributes Any alternate attributes that contain the reference to a filter.
   * @return The filter.
   * @throws FilterException Thrown if the filter cannot be parsed due to missing or invalid information.
   */
  public Filter getFilter(Match match, Element element, String... alternateAttributes) throws FilterException {
    String id = element.getAttributeValue("id");
    switch (element.getName()) {
      case "team": {
        TeamFilterParser parser = new TeamFilterParser(element);
        return checkFilter(match, id, new TeamFilter(parser.getTeam()));
      }
      case "material": {
        MaterialFilterParser parser = new MaterialFilterParser(element);
        return checkFilter(match, id, new SingletonFilter<>(parser.getPattern()));
      }
      case "spawn": {
        SpawnFilterParser parser = new SpawnFilterParser(element);
        return checkFilter(match, id, new SingletonFilter<>(parser.getSpawnReason()));
      }
      case "mob": {
        //TODO
        return null;
      }
      case "entity": {
        EntityFilterParser parser = new EntityFilterParser(element);
        return checkFilter(match, id, new SingletonFilter<>(parser.getEntityType()));
      }
      case "kill-streak":
        //TODO: Track killstreaks
        return null;
      case "class":
        //TODO: Support classes
        return null;
      case "random": {
        RandomFilterParser parser = new RandomFilterParser(element);
        return checkFilter(match, id, new RandomFilter(parser));
      }
      case "crouching": {
        return checkFilter(match, id, new CrouchingFilter());
      }
      case "walking": {
        return checkFilter(match, id, new WalkingFilter());
      }
      case "sprinting": {
        return checkFilter(match, id, new SprintingFilter());
      }
      case "flying": {
        return checkFilter(match, id, new FlyingFilter());
      }
      case "can-fly": {
        return checkFilter(match, id, new CanFlyFilter());
      }
      case "objective": {
        ObjectiveFilterParser parser = new ObjectiveFilterParser(element);
        return checkFilter(match, id, new ObjectiveFilter(parser.getObjective()));
      }
      case "carrying": {
        //TODO
        return null;
      }
      case "holding": {
        //TODO
        return null;
      }
      case "wearing": {
        //TODO
        return null;
      }
      default:
        for (String alternateAttribute : alternateAttributes) {
          String filterValue = element.getAttributeValue(alternateAttribute);
          if (filterValue != null) {
            Filter filter = getFilterById(filterValue);
            if (filter != null) {
              return checkFilter(match, id, filter);
            }
          }
        }

        String filterValue = element.getAttributeValue("id");
        if (filterValue != null) {
          Filter filter = getFilterById(filterValue);
          if (filter != null) {
            return checkFilter(match, id, filter);
          }
        }
    }
    return null;
  }

  private Filter checkFilter(Match match, String id, Filter filter) {
    if (id != null) {
      filters.get(match).put(id, filter);
    }
    return filter;
  }

  /**
   * Gets an appropriate error message for a failed filter parsing.
   *
   * @param e      The exception thrown when parsing.
   * @param name   The name of the expected filter.
   * @param parent The module that attempted to retrieve the filter.
   * @return The error message.
   */
  public static String getFilterError(FilterException e, String name, String parent) {
    if (e instanceof FilterPropertyException) {
      FilterPropertyException exception = (FilterPropertyException) e;
      if (exception instanceof MissingFilterPropertyException) {
        return "Missing property \"" + exception.getProperty() + "\"" + (name != null ? " for " + name : "")
            + (parent != null ? " for " + parent : "");
      } else if (exception instanceof InvalidFilterPropertyException) {
        return "Invalid property \"" + exception.getProperty() + "\"" + (name != null ? " for " + name : "")
            + (parent != null ? " for " + parent : "");
      }
    }
    return "Could not parse " + name + (parent != null ? " for " + parent : "");
  }

}
