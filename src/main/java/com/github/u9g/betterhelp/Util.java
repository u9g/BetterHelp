package com.github.u9g.betterhelp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Util {
    public static Component colorizeString (String input) {
        return MiniMessage.miniMessage().deserialize(Constants.COLORIZE_USAGE.matcher(input.replace("<", "|||abc|||")
                .replace(">", "<colors_2>><colors_1>")
                .replace("|||abc|||", "<colors_2><<colors_1>")
                // order matters, keep those three above in that order
                .replaceAll("/", "<colors_1>/<colors_1>") // / help 15
                .replace("\n", " <colors_2>|<colors_1> ")).replaceAll("<colors_2>$1<colors_1>"), Constants.colorResolver);
    }

    // ensure slash before command starts, ie: " enchant" -> " /enchant"
    public static String ensureSlashInUsage(String input) {
        StringBuilder s = new StringBuilder();
        boolean pastStart = false;
        for (char ch : input.toCharArray()) {
            if (!pastStart && ch != ' ' && ch != '/') {
                s.append("/");
                pastStart = true;
            } else if (!pastStart && ch == '/') {
                pastStart = true;
            }
            s.append(ch);
        }
        return s.toString();
    }

    // separate slashes and command to separate color
    public static Component getColoredSlashCommandName(String commandName, TextColor slashColor, TextColor cmdColor) {
        int numSlashes = 1;
        boolean haveAllSlashes = false;
        StringBuilder cmd = new StringBuilder();
        for (var ch : commandName.toCharArray()) {
            if (ch == '/') numSlashes++;
            else if (!haveAllSlashes) {
                haveAllSlashes = true;
                cmd.append(ch);
            } else {
                cmd.append(ch);
            }
        }
        return Component.text("/".repeat(numSlashes), slashColor).append(Component.text(cmd.toString(), cmdColor));
    }
}
