package ltd.rymc.anvilgui.anvil;


import java.util.ArrayList;
import java.util.List;
import ltd.rymc.anvilgui.Anvil;
import ltd.rymc.anvilgui.version.VersionMatcher;
import ltd.rymc.anvilgui.version.VersionWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilGUI {

    private final Player player;
    private final String inventoryTitle;
    private ItemStack inputLeft;
    private ItemStack inputRight;
    private ItemStack output;
    private boolean open;
    private boolean status = true;
    private int containerId;
    private static final VersionWrapper Wrapper = new VersionMatcher().match();

    private final ListenUp listener = new ListenUp();

    private AnvilGUI(
            Player player, String inventoryTitle, ItemStack inputLeft, ItemStack inputRight, ItemStack output) {
        this.player = player;
        this.inventoryTitle = inventoryTitle;
        this.inputLeft = inputLeft;
        this.inputRight = inputRight;
        this.output = output;
        openInventory();
    }

    private void openInventory() {
        Wrapper.handleInventoryCloseEvent(player);
        Wrapper.setActiveContainerDefault(player);

        Bukkit.getPluginManager().registerEvents(listener, Anvil.getInstance());

        final Object container = Wrapper.newContainerAnvil(player, inventoryTitle);

        Inventory inventory = Wrapper.toBukkitInventory(container);
        inventory.setItem(0, inputLeft);
        inventory.setItem(1, inputRight);
        inventory.setItem(2, output);

        containerId = Wrapper.getNextContainerId(player, container);
        Wrapper.sendPacketOpenWindow(player, containerId, inventoryTitle);
        Wrapper.setActiveContainer(player, container);
        Wrapper.setActiveContainerId(container, containerId);
        Wrapper.addActiveContainerSlotListener(container, player);

        open = true;
        Anvil.getInstance().getOpenPlayers().add(player.getUniqueId());
    }

    private void closeInventory(boolean sendClosePacket) {
        if (!open) {
            return;
        }

        open = false;

        HandlerList.unregisterAll(listener);
        Anvil.getInstance().getOpenPlayers().remove(player.getUniqueId());

        if (sendClosePacket) {
            Wrapper.handleInventoryCloseEvent(player);
            Wrapper.setActiveContainerDefault(player);
            Wrapper.sendPacketCloseWindow(player, containerId);
        }
    }

    private class ListenUp implements Listener {
        @EventHandler
        public void onAnvilCombined(PrepareAnvilEvent event) {
            ItemStack[] contents = event.getInventory().getContents();
            ItemStack firstSlot = contents[0];
            ItemStack secondSlot = contents[1];
            if (firstSlot == null || secondSlot == null) return;
            if (!(firstSlot.getType().equals(inputLeft.getType())
                    || secondSlot.getType().equals(inputRight.getType()))) return;
            event.getInventory().setRepairCost(0);
            event.setResult(output);
        }

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (event.isCancelled()) return;
            if (event.getCurrentItem() == null
                    || event.getCurrentItem().getType().equals(Material.AIR)) return;
            HumanEntity entity = event.getWhoClicked();
            if (!(entity instanceof Player)) return;
            Player player = (Player) entity;
            if (!(event.getInventory() instanceof AnvilInventory)) return;
            if (!Anvil.getInstance().getOpenPlayers().contains(player.getUniqueId())) return;
            event.setCancelled(true);
            Inventory inventory = event.getInventory();
            if (event.getRawSlot() == 2) {
                String text = ((AnvilInventory) event.getInventory()).getRenameText();
                if (text == null) return;
                text = text.replace(" ", "");
                if (text.equals("")) return;
                Bukkit.dispatchCommand(player, "res tp " + text);
                closeInventory(true);
            } else if (event.getRawSlot() == 0) {
                ItemStack item = inventory.getItem(2);
                if (item == null) return;
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta == null) return;
                List<String> lore = itemMeta.getLore();
                if (lore == null) lore = new ArrayList<>();
                lore.add("§f测试: 点击了一次");
                ItemStack newItem = new ItemStack(Material.DIAMOND);
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                newItem.setItemMeta(itemMeta);
                output = newItem;
                inventory.setItem(2, newItem);
            } else if (event.getRawSlot() == 1) {
                ItemStack item = new ItemStack(Material.PUMPKIN);
                List<String> lore = new ArrayList<>();
                lore.add("");
                if (status) {
                    lore.add("§a当前状态: §6收购");
                    status = false;
                } else {
                    lore.add("§a当前状态: §e出售");
                    status = true;
                }
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta == null) return;
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                inputLeft = item;
                inventory.setItem(1, item);
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            if (!(event.getInventory() instanceof AnvilInventory)) return;
            if (!player.getUniqueId().equals(event.getPlayer().getUniqueId())) return;
            closeInventory(false);
        }
    }

    public static class Builder {
        private String title;
        private ItemStack itemLeft;
        private ItemStack itemRight;
        private ItemStack itemOutput;

        public Builder itemLeft(ItemStack item) {
            this.itemLeft = item;
            return this;
        }

        public Builder itemRight(ItemStack item) {
            this.itemRight = item;
            return this;
        }

        public Builder itemOutput(ItemStack item) {
            this.itemOutput = item;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public void open(Player player) {
            new AnvilGUI(player, title, itemLeft, itemRight, itemOutput);
        }
    }
}
