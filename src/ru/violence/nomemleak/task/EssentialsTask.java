package ru.violence.nomemleak.task;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import com.google.common.cache.Cache;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import ru.violence.nomemleak.NoMemLeakPlugin;
import ru.violence.nomemleak.Utils;

import java.lang.reflect.Field;
import java.util.Collection;

public class EssentialsTask extends BukkitRunnable {
    private final NoMemLeakPlugin plugin;
    private final Essentials essPlugin;
    private final Field field_users_UserMap;

    public EssentialsTask(NoMemLeakPlugin plugin) throws Exception {
        this.plugin = plugin;
        this.essPlugin = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        this.field_users_UserMap = Utils.getFieldAccessible(UserMap.class, "users");
    }

    @Override
    public void run() {
        Collection<User> values = getUsersCache().asMap().values();
        int prevSize = values.size();
        values.removeIf(user -> !user.getBase().isOnline());
        this.plugin.logCleared("Essentials", prevSize - values.size());
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private Cache<String, User> getUsersCache() {
        return (Cache<String, User>) this.field_users_UserMap.get(this.essPlugin.getUserMap());
    }
}
