package io.lionpa.codeInit.command;

import io.lionpa.codeInit.CodeInitializer;
import io.lionpa.codeInit.register.Register;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Register(type = "command", data = "get")
public class GetCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player = (Player) commandSender;

        if (args.length == 0){
            player.sendMessage(CodeInitializer.data.keySet().toString());
            return false;
        }
        Object d = CodeInitializer.data.get(args[0]);
        if (d == null) {
            player.sendMessage("Не найдено!");
            return false;
        }
        player.sendMessage(d.toString());
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return CodeInitializer.Data.getData(args[0]);
        }
        return null;
    }
}
