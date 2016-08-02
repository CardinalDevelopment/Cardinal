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

package in.twizmwaz.cardinal.module.filter.parser;

import in.twizmwaz.cardinal.module.filter.FilterException;
import in.twizmwaz.cardinal.module.filter.exception.property.MissingFilterChildException;
import in.twizmwaz.cardinal.util.document.DocumentItems;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jdom2.Element;

@Getter
public class ItemFilterParser {

  private final ItemStack item;

  /**
   * Parses an element for filter that hold an item, like Carrying, Holding or Wearing filters.
   *
   * @param element The element.
   * @throws FilterException Thrown if the block property is missing or invalid.
   */
  public ItemFilterParser(Element element) throws FilterException {
    if (element.getChild("item") ==  null) {
      //Fixme: needs descriptive exception
      throw new MissingFilterChildException("item", element);
    }
    item = DocumentItems.getItem(element.getChild("item"));
  }

}
