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

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.event.match.MatchModuleLoadCompleteEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.AbstractModule;
import in.twizmwaz.cardinal.module.ModuleEntry;
import in.twizmwaz.cardinal.module.ModuleError;
import in.twizmwaz.cardinal.module.filter.exception.FilterPropertyException;
import in.twizmwaz.cardinal.module.filter.exception.property.InvalidFilterPropertyException;
import in.twizmwaz.cardinal.module.filter.exception.property.MissingFilterChildException;
import in.twizmwaz.cardinal.module.filter.exception.property.MissingFilterPropertyException;
import in.twizmwaz.cardinal.module.filter.parser.CauseFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.ChildFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.ChildrenFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.EntityFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.ItemFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.LayerFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.MaterialFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.RandomFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.RangeFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.SpawnFilterParser;
import in.twizmwaz.cardinal.module.filter.parser.TeamFilterParser;
import in.twizmwaz.cardinal.module.filter.type.CanFlyFilter;
import in.twizmwaz.cardinal.module.filter.type.CarryingFilter;
import in.twizmwaz.cardinal.module.filter.type.CauseFilter;
import in.twizmwaz.cardinal.module.filter.type.CreatureFilter;
import in.twizmwaz.cardinal.module.filter.type.CrouchingFilter;
import in.twizmwaz.cardinal.module.filter.type.EntityFilter;
import in.twizmwaz.cardinal.module.filter.type.FlyingFilter;
import in.twizmwaz.cardinal.module.filter.type.HoldingFilter;
import in.twizmwaz.cardinal.module.filter.type.LayerFilter;
import in.twizmwaz.cardinal.module.filter.type.MaterialFilter;
import in.twizmwaz.cardinal.module.filter.type.MonsterFilter;
import in.twizmwaz.cardinal.module.filter.type.ObjectiveFilter;
import in.twizmwaz.cardinal.module.filter.type.RandomFilter;
import in.twizmwaz.cardinal.module.filter.type.SameTeamFilter;
import in.twizmwaz.cardinal.module.filter.type.SpawnFilter;
import in.twizmwaz.cardinal.module.filter.type.SprintingFilter;
import in.twizmwaz.cardinal.module.filter.type.StaticFilter;
import in.twizmwaz.cardinal.module.filter.type.TeamFilter;
import in.twizmwaz.cardinal.module.filter.type.VoidFilter;
import in.twizmwaz.cardinal.module.filter.type.WalkingFilter;
import in.twizmwaz.cardinal.module.filter.type.WearingFilter;
import in.twizmwaz.cardinal.module.filter.type.modifiers.AllFilter;
import in.twizmwaz.cardinal.module.filter.type.modifiers.AllowFilter;
import in.twizmwaz.cardinal.module.filter.type.modifiers.AnyFilter;
import in.twizmwaz.cardinal.module.filter.type.modifiers.DenyFilter;
import in.twizmwaz.cardinal.module.filter.type.modifiers.NotFilter;
import in.twizmwaz.cardinal.module.filter.type.modifiers.OneFilter;
import in.twizmwaz.cardinal.module.filter.type.modifiers.RangeFilter;
import in.twizmwaz.cardinal.module.id.IdModule;
import in.twizmwaz.cardinal.module.region.RegionModule;
import in.twizmwaz.cardinal.module.team.TeamModule;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jdom2.Element;
import org.jdom2.located.Located;

import java.util.Collection;

@ModuleEntry(depends = {IdModule.class, RegionModule.class, TeamModule.class})
public class FilterModule extends AbstractModule implements Listener {

  //Static filters. Can be shared across matches, because they use no arguments
  public static final Filter ALLOW = new StaticFilter(FilterState.ALLOW);
  public static final Filter ABSTAIN = new StaticFilter(FilterState.ABSTAIN);
  public static final Filter DENY = new StaticFilter(FilterState.DENY);

  public static final Filter CREATURE = new CreatureFilter();
  public static final Filter MONSTER = new MonsterFilter();

