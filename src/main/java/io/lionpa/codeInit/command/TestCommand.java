package io.lionpa.codeInit.command;

import io.lionpa.codeInit.CodeInitializer;
import io.lionpa.codeInit.RunOn;
import io.lionpa.codeInit.register.Register;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Register(type = "command", data = "test")
public class TestCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player = (Player) commandSender;

        if (args.length == 0){
            player.sendMessage(CodeInitializer.nodes.keySet().toString());
            return false;
        }
        if (!args[0].contains(":")){
            ArrayList<String> output = new ArrayList<>();
            CodeInitializer.nodes.keySet().forEach(string -> {
                if (string.contains(args[0])) {
                    CodeInitializer.RunOnNode node = CodeInitializer.nodes.get(string);
                    StringBuilder argsInMethod = new StringBuilder();
                    if (node.method() != null)
                        for (Parameter parameter : node.method().getParameters()){
                            argsInMethod.append(parameter.getType().getSimpleName() + ", ");
                        }
                    if (argsInMethod.isEmpty()) {
                        output.add(string);
                    } else {
                        output.add(string + " args(" + argsInMethod.substring(0, Math.max(0, argsInMethod.length() - 2)) + ")");
                    }
                }
            });

            for (String string : output) {
                player.sendMessage(string);
            }
            return false;
        }
        Method d = CodeInitializer.nodes.get(args[0]).method();
        if (d == null) {
            player.sendMessage("Не найдено!");
            return false;
        }
        List<Object> argsForMethod = new ArrayList<>();

        readArgs(d, argsForMethod, player, args);

        player.sendMessage(Arrays.toString(d.getParameters()));
        player.sendMessage("Запуск " + d.getName());
        try {
            d.invoke(null, argsForMethod.toArray());
        } catch (Exception e){
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Ошибка в запуске метода! Проверь консоль");
        }
        return false;
    }

    private static void readArgs(Method method, List<Object> argsForMethod, Player player, String[] args){
        int readArgs = 0;

        try {
            for (Parameter parameter : method.getParameters()) {
                Class<?> t = parameter.getType();

                ClassToArg classToArg = byClass.get(t.getSimpleName());
                if (classToArg != null) {
                    readArgs = classToArg.toArgIO.run(argsForMethod, args[readArgs], readArgs);
                }

                if (t == Player.class) {
                    argsForMethod.add(player);
                    readArgs++;
                }
            }
        } catch (Exception e){
            player.sendMessage("Exception: " + e.getMessage());
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return CodeInitializer.Data.getNodes(args[0]);
        }
        return null;
    }

    @RunOn()
    public static void initClassToArg(){
        ClassToArg.INT.toArgIO.getClass();
    }

    private static final HashMap<String, ClassToArg> byClass = new HashMap<>();

    private enum ClassToArg {
        INT((argsForMethod, arg, read) -> {
            argsForMethod.add(Integer.parseInt(arg));
            return read+1;
        }, "int", "Integer"),
        String((argsForMethod, arg, read) -> {
            argsForMethod.add(arg);
            return read+1;
        }, "String"),
        FLOAT((argsForMethod, arg, read) -> {
            argsForMethod.add(Float.parseFloat(arg));
            return read+1;
        }, "float", "Float"),
        DOUBLE((argsForMethod, arg, read) -> {
            argsForMethod.add(Double.parseDouble(arg));
            return read+1;
        }, "double", "Double");

        private final ClassToArgIO toArgIO;

        ClassToArg(ClassToArgIO toArgIO, String... classes){
            this.toArgIO = toArgIO;
            for (String clazz : classes) {
                byClass.put(clazz, this);
            }
        }
    }
    private interface ClassToArgIO {
        int run(List<Object> argsForMethod, String arg, int read);
    }
}
