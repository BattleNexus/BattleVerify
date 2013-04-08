package net.battlenexus.bukkit.verify.commands;

import net.battlenexus.bukkit.verify.BattleVerify;
import net.battlenexus.bukkit.verify.api.Api;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Code extends BNCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ConsoleCommandSender){
            sender.sendMessage("This command can only be used by a player");
            return;
        }
        if(args.length == 0){
            sender.sendMessage(ChatColor.YELLOW+"You didn't enter a code");
            return;
        }
        String code = args[0];
        Player player = (Player) sender;
        if(Api.confirmByCode(code, player.getName())) {
            sender.sendMessage(ChatColor.YELLOW+"Your account has now been verified and linked with "+ChatColor.WHITE+Api.lastVerifiedUser+ChatColor.YELLOW+"!");
            BattleVerify.instance.getLogger().info(player.getName()+" has verified their account.");
            EconomyResponse r = BattleVerify.econ.depositPlayer(player.getName(), 100);
            if(r.transactionSuccess()) {
                sender.sendMessage(ChatColor.GREEN+"You have been given $100 for verifying your account.");
            } else {
                sender.sendMessage(String.format("An error occurred: %s", r.errorMessage));
            }
        }else{
            sender.sendMessage(ChatColor.YELLOW+"Verification code not found. Did you type it correctly?");
        }
    }

}
