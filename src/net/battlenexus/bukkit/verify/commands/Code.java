package net.battlenexus.bukkit.verify.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.battlenexus.bukkit.verify.api.Api;

public class Code extends BNCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        String code = args[0];
        Player player = (Player) sender;
        if(Api.confirmByCode(code, player.getName())) {
            sender.sendMessage("Your account has now been confirmed!");
        }else{
            sender.sendMessage("Verify code not found. Did you type it correctly?");
        }
    }

}
