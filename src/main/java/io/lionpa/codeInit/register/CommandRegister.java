package io.lionpa.codeInit.register;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class CommandRegister implements IRegister {
    @Override
    public void run(Register register, Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        TabExecutor executor = (TabExecutor) constructor.newInstance();
        String name = register.data();
        PluginCommand command = Bukkit.getPluginCommand(name);
        if (command == null) {
            return;
        }
        command.setTabCompleter(executor);
        command.setExecutor(executor);
    }
}
