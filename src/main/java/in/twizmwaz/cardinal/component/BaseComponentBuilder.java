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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.List;

/**
 * This is a base for other component builders to use, it has all the {@link BaseComponent} fields.
 *
 * @param <B> The builder class, should always be the same as the class.
 * @param <C> The component output.
 */
public abstract class BaseComponentBuilder<B extends BaseComponentBuilder, C extends BaseComponent> {

  private ChatColor color;
  private boolean bold;
  private boolean italic;
  private boolean underlined;
  private boolean strikethrough;
  private boolean obfuscated;
  private ClickEvent clickEvent;
  private HoverEvent hoverEvent;
  private List<BaseComponent> extra;
  
  private B thisObject;

  /**
   * This is a base for other component builders to use, it has all the {@link BaseComponent} fields.
   */
  public BaseComponentBuilder() {
    color = null;
    bold = false;
    italic = false;
    underlined = false;
    strikethrough = false;
    obfuscated = false;
    clickEvent = null;
    hoverEvent = null;
    extra = Lists.newArrayList();
    thisObject = getThis();
  }

  /**
   * Must be implemented by all sub classes, should just be a "return this;".
   * @return The builder object.
   */
  public abstract B getThis();


  public B color(ChatColor color) {
    this.color = color;
    return thisObject;
  }

  public B bold(boolean bold) {
    this.bold = bold;
    return thisObject;
  }

  public B italic(boolean italic) {
    this.italic = italic;
    return thisObject;
  }

  public B underlined(boolean underlined) {
    this.underlined = underlined;
    return thisObject;
  }

  public B strikethrough(boolean strikethrough) {
    this.strikethrough = strikethrough;
    return thisObject;
  }

  public B obfuscated(boolean obfuscated) {
    this.obfuscated = obfuscated;
    return thisObject;
  }

  public B clickEvent(ClickEvent clickEvent) {
    this.clickEvent = clickEvent;
    return thisObject;
  }

  public B hoverEvent(HoverEvent hoverEvent) {
    this.hoverEvent = hoverEvent;
    return thisObject;
  }

  public B extra(List<BaseComponent> extra) {
    this.extra = extra;
    return thisObject;
  }

  /**
   * Builds a {@link C} from the specified values.
   *
   * @return The built component.
   */
  public C build(C component) {
    component.setColor(color);
    component.setBold(bold);
    component.setItalic(italic);
    component.setUnderlined(underlined);
    component.setStrikethrough(strikethrough);
    component.setObfuscated(obfuscated);
    component.setClickEvent(clickEvent);
    component.setHoverEvent(hoverEvent);
    component.setExtra(extra);
    return component;
  }

}
