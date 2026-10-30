package dev.anvilcraft.rg.server.event.listener;

import dev.anvilcraft.rg.api.RGRule;
import dev.anvilcraft.rg.api.event.RGRuleChangeEvent;
import dev.anvilcraft.rg.mixin.DedicatedServerAccessor;
import dev.anvilcraft.rg.server.ServerPlusPlus;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ServerPlusPlus.MOD_ID)
public class RGRuleChangeEventListener {
    @SubscribeEvent
    public static void onRuleChange(RGRuleChangeEvent.Server<Integer> event) {
        RGRule<Integer> rule = event.getRule();
        if (rule.name().equals("viewDistance")) {
            changeViewDistance(event.getServer(), event.getNewValue());
        } else if (rule.name().equals("simulationDistance")) {
            changeSimulationDistance(event.getServer(), event.getNewValue());
        }
    }

    public static void changeViewDistance(MinecraftServer server, int value) {
        if (!server.isDedicatedServer()) return;
        int distance = value >= 2 ? value : ((DedicatedServerAccessor) server).getSettings().getProperties().viewDistance.get();
        server.getPlayerList().setViewDistance(distance);
    }

    public static void changeSimulationDistance(MinecraftServer server, int value) {
        if (!server.isDedicatedServer()) return;
        int distance = value >= 2 ? value : ((DedicatedServerAccessor) server).getSettings().getProperties().simulationDistance.get();
        server.getPlayerList().setSimulationDistance(distance);
    }
}
