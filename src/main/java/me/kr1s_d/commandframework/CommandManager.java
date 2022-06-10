package me.kr1s_d.commandframework;

import me.kr1s_d.commandframework.objects.SubCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.util.*;
import java.util.function.Consumer;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final List<SubCommand> loadedCommands;
    private final Set<String> tabComplete;
    private String wrongArgumentMessage;
    private String noPermsMessage;
    private String noPlayerMessage;
    private final String command;
    private String prefix;
    private final String[] aliases;
    private Consumer<CommandSender> commandSenderConsumer;
    private boolean hasDefaultCommandAction;

    public CommandManager(String command, String prefix, String... aliases){
        this.loadedCommands = new ArrayList<>();
        this.tabComplete = new HashSet<>();
        this.wrongArgumentMessage = "&cYou entered a wrong argument!";
        this.noPermsMessage = "&cYou don't have enough permissions to do this command!";
        this.noPlayerMessage = "&cOnly players can do this command!";
        this.command = command;
        this.aliases = aliases;
        this.hasDefaultCommandAction = false;
        this.prefix = prefix;
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
            commandSenderConsumer.accept(sender);
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(colora(prefix + wrongArgumentMessage));
            return true;
        }
        SubCommand cmd = getSubCommandFromArgs(args[0]);
        if(cmd == null){
            sender.sendMessage(colora(prefix + wrongArgumentMessage));
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
            sender.sendMessage(colora(prefix + wrongArgumentMessage));
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
                return new ArrayList<>(tabComplete);
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

    public void setWrongArgumentMessage(String wrongArgumentMessage) {
        this.wrongArgumentMessage = wrongArgumentMessage;
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

    public void onSenderBaseCommand(Consumer<CommandSender> senderConsumer) {
        this.commandSenderConsumer = senderConsumer;
        this.hasDefaultCommandAction = true;
    }
}
