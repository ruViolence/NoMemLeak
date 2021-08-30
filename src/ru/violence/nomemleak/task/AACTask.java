package ru.violence.nomemleak.task;

import me.konsolas.aac.AAC;
import me.konsolas.aac.a3;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.violence.nomemleak.NoMemLeakPlugin;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class AACTask extends BukkitRunnable {
    private final NoMemLeakPlugin plugin;
    private final AAC aacPlugin;
    private final Field kField;

    public AACTask(NoMemLeakPlugin plugin) throws Exception {
        this.plugin = plugin;
        this.aacPlugin = (AAC) Bukkit.getPluginManager().getPlugin("AAC");
        this.kField = a3.class.getDeclaredField("k");
        this.kField.setAccessible(true);
    }

    @Override
    public void run() {
        Set<Player> values = getKMap().keySet();
        int prevSize = values.size();
        values.removeIf(player -> !player.isOnline());
        this.plugin.logCleared("AAC", prevSize - values.size());
    }

    @SuppressWarnings("unchecked")
    private Map<Player, ?> getKMap() {
        try {
            return (Map<Player, ?>) this.kField.get(this.aacPlugin.i);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
