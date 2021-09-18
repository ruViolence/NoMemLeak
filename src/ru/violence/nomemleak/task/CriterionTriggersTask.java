package ru.violence.nomemleak.task;

import lombok.SneakyThrows;
import net.minecraft.server.v1_12_R1.AdvancementDataPlayer;
import net.minecraft.server.v1_12_R1.CriterionTrigger;
import net.minecraft.server.v1_12_R1.CriterionTriggers;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.scheduler.BukkitRunnable;
import ru.violence.nomemleak.NoMemLeakPlugin;
import ru.violence.nomemleak.Utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class CriterionTriggersTask extends BukkitRunnable {
    private final NoMemLeakPlugin plugin;
    private final Field field_player_AdvancementDataPlayer;

    public CriterionTriggersTask(NoMemLeakPlugin plugin) throws Exception {
        this.plugin = plugin;
        this.field_player_AdvancementDataPlayer = Utils.getFieldAccessible(AdvancementDataPlayer.class, "player");
    }

    @Override
    public void run() {
        int cleared = 0;

        cleared += clearMap(CriterionTriggers.b, "a");
        cleared += clearMap(CriterionTriggers.c, "a");
        cleared += clearMap(CriterionTriggers.d, "b");
        cleared += clearMap(CriterionTriggers.e, "b");
        cleared += clearMap(CriterionTriggers.f, "b");
        cleared += clearMap(CriterionTriggers.g, "b");
        cleared += clearMap(CriterionTriggers.h, "b");
        cleared += clearMap(CriterionTriggers.i, "b");
        cleared += clearMap(CriterionTriggers.j, "b");
        cleared += clearMap(CriterionTriggers.k, "b");
        cleared += clearMap(CriterionTriggers.l, "b");
        cleared += clearMap(CriterionTriggers.m, "b");
        cleared += clearMap(CriterionTriggers.n, "b");
        cleared += clearMap(CriterionTriggers.o, "b");
        cleared += clearMap(CriterionTriggers.p, "b");
        cleared += clearMap(CriterionTriggers.q, "b");
        cleared += clearMap(CriterionTriggers.r, "b");
        cleared += clearMap(CriterionTriggers.s, "b");
        cleared += clearMap(CriterionTriggers.t, "b");
        cleared += clearMap(CriterionTriggers.u, "b");
        cleared += clearMap(CriterionTriggers.v, "b");
        cleared += clearMap(CriterionTriggers.w, "b");
        cleared += clearMap(CriterionTriggers.x, "b");
        cleared += clearMap(CriterionTriggers.y, "b");
        cleared += clearMap(CriterionTriggers.z, "b");
        cleared += clearMap(CriterionTriggers.A, "b");
        cleared += clearMap(CriterionTriggers.B, "b");

        this.plugin.logCleared("CriterionTriggers", cleared);
    }

    private int clearMap(CriterionTrigger<?> trigger, String fieldName) {
        Map<AdvancementDataPlayer, ?> map = getCriterionTriggersMap(trigger, fieldName);
        int prevSize = map.size();

        List<EntityPlayer> playerList = ((CraftServer) Bukkit.getServer()).getHandle().v();
        map.keySet().removeIf(advancementDataPlayer -> !playerList.contains(getPlayer(advancementDataPlayer)));

        return prevSize - map.size();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private Map<AdvancementDataPlayer, ?> getCriterionTriggersMap(CriterionTrigger<?> trigger, String fieldName) {
        return (Map<AdvancementDataPlayer, ?>) Utils.getFieldAccessible(trigger.getClass(), fieldName).get(trigger);
    }

    @SneakyThrows
    private EntityPlayer getPlayer(AdvancementDataPlayer advancementData) {
        return (EntityPlayer) this.field_player_AdvancementDataPlayer.get(advancementData);
    }
}
