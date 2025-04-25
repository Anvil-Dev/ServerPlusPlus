package dev.anvilcraft.rg.server.event.listener;

import dev.anvilcraft.rg.api.event.ServerPlayerChatEvent;
import dev.anvilcraft.rg.server.ServerPlusPlus;
import dev.anvilcraft.rg.server.ServerPlusPlusServerRules;
import dev.anvilcraft.rg.server.utils.FastPingFriend;
import dev.anvilcraft.rg.tools.TriConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = ServerPlusPlus.MOD_ID)
public class PlayerChatEventListener {
    @SubscribeEvent
    public static void onPlayerChat(@NotNull ServerPlayerChatEvent event) {
        ServerPlayer player = event.getEntity();
        Component component = event.getComponent();
        PlayerChatEventListener.handleChat(
            ServerPlusPlusServerRules.fastPingFriend,
            "@ ",
            player,
            component,
            FastPingFriend::handleChat
        );
        PlayerChatEventListener.handleChat(
            ServerPlusPlusServerRules.fastPingFriend,
            "@@ ",
            player,
            component,
            FastPingFriend::handleChatUrgent
        );
    }

    private static void handleChat(
        boolean rule,
        String prefix,
        ServerPlayer player,
        Component component,
        TriConsumer<MinecraftServer, ServerPlayer, String> handle
    ) {
        if (!rule) return;
        String string = component.getString();
        if (!string.startsWith(prefix)) return;
        string = string.substring(prefix.length());
        MinecraftServer server = player.getServer();
        handle.accept(server, player, string);
    }
}