  public static final Filter CROUCHING = new CrouchingFilter();
  public static final Filter WALKING = new WalkingFilter();
  public static final Filter SPRINTING = new SprintingFilter();
  public static final Filter FLYING = new FlyingFilter();
  public static final Filter CAN_FLY = new CanFlyFilter();

  public static final Filter VOID = new VoidFilter();

  public FilterModule() {
    Cardinal.registerEvents(this);
  }

  @Override
  public boolean loadMatch(@NonNull Match match) {
    IdModule.get().add(match, "always", ALLOW);
    IdModule.get().add(match, "deny", DENY);
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

  /**
   * Runs load() on filters that implement the LoadLateFilter interface.
   */
  @EventHandler
  public void onModuleLoad(MatchModuleLoadCompleteEvent event) {
    loadFilters(event.getMatch(), IdModule.get().getList(event.getMatch(), Filter.class));
  }

  /**
   * Runs load() on filters that implement the LoadLateFilter interface.
   */
  public void loadFilters(Match match, Collection<Filter> filters) {
    filters.stream().filter(filter -> filter instanceof LoadLateFilter).forEach(filter -> {
      try {
        ((LoadLateFilter) filter).load(match);
      } catch (FilterException e) {
        errors.add(new ModuleError(this, match.getMap(), new String[]{getFilterError(e, "filter", null)}, false));
      }
    });
  }

  public Filter getFilter(@NonNull Match match, @NonNull String id) {
    return IdModule.get().get(match, id, Filter.class);
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
      /* Static filters */
      case "always": {
        return checkFilter(match, id, ALLOW);
      }
      case "never": {
        return checkFilter(match, id, DENY);
      }
      /* Filter modifiers */
      case "not": {
        ChildFilterParser parser = new ChildFilterParser(this, match, element);
        return checkFilter(match, id, new NotFilter(parser.getChild()));
      }
      case "allow": {
        ChildFilterParser parser = new ChildFilterParser(this, match, element);
        return checkFilter(match, id, new AllowFilter(parser.getChild()));
      }
      case "deny": {
        ChildFilterParser parser = new ChildFilterParser(this, match, element);
        return checkFilter(match, id, new DenyFilter(parser.getChild()));
      }
      /* Filter combinations */
      case "one": {
        ChildrenFilterParser parser = new ChildrenFilterParser(this, match, element);
        return checkFilter(match, id, new OneFilter(parser.getChildren()));
      }
      case "all": {
        ChildrenFilterParser parser = new ChildrenFilterParser(this, match, element);
        return checkFilter(match, id, new AllFilter(parser.getChildren()));
      }
      case "any": {
        ChildrenFilterParser parser = new ChildrenFilterParser(this, match, element);
        return checkFilter(match, id, new AnyFilter(parser.getChildren()));
      }
      case "range": {
        RangeFilterParser parser = new RangeFilterParser(this, match, element);
        return checkFilter(match, id, new RangeFilter(parser.getChildren(), parser.getMin(), parser.getMax()));
      }
      /* Regular filters */
      case "team": {
        TeamFilterParser parser = new TeamFilterParser(match, element);
        return checkFilter(match, id, new TeamFilter(parser.getTeam()));
      }
      case "material": {
        MaterialFilterParser parser = new MaterialFilterParser(element);
        return checkFilter(match, id, new MaterialFilter(parser.getPattern()));
      }
      case "spawn": {
        SpawnFilterParser parser = new SpawnFilterParser(element);
        return checkFilter(match, id, new SpawnFilter(parser.getSpawnReason()));
      }
      case "mob": {
        return checkFilter(match, id, MONSTER);
      }
      case "monster": {
        return checkFilter(match, id, MONSTER);
      }
      case "creature": {
        return checkFilter(match, id, CREATURE);
      }
      case "entity": {
        EntityFilterParser parser = new EntityFilterParser(element);
        return checkFilter(match, id, new EntityFilter(parser.getEntityType()));
      }
      case "kill-streak":
        //TODO: Track killstreaks
        return checkFilter(match, id, ABSTAIN);
      case "class":
        //TODO: Support classes
        return checkFilter(match, id, ABSTAIN);
      case "random": {
        RandomFilterParser parser = new RandomFilterParser(element);
        return checkFilter(match, id, new RandomFilter(parser));
      }
      case "crouching": {
        return checkFilter(match, id, CROUCHING);
      }
      case "walking": {
        return checkFilter(match, id, WALKING);
      }
      case "sprinting": {
        return checkFilter(match, id, SPRINTING);
      }
      case "flying": {
        return checkFilter(match, id, FLYING);
      }
      case "can-fly": {
        return checkFilter(match, id, CAN_FLY);
      }
      case "objective": {
        return checkFilter(match, id, new ObjectiveFilter(element));
      }
      //Fixme: missing flag filters
      case "flag-carried": {
        return checkFilter(match, id, ABSTAIN);
      }
      case "flag-dropped": {
        return checkFilter(match, id, ABSTAIN);
      }
      case "flag-returned": {
        return checkFilter(match, id, ABSTAIN);
      }
      case "flag-captured": {
        return checkFilter(match, id, ABSTAIN);
      }
      case "carrying-flag": {
        return checkFilter(match, id, ABSTAIN);
      }
      case "cause": {
        CauseFilterParser parser = new CauseFilterParser(element);
        return checkFilter(match, id, new CauseFilter(parser.getCause()));
      }
      case "carrying": {
        ItemFilterParser parser = new ItemFilterParser(element);
        return checkFilter(match, id, new CarryingFilter(parser.getItem()));
      }
      case "holding": {
        ItemFilterParser parser = new ItemFilterParser(element);
        return checkFilter(match, id, new HoldingFilter(parser.getItem()));
      }
      case "wearing": {
        ItemFilterParser parser = new ItemFilterParser(element);
        return checkFilter(match, id, new WearingFilter(parser.getItem()));
      }
      case "same-team": {
        ChildFilterParser parser = new ChildFilterParser(this, match, element);
        return checkFilter(match, id, new SameTeamFilter(parser.getChild()));
      }
      case "void": {
        return checkFilter(match, id, VOID);
      }
      case "layer": {
        LayerFilterParser parser = new LayerFilterParser(element);
        return checkFilter(match, id, new LayerFilter(parser.getLayer(), parser.getCoordinate()));
      }
      /* Region filter */
      case "region": {
        return checkFilter(match, id, Cardinal.getModule(RegionModule.class).getRegionById(match, id));
      }
      default:
        for (String alternateAttribute : alternateAttributes) {
          String filterValue = element.getAttributeValue(alternateAttribute);
          if (filterValue != null) {
            Filter filter = getFilter(match, filterValue);
            if (filter != null) {
              return checkFilter(match, id, filter);
            }
          }
        }

        String filterValue = element.getAttributeValue("id");
        if (filterValue != null) {
          Filter filter = getFilter(match, filterValue);
          if (filter != null) {
            return checkFilter(match, id, filter);
          }
        }
    }
    return null;
  }

  private Filter checkFilter(Match match, String id, Filter filter) throws FilterException {
    if (id != null) {
      if (!IdModule.get().add(match, id, filter)) {
        //Fixme: needs descriptive exception
        throw new FilterException();
      }
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
      Located located = (Located) exception.getElement();
      if (exception instanceof MissingFilterPropertyException) {
        return "Missing property \"" + exception.getProperty() + "\"" + (name != null ? " for " + name : "")
            + (parent != null ? " for " + parent : "") + " at " + located.getLine() + ", " + located.getColumn();
      } else if (exception instanceof InvalidFilterPropertyException) {
        return "Invalid property \"" + exception.getProperty() + "\"" + (name != null ? " for " + name : "")
            + (parent != null ? " for " + parent : "") + " at " + located.getLine() + ", " + located.getColumn();
      }  else if (exception instanceof MissingFilterChildException) {
        return "Missing child \"" + exception.getProperty() + "\"" + (name != null ? " for " + name : "")
            + (parent != null ? " for " + parent : "") + " at " + located.getLine() + ", " + located.getColumn();
      }
    }
    return "Could not parse " + name + (parent != null ? " for " + parent : "");
  }

}
