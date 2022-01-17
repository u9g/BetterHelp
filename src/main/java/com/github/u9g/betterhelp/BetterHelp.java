package com.github.u9g.betterhelp;

import com.github.u9g.betterhelp.helpers.CommandHelper;
import com.github.u9g.betterhelp.helpers.FontHelper;
import com.github.u9g.u9gutils.ItemBuilder;
import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.placeholder.Placeholder;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import redempt.redlib.commandmanager.CommandHook;

import java.util.*;

import static com.github.u9g.betterhelp.Constants.*;

public class BetterHelp {
    private static final int PAGE_SIZE = 8;
    private final MiniMessage mm = MiniMessage.miniMessage();

    @CommandHook("help")
    public void onHelp (final CommandSender cs, Integer page) {
        page = page == null ? 1 : page;
        int skip = (page-1) * PAGE_SIZE;
        List<Map.Entry<String, Command>> cmds = CommandHelper.getCommandsFor(cs);
        if (skip >= cmds.size()) return; // TODO: give good error message, page is too large.
        int maxPage = (int)Math.ceil(cmds.size()/(double)PAGE_SIZE); // max page
        var msgBuilder = Component.text(); // builder to construct chat message
        msgBuilder.append(makeHeader(page, maxPage)); // Header with page number
        int end = Math.min(PAGE_SIZE, cmds.size() - skip) + skip;
        for (int i = skip; i < end; i++) {
            Command command = cmds.get(i).getValue();
            Component prefix = Component.text(
                    (i == skip  ?  FIRST_LINE_PREFIX:  // first line
                            i == end-1 ?   LAST_LINE_PREFIX:  // last line
                                         MIDDLE_LINE_PREFIX   // middle line
                    ) + PREFIX_HYPHEN + " ", COLORS[4]);
            msgBuilder.append(makeCommandComponent(cs, prefix, command));
        }
        msgBuilder.append(makeBottomBorder(page, maxPage));
        cs.sendMessage(msgBuilder.build());
    }

    private Component makeHeader(int page, int maxPage) {
        String pageHeader = "(%d/%d)".formatted(page, maxPage);
        String border = FontHelper.centerMessage(pageHeader, BORDER_CHAR);
        PlaceholderResolver tempResolver = PlaceholderResolver.combining(colorResolver, PlaceholderResolver.placeholders(
                Placeholder.component("lborder", Component.text(border, COLORS[0])), Placeholder.component("rborder", Component.text(StringUtils.reverse(border), COLORS[0])),
                Placeholder.component("current", Component.text(page, COLORS[1])), Placeholder.component("max", Component.text(maxPage, COLORS[1]))
        ));
        return mm.deserialize("<lborder><colors_2>(<current>/<max>)<rborder>\n", tempResolver);
    }

    private Component makeCommandComponent(CommandSender cs, Component prefix, Command command) {
        List<Component> lore = CommandHelper.getCommandInfo(cs, command.getName(), command, COLORS);
        lore.add(Util.colorizeString("Origin: ").append(Component.text(CommandHelper.getPluginOrigin(command), COLORS[1])));
        // make usage first line of item
        Component usageLine = null;
        var toString = PaperComponents.plainTextSerializer();
        for (Component c : lore) {
            String b = toString.serialize(c);
            if (b.contains("Usage")) {
                usageLine = c;
                break;
            }
        }
        final ItemBuilder item = ItemBuilder.of(Material.BARRIER);
        if (usageLine != null) {
            item.name(usageLine);
            lore.remove(usageLine);
            item.lore(lore);
        } else {
            item.name(lore.get(0));
            item.lore(lore.subList(1, lore.size()));
        }
        return prefix
                .append(Util.getColoredSlashCommandName(command.getName(), COLORS[2], COLORS[1]).append(Component.text("\n")))
                .hoverEvent(item.build())
                .clickEvent(ClickEvent.suggestCommand("/"+command.getName()));
    }

    private Component makeBottomBorder (int page, int maxPage) {
        boolean canGoBack = page > 1;
        boolean canGoNext = page < maxPage;
        String bottomStr = (canGoBack ? "["+LEFT_ARROW+"]" : "") + (canGoNext ? "["+RIGHT_ARROW+"]" : "");
        String bborder = FontHelper.centerMessage(bottomStr, BORDER_CHAR);
        var builder = Component.text();
        builder.append(Component.text(bborder, COLORS[0]));
        if (canGoBack) {
            appendArrowToBuilder(builder,LEFT_ARROW, COLORS[3], "Previous page", COLORS[2], "/help " + (page-1), COLORS[1]);
        }
        if (canGoNext)  {
            appendArrowToBuilder(builder,RIGHT_ARROW, COLORS[3], "Next page", COLORS[2], "/help " + (page+1), COLORS[1]);
        }
        builder.append(Component.text(StringUtils.reverse(bborder), COLORS[0]));
        return builder.build();
    }

    private void appendArrowToBuilder (
            TextComponent.Builder builder,
            String textInChat,
            TextColor colorInChat,
            String itemName,
            TextColor itemNameColor,
            String clickCommand,
            TextColor bracketsColor
    ) {
        var source = ItemBuilder.of(Material.BARRIER).name(Component.text(itemName, itemNameColor, TextDecoration.BOLD)).build().asHoverEvent();
        ClickEvent ce = ClickEvent.runCommand(clickCommand);
        builder.append(Component.text("[", bracketsColor).hoverEvent(source).clickEvent(ce));
        builder.append(Component.text(textInChat, colorInChat).hoverEvent(source).clickEvent(ce));
        builder.append(Component.text("]", bracketsColor).hoverEvent(source).clickEvent(ce));
    }
}
