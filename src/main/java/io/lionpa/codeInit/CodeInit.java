package io.lionpa.codeInit;

import io.lionpa.codeInit.register.CommandRegister;
import io.lionpa.codeInit.register.EventListenerRegister;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class CodeInit extends JavaPlugin {

    private static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        CodeInitializerStart.countPlugins();

        CodeInitializer.addRegister("event", new EventListenerRegister());
        CodeInitializer.addRegister("command", new CommandRegister());

        addPlugin("io.lionpa.codeInit", this);
    }

    @Override
    public void onDisable() {
        CodeInitializer.stop();
    }

    public static void addPlugin(String packageName, Plugin plugin){
        CodeInitializerStart.addPlugin(packageName, plugin);
    }

    public static Plugin getPlugin(){
        return plugin;
    }
}
