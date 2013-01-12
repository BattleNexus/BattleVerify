package net.battlenexus.bukkit.verify.listeners;

import java.lang.reflect.Constructor;

import net.battlenexus.bukkit.verify.sql.SqlClass;
import net.battlenexus.bukkit.verify.commands.BNCommand;

import org.apache.commons.lang.WordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BattleCommands implements CommandExecutor {

    SqlClass sql;
    public static BattleCommands instance;

    public BattleCommands(SqlClass sql) {
        this.sql = sql;            
        instance = this;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
            String[] args) {
        // If no arguments are entered, show balance if player or help if
        // console
        if (args.length < 1) {
            return false;
        }

        try {            
            Class<?> class_ = Class
                    .forName("net.battlenexus.bukkit.verify.commands."
                            + WordUtils.capitalizeFully(cmd.getName().equalsIgnoreCase("bv") ? args[0] : label));
            Class<? extends BNCommand> runClass = class_
                    .asSubclass(BNCommand.class);
            Constructor<? extends BNCommand> constructor = runClass
                    .getConstructor();
            BNCommand command = constructor.newInstance();
            String[] newargs = new String[args.length - 1];
            if (newargs.length >= 2)
                System.arraycopy(args, 1, newargs, 0, newargs.length);
            else if (newargs.length == 1)
                newargs[0] = args[1];
            command.sql = sql;
            try {
                command.execute(sender, newargs);
            } catch (Exception e) { sender.sendMessage("There was an error running the command."); e.printStackTrace(); }
        } catch (Exception e) {
            sender.sendMessage("Command not found.");
        }

        return true;
    }
}