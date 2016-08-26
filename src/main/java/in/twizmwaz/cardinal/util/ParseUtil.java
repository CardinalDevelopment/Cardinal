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

package in.twizmwaz.cardinal.util;

import lombok.NonNull;
import org.jdom2.Element;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//fixme: I don't like this name
public class ParseUtil {

  /**
   * @param attribute The attribute name that is checked from the elements.
   * @param elements  The array of elements that are checked for the attribute.
   * @return The value of the first attribute in the elements.
   */
  public static String getFirstAttribute(@NonNull String attribute, @NonNull Element... elements) {
    for (Element element : elements) {
      String value = element.getAttributeValue(attribute);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  /**
   * @param element    The element to get attributes from.
   * @param attributes The attribute names, in order, to check.
   * @return The first non-null value, if it exists.
   */
  public static Map.Entry<String, String> getFirstNonNullAttributeValue(Element element, List<String> attributes) {
    for (String attr : attributes) {
      String value = element.getAttributeValue(attr);
      if (value != null) {
        return new AbstractMap.SimpleEntry<>(attr, value);
      }
    }
    return null;
  }

  /**
   * Adds an element on the front of an array of elements.
   * @param element  The element to add.
   * @param elements The element array.
   * @return A new array with the element and the elements.
   */
  public static Element[] addElement(Element element, Element... elements) {
    Element[] newElements = new Element[elements.length + 1];
    newElements[0] = element;
    System.arraycopy(elements, 0, newElements, 1, elements.length);
    return newElements;
  }

  /**
   * @param value    The value to test.
   * @param fallback The fallback if value is null.
   * @return Returns value as long as it isn't null, if it's null, it returns fallback.
   */
  public static <T> T fallback(T value, @NonNull T fallback) {
      return value == null ? fallback : value;
  }

  /** Utility to get a list of pre-processed elements, with the parent's attributes. Very useful in any module that has
   * a parent element, with sub elements.
   * @param parentElement The parent element to search on. Usually the document root element.
   * @param parent The parent element name. Ex: cores
   * @param child The child element name.   Ex: core
   * @return A list of child elements, containing all attributes from their parents, in importance order.
   */
  public static List<Element> getElementsIn(Element parentElement, String parent, String child) {
    List<Element> results = new ArrayList<>();
    for (Element element : parentElement.getChildren(parent)) {
      results.addAll(getElementsIn(parent, child, element));
    }
    return results;
  }

  private static List<Element> getElementsIn(String parent, String child, Element... elements) {
    List<Element> results = new ArrayList<>();

    elements[0].getChildren(child).forEach(childEl -> results.add(getJoinedElements(addElement(childEl, elements))));
    elements[0].getChildren(parent)
        .forEach(parentEl -> results.addAll(getElementsIn(parent, child, addElement(parentEl, elements))));

    return results;
  }

  private static Element getJoinedElements(Element... elements) {
    Element result = elements[0].clone();
    for (Element element : elements) {
      element.getAttributes().stream()
          .filter(attribute -> result.getAttribute(attribute.getName()) != null).map(result::setAttribute);
    }
    return result;
  }

}
