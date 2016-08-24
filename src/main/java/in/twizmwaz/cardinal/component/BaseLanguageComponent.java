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

import ee.ellytr.chat.component.LanguageComponent;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class BaseLanguageComponent<C extends BaseComponent> extends LanguageComponent {

  /**
   * Adds regular {@link BaseComponent} fields to a duplicate component.
   * @param component The BaseComponent to add values to.
   * @return The BaseComponent with the values.
   */
  public C duplicate(C component) {
    component.setColor(getColor());
    component.setBold(isBold());
    component.setItalic(isItalic());
    component.setUnderlined(isUnderlined());
    component.setStrikethrough(isStrikethrough());
    component.setObfuscated(isObfuscated());
    component.setClickEvent(getClickEvent());
    component.setHoverEvent(getHoverEvent());
    if (getExtra() != null) {
      for (BaseComponent extra : getExtra()) {
        component.addExtra(extra.duplicate());
      }
    }
    return component;
  }

}
