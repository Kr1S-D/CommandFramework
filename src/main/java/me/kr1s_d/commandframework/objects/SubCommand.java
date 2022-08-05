package me.kr1s_d.commandframework.objects;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public interface SubCommand {
    String getSubCommandId();

    void execute(CommandSender sender, String[] args);

    String getPermission();

    int minArgs();

    Map<Integer, List<String>> getTabCompleter();

    boolean allowedConsole();
}
