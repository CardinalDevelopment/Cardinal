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

import in.twizmwaz.cardinal.module.region.RegionException;
import in.twizmwaz.cardinal.module.region.exception.RegionAttributeException;
import in.twizmwaz.cardinal.module.region.exception.RegionPropertyException;
import in.twizmwaz.cardinal.module.region.exception.attribute.InvalidRegionAttributeException;
import in.twizmwaz.cardinal.module.region.exception.attribute.MissingRegionAttributeException;
import in.twizmwaz.cardinal.module.region.exception.property.InvalidRegionPropertyException;
import in.twizmwaz.cardinal.module.region.exception.property.MissingRegionPropertyException;
import lombok.NonNull;
import org.jdom2.Element;

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
   * Gets an appropriate error message for a failed region parsing.
   *
   * @param e      The exception thrown when parsing.
   * @param name   The name of the expected region.
   * @param parent The module that attempted to retrieve the region.
   * @return The error message.
   */
  public static String getRegionError(RegionException e, String name, String parent) {
    if (e instanceof RegionAttributeException) {
      RegionAttributeException exception = (RegionAttributeException) e;
      if (exception instanceof MissingRegionAttributeException) {
        return "Missing attribute \"" + exception.getAttribute() + "\" for " + name + " for " + parent;
      } else if (exception instanceof InvalidRegionAttributeException) {
        return "Invalid attribute \"" + exception.getAttribute() + "\" for " + name + " for " + parent;
      }
    } else if (e instanceof RegionPropertyException) {
      RegionPropertyException exception = (RegionPropertyException) e;
      if (exception instanceof MissingRegionPropertyException) {
        return "Missing property \"" + exception.getProperty() + "\" for " + name + " for " + parent;
      } else if (exception instanceof InvalidRegionPropertyException) {
        return "Invalid property \"" + exception.getProperty() + "\" for " + name + " for " + parent;
      }
    }
    return "Could not parse " + name + " for " + parent;
  }

}
