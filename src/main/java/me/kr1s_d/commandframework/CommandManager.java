package me.kr1s_d.commandframework;

import me.kr1s_d.commandframework.objects.SubCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {
    private JavaPlugin plugin;
    private final List<SubCommand> loadedCommands;
    private final List<String> tabComplete;
    private String defaultCommandWrongArgumentMessage;
    private String noPermsMessage;
    private String noPlayerMessage;
    private final String command;
    private String prefix;
    private final String[] aliases;
    private Runnable defaultCommandAction;
    private boolean hasDefaultCommandAction;

    public CommandManager(JavaPlugin plugin, String command, String... aliases){
        this.plugin = plugin;
        this.loadedCommands = new ArrayList<>();
        this.tabComplete = new ArrayList<>();
        this.defaultCommandWrongArgumentMessage = "&cWrong Argument!";
        this.noPermsMessage = "&cNo permission!";
        this.noPlayerMessage = "&cYou are not a Player!";
        this.command = command;
        this.aliases = aliases;
        this.hasDefaultCommandAction = false;
        this.prefix = "PREFIX > ";
        try {
            Bukkit.getPluginCommand(command).setTabCompleter(this);
            Bukkit.getPluginCommand(command).setExecutor(this);
        }catch (Exception e){
            Bukkit.getLogger().severe("Error during command & tabcompleter registering...");
            e.printStackTrace();
        }
    }

    public void register(SubCommand subCommand){
        loadedCommands.add(subCommand);
        tabComplete.add(subCommand.getSubCommandId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0 && hasDefaultCommandAction){
            Bukkit.getScheduler().runTask(plugin, defaultCommandAction);
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(colora(prefix + defaultCommandWrongArgumentMessage));
            return true;
        }
        SubCommand cmd = getSubCommandFromArgs(args[0]);
        if(cmd == null){
            sender.sendMessage(colora(prefix + defaultCommandWrongArgumentMessage));
            return true;
        }
        if (args[0].equals(cmd.getSubCommandId()) && args.length == cmd.argsSize()) {
            if (!cmd.allowedConsole() && sender instanceof ConsoleCommandSender) {
                sender.sendMessage(colora(prefix + noPlayerMessage));
                return true;
            }
            if (sender.hasPermission(cmd.getPermission())) {
                cmd.execute(sender, args);
                return true;
            } else {
                sender.sendMessage(colora(prefix + noPermsMessage));
            }
            return true;
        } else {
            sender.sendMessage(colora(prefix + defaultCommandWrongArgumentMessage));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equals(this.command) || Arrays.stream(aliases).filter(a -> a.equals(command.getName())).count() == 1) {
            SubCommand subCommand = getSubCommandFromArgs(args[0]);
            if (subCommand != null && args[0].equals(subCommand.getSubCommandId())) {
                if (subCommand.getTabCompleter() != null) {
                    return subCommand.getTabCompleter().get(args.length -1);
                }
            }
            if(args.length == 1) {
                return tabComplete;
            }
        }
        return null;
    }

    private SubCommand getSubCommandFromArgs(String args0){
        for(SubCommand subCommand : loadedCommands){
            if(subCommand.getSubCommandId().equals(args0)){
                return subCommand;
            }
        }
        return null;
    }

    public void setDefaultCommandWrongArgumentMessage(String defaultCommandWrongArgumentMessage) {
        this.defaultCommandWrongArgumentMessage = defaultCommandWrongArgumentMessage;
    }

    public void setNoPermsMessage(String noPermsMessage) {
        this.noPermsMessage = noPermsMessage;
    }

    public void setNoPlayerMessage(String noPlayerMessage) {
        this.noPlayerMessage = noPlayerMessage;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private String colora(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public void setDefaultCommandAction(Runnable defaultCommandAction) {
        this.hasDefaultCommandAction = true;
        this.defaultCommandAction = defaultCommandAction;
    }
}
