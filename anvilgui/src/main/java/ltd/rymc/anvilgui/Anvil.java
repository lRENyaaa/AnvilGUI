package ltd.rymc.anvilgui;


import com.comphenix.protocol.ProtocolLibrary;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import ltd.rymc.anvilgui.command.AnvilCmd;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Anvil extends JavaPlugin {

    public static Anvil Instance;
    private static List<UUID> openPlayers;

    @Override
    public void onEnable() {

        Instance = this;
        openPlayers = new ArrayList<>();

        Objects.requireNonNull(getCommand("anvil")).setExecutor(new AnvilCmd());

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            getLogger().severe("未找到ProtocolLib,插件将自动卸载");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ProtocolLibrary.getProtocolManager().addPacketListener(new AnvilUpdater(this));
    }

    public static Anvil getInstance() {
        return Instance;
    }

    public List<UUID> getOpenPlayers() {
        return openPlayers;
    }
}
