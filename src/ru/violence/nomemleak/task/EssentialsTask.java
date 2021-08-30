package ru.violence.nomemleak.task;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import com.google.common.cache.Cache;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import ru.violence.nomemleak.NoMemLeakPlugin;

import java.lang.reflect.Field;
import java.util.Collection;

public class EssentialsTask extends BukkitRunnable {
    private final NoMemLeakPlugin plugin;
    private final Essentials essPlugin;
    private final Field usersField;

    public EssentialsTask(NoMemLeakPlugin plugin) throws Exception {
        this.plugin = plugin;
        this.essPlugin = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        this.usersField = UserMap.class.getDeclaredField("users");
        this.usersField.setAccessible(true);
    }

    @Override
    public void run() {
        Collection<User> values = getUsersCache().asMap().values();
        int prevSize = values.size();
        values.removeIf(user -> !user.getBase().isOnline());
        this.plugin.logCleared("Essentials", prevSize - values.size());
    }

    @SuppressWarnings("unchecked")
    private Cache<String, User> getUsersCache() {
        try {
            return (Cache<String, User>) this.usersField.get(this.essPlugin.getUserMap());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
