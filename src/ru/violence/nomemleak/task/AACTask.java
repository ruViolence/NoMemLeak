package ru.violence.nomemleak.task;

import lombok.SneakyThrows;
import me.konsolas.aac.AAC;
import me.konsolas.aac.a3;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.violence.nomemleak.NoMemLeakPlugin;
import ru.violence.nomemleak.Utils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class AACTask extends BukkitRunnable {
    private final NoMemLeakPlugin plugin;
    private final AAC aacPlugin;
    private final Field field_k_a3;

    public AACTask(NoMemLeakPlugin plugin) throws Exception {
        this.plugin = plugin;
        this.aacPlugin = (AAC) Bukkit.getPluginManager().getPlugin("AAC");
        this.field_k_a3 = Utils.getFieldAccessible(a3.class, "k");
    }

    @SuppressWarnings("unchecked")
    @Override
    @SneakyThrows
    public void run() {
        Set<Player> values = ((Map<Player, ?>) this.field_k_a3.get(this.aacPlugin.i)).keySet();
        int prevSize = values.size();
        values.removeIf(player -> !player.isOnline());
        this.plugin.logCleared("AAC", prevSize - values.size());
    }
}
