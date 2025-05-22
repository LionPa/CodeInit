package io.lionpa.codeInit;

import com.google.common.reflect.ClassPath;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

class CodeInitializerStart {

    private static int plugins;
    private static int registeredPlugins;

    private static final Set<String> packages = new HashSet<>();
    private static final Set<Class<?>> classes = new HashSet<>();

    public static void countPlugins(){
        plugins = CodeInitializerCounter.countJarsWithCodeInitializer(Bukkit.getPluginsFolder());
    }

    public static void addPlugin(String packageName, Plugin plugin){
        registeredPlugins++;

        packages.add(packageName);
        classes.add(plugin.getClass());

        if (registeredPlugins >= plugins) {
            register();
        }
    }

    private static void register(){
        Set<ClassPath.ClassInfo> classInfos = new HashSet<>();

        try {
            for (Class<?> clazz : classes) {
                classInfos.addAll(ClassPath.from(clazz.getClassLoader()).getAllClasses());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        CodeInitializer.register(packages, classInfos);
    }


    @RunOn(on = RunOnType.TEST)
    public static void readInfo(Player player){
        player.sendMessage("Plugins detected: " + plugins);
        player.sendMessage("Plugins registered: " + registeredPlugins);
        player.sendMessage("Packages: ");
        for (String packag : packages) {
            player.sendMessage(" - " + packag);
        }
        player.sendMessage("Classes: ");
        for (Class<?> clazz : classes) {
            player.sendMessage(" - " + clazz.getName());
        }
    }
}
