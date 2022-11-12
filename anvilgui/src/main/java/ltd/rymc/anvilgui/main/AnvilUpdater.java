package ltd.rymc.anvilgui.main;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class AnvilUpdater extends PacketAdapter {

    public AnvilUpdater(Plugin plugin) {
        super(PacketAdapter.params(plugin, PacketType.Play.Client.ITEM_NAME).optionAsync());
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        if (Anvil.getInstance().getOpenPlayers().contains(player.getUniqueId())) player.updateInventory();
    }
}
