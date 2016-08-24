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

package in.twizmwaz.cardinal.component.team;

import com.google.common.collect.Lists;
import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.formattable.LocalizedComponent;
import ee.ellytr.chat.util.ChatUtil;
import in.twizmwaz.cardinal.component.BaseLanguageComponent;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.util.Strings;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.List;
import java.util.Locale;

@Getter
@Setter
public class TeamComponent extends BaseLanguageComponent<TeamComponent> {

  private Team team;
  private boolean hover;

  public TeamComponent(Team team) {
    this.team = team;
    hover = true;
  }

  @Override
  public TeamComponent duplicate() {
    TeamComponent component = new TeamComponent(team);
    super.duplicate(component);
    component.setHover(isHover());
    return component;
  }

  @Override
  public BaseComponent[] getComponents(Locale locale) {
    List<BaseComponent> components = Lists.newArrayList();
    setColor(team.getColor());
    components.add(ChatUtil.getTextComponent(team.getName(), this));
    if (hover) {
      for (BaseComponent component : components) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
            new LocalizedComponent(ChatConstant.getConstant("team.hover.join"),
                new TeamComponentBuilder(team).hover(false).build()).getComponents(locale)));
        component.setClickEvent(
            new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + Strings.getFirstWord(team.getName())));
      }
    }
    return components.toArray(new BaseComponent[components.size()]);
  }

}
