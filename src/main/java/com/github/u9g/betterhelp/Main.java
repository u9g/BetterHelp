package com.github.u9g.betterhelp;

import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.commandmanager.CommandParser;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        new CommandParser(this.getResource("help.rdcml")).parse().register("better", new BetterHelp());
    }
}
