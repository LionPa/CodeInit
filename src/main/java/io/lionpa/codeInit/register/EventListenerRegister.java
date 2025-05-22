package io.lionpa.codeInit.register;


import io.lionpa.codeInit.CodeInit;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class EventListenerRegister implements IRegister {
    @Override
    public void run(Register register, Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Listener listener = (Listener) constructor.newInstance();
        Bukkit.getPluginManager().registerEvents(listener, CodeInit.getPlugin());
    }
}
