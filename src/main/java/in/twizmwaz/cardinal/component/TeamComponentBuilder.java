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

package in.twizmwaz.cardinal.component;

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.module.team.Team;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.List;

public class TeamComponentBuilder {

  private Team team;
  private ChatColor color;
  private boolean bold;
  private boolean italic;
  private boolean underlined;
  private boolean strikethrough;
  private boolean obfuscated;
  private ClickEvent clickEvent;
  private HoverEvent hoverEvent;
  private List<BaseComponent> extra;
  private boolean hover;

  /**
   * Creates a builder of {@link TeamComponent} based on specified values.
   *
   * @param team The team for this component.
   */
  public TeamComponentBuilder(Team team) {
    this.team = team;
    color = null;
    bold = false;
    italic = false;
    underlined = false;
    strikethrough = false;
    obfuscated = false;
    clickEvent = null;
    hoverEvent = null;
    extra = Lists.newArrayList();
    hover = true;
  }

  public TeamComponentBuilder color(ChatColor color) {
    this.color = color;
    return this;
  }

  public TeamComponentBuilder bold(boolean bold) {
    this.bold = bold;
    return this;
  }

  public TeamComponentBuilder italic(boolean italic) {
    this.italic = italic;
    return this;
  }

  public TeamComponentBuilder underlined(boolean underlined) {
    this.underlined = underlined;
    return this;
  }

  public TeamComponentBuilder strikethrough(boolean strikethrough) {
    this.strikethrough = strikethrough;
    return this;
  }

  public TeamComponentBuilder obfuscated(boolean obfuscated) {
    this.obfuscated = obfuscated;
    return this;
  }

  public TeamComponentBuilder clickEvent(ClickEvent clickEvent) {
    this.clickEvent = clickEvent;
    return this;
  }

  public TeamComponentBuilder hoverEvent(HoverEvent hoverEvent) {
    this.hoverEvent = hoverEvent;
    return this;
  }

  public TeamComponentBuilder extra(List<BaseComponent> extra) {
    this.extra = extra;
    return this;
  }

  public TeamComponentBuilder hover(boolean hover) {
    this.hover = hover;
    return this;
  }

  /**
   * Builds a {@link TeamComponent} from the specified values.
   *
   * @return The built component.
   */
  public TeamComponent build() {
    TeamComponent component = new TeamComponent(team);
    component.setColor(color);
    component.setBold(bold);
    component.setItalic(italic);
    component.setUnderlined(underlined);
    component.setStrikethrough(strikethrough);
    component.setObfuscated(obfuscated);
    component.setClickEvent(clickEvent);
    component.setHoverEvent(hoverEvent);
    component.setExtra(extra);
    component.setHover(hover);
    return component;
  }

}
