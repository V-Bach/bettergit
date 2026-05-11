package gitv.cli;

public class Ansi {
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    
    // Foreground colors
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String GRAY = "\u001B[90m";

    public static String color(String text, String colorCode) {
        return colorCode + text + RESET;
    }

    public static String bold(String text) {
        return BOLD + text + RESET;
    }

    public static String colorBold(String text, String colorCode) {
        return BOLD + colorCode + text + RESET;
    }
}
