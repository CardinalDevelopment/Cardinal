package in.twizmwaz.cardinal.util;

public class Numbers {

  public static boolean parseBoolean(String in) {
    return in.equalsIgnoreCase("on") || in.equalsIgnoreCase("true");
  }

  public static double parseDouble(String in) {
    if (in.equalsIgnoreCase("oo")) {
      return Double.POSITIVE_INFINITY;
    }
    return Double.parseDouble(in);
  }

  public static int parseInteger(String in) {
    if (in.equalsIgnoreCase("oo")) {
      return Integer.MAX_VALUE;
    }
    return Integer.parseInt(in);
  }

}
