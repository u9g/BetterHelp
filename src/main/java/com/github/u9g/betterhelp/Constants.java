package com.github.u9g.betterhelp;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;

import java.util.regex.Pattern;

public class Constants {
    public static final String BORDER_CHAR = "\u2550";
    public static final String LEFT_ARROW  = "\u25C1";
    public static final String RIGHT_ARROW = "\u25b7";
    public static final String FIRST_LINE_PREFIX  = "\u250C";
    public static final String LAST_LINE_PREFIX   = "\u2514";
    public static final String MIDDLE_LINE_PREFIX = "\u251C";
    public static final String PREFIX_HYPHEN = "\u2500";
    public static final Pattern COLORIZE_USAGE = Pattern.compile("([()\\[\\]|:])");
    public static final TextColor[] COLORS = new TextColor[]{
            TextColor.color(0x5B00FF),
            NamedTextColor.WHITE,
            TextColor.color(0xC028FF),
            NamedTextColor.GRAY,
            NamedTextColor.DARK_GRAY
    };
    public static final PlaceholderResolver colorResolver = PlaceholderResolver.placeholders(hex("colors_0", COLORS[0]), hex("colors_1", COLORS[1]), hex("colors_2", COLORS[2]), hex("colors_3", COLORS[3]), hex("colors_4", COLORS[4]));

    private static Placeholder<String> hex (String key, TextColor c) {
        return Placeholder.miniMessage(key, "<color:" + c.asHexString() + ">");
    }
}
