package in.twizmwaz.cardinal.util;

public class Strings {

  public static String getTechnicalName(String in) {
    return in.toUpperCase().replaceAll(" ", "_");
  }

  public static String getSimpleName(String in) {
    return in.toLowerCase().replaceAll("_", " ");
  }

}
