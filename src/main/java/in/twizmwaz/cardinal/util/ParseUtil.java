package in.twizmwaz.cardinal.util;

import org.jdom2.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParseUtil {

  @Nullable
  public static String getFirstAttribute(@Nonnull String attribute, @Nonnull Element... elements) {
    for (Element element : elements) {
      String value = element.getAttributeValue(attribute);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

}
