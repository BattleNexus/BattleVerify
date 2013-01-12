package net.battlenexus.bukkit.verify.commands;

import java.util.logging.Logger;

import net.battlenexus.bukkit.verify.sql.SqlClass;

import org.bukkit.command.CommandSender;

public abstract class BNCommand {
    protected static final Logger log = Logger.getLogger("Minecraft");
    public SqlClass sql;

    public abstract void execute(CommandSender sender, String[] args);

}