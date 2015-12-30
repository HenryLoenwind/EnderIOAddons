package info.loenwind.enderioaddons.machine.part;

import java.io.IOException;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandReloadFortune implements ICommand {

  @Override
  public String getCommandName() {
    return "reloadfortune";
  }

  @Override
  public String getCommandUsage(ICommandSender p_71518_1_) {
    return "reloadfortune";
  }

  @Override
  public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
    try {
      int count = ItemMachinePart.readConfig();
      p_71515_1_.addChatMessage(new ChatComponentText("Loaded " + count + " fortunes"));
    } catch (IOException e) {
      p_71515_1_.addChatMessage(new ChatComponentText("Loading fortunes failed: " + e.getLocalizedMessage()));
    }
  }

  @Override
  public List getCommandAliases() {
    return null;
  }

  public static int getRequiredPermissionLevel() {
    return 4;
  }

  @Override
  public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
    return p_71519_1_.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
  }

  @Override
  public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
    return null;
  }

  @Override
  public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
    return false;
  }

  public int compareTo(ICommand p_compareTo_1_) {
    return this.getCommandName().compareTo(p_compareTo_1_.getCommandName());
  }

  @Override
  public int compareTo(Object p_compareTo_1_) {
    return this.compareTo((ICommand) p_compareTo_1_);
  }

}
