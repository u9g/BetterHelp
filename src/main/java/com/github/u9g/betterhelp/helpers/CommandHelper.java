package com.github.u9g.betterhelp.helpers;
import com.github.u9g.betterhelp.Constants;
import com.github.u9g.betterhelp.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandHelper {
    public static String getPluginOrigin (Command command) {
        String pkg = command.getClass().getPackage().getName();
        String commandOrigin = "Unknown (package name: " + pkg + ")";
        if (command.getClass().getSimpleName().equals("VanillaCommandWrapper")) {
            commandOrigin = "Minecraft (Vanilla)";
        } else if (pkg.equals("org.bukkit.command.defaults") || (pkg.equals("org.bukkit.command") && !(command instanceof PluginIdentifiableCommand))) { // /help 10
            commandOrigin = "Bukkit";
        } else if (pkg.equals("org.spigotmc")) {
            commandOrigin = "Spigot";
        } else if (pkg.equals("com.destroystokyo.paper") || pkg.equals("co.aikar.timings")) { // /help 5
            commandOrigin = "Paper";
        } else if (command instanceof PluginIdentifiableCommand pic) {
            commandOrigin = pic.getPlugin().getName();
        }
        return commandOrigin;
    }

    public static List<Map.Entry<String, Command>> getCommandsFor (CommandSender cs) {
        return Bukkit
                .getCommandMap().getKnownCommands().entrySet()
                .stream().filter(c -> c.getValue().testPermissionSilent(cs) && !c.getKey().contains(":") && !c.getValue().getAliases().contains(c.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .toList();
    }

    public static List<Component> getCommandInfo(CommandSender cs, String commandName, Command fallbackInfo, TextColor[] colors) {
        HelpTopic ht = Bukkit.getHelpMap().getHelpTopic("/" + commandName); // need to add leading slash!
        if (ht == null) { // if there isn't a help topic, we emulate one
            String desc = fallbackInfo.getDescription();
            List<String> aliases = fallbackInfo.getAliases();
            return List.of(
                    Util.getColoredSlashCommandName(fallbackInfo.getName(), colors[2], colors[1]),
                    Component.text(desc.equals("") ? "None" : desc, colors[1]),
                    Component.text(aliases.size() != 0 ? String.join(", ", aliases) : "None", colors[1])
            );
        }
        String[] lines = StringUtils.split(ht.getFullText(cs), "\n");
        List<Component> components = new ArrayList<>();
        for (String _line : lines) {
            // if this is the first line of a help and doesn't have a ":", it's a description (probably)
            if (_line.equals(lines[0]) && !_line.contains(":") && !_line.equals("")) {
                _line = "Description: " + _line;
            }
            String line = ChatColor.stripColor(_line);
            // makes colon COLORS[2] color
            if (line.contains(":")) {
                String[] split = StringUtils.split(line, ":", 2);
                var builder = Component.text();
                builder.append(Util.colorizeString(split[0]));
                builder.append(Component.text(":", colors[2]));
                if (split[0].equals("Description") && split[1].trim().equals("")) split[1] = " None";
                if (split.length > 1) {
                    if (split[0].equals("Usage")) split[1] = Util.ensureSlashInUsage(split[1]);
                    builder.append(Util.colorizeString(split[1]));
                }
                components.add(builder.build());
            } else {
                components.add(Util.colorizeString(line));
            }
        }
        return components;
    }
}
