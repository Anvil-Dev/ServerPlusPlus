package dev.anvilcraft.rg.server;

import com.mojang.logging.LogUtils;
import dev.anvilcraft.rg.server.utils.WelcomeMessage;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(ServerPlusPlus.MOD_ID)
public class ServerPlusPlus {
    public static final String MOD_ID = "server_plus_plus";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ServerPlusPlus(@NotNull @SuppressWarnings("unused") IEventBus modEventBus, @NotNull @SuppressWarnings("unused") ModContainer modContainer) {
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggingIn);
    }

    @SubscribeEvent
    public void onPlayerLoggingIn(@NotNull PlayerEvent.PlayerLoggedInEvent event) {
        if (ServerPlusPlusServerRules.welcomePlayer) {
            WelcomeMessage.onPlayerLoggedIn((ServerPlayer) event.getEntity());
        }
    }

    public static @NotNull Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ServerPlusPlus.MOD_ID, path);
    }
}
