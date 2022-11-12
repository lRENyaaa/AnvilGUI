package ltd.rymc.anvilgui.main.command;


import java.util.ArrayList;
import java.util.List;
import ltd.rymc.anvilgui.main.anvil.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilCmd implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta == null) return true;
            itemMeta.setDisplayName(" ");
            item.setItemMeta(itemMeta);

            ItemStack secondItem = new ItemStack(Material.PUMPKIN);
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§a当前状态: §e出售");
            ItemMeta newItemMeta = item.getItemMeta();
            if (newItemMeta == null) return true;
            newItemMeta.setLore(lore);
            item.setItemMeta(newItemMeta);
            secondItem.setItemMeta(itemMeta);

            new AnvilGUI.Builder()
                    .itemLeft(item)
                    .itemRight(secondItem)
                    .itemOutput(new ItemStack(Material.DIAMOND))
                    .title("请输入领地名称")
                    .open(player);
        }
        return true;
    }
}
