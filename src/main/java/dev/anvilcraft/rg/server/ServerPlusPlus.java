package dev.anvilcraft.rg.server;

import com.mojang.logging.LogUtils;
import dev.anvilcraft.rg.api.RGAdditional;
import dev.anvilcraft.rg.api.server.ServerRGRuleManager;
import dev.anvilcraft.rg.api.server.TranslationUtil;
import dev.anvilcraft.rg.server.utils.WelcomeMessage;
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
public class ServerPlusPlus implements RGAdditional {
    public static final String MOD_ID = "server_plus_plus";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ServerPlusPlus(@SuppressWarnings("unused") IEventBus modEventBus, @NotNull ModContainer modContainer) {
        modContainer.registerExtensionPoint(RGAdditional.class, this);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggingIn);
    }

    @SubscribeEvent
    public void onPlayerLoggingIn(@NotNull PlayerEvent.PlayerLoggedInEvent event){
        if(ServerPlusPlusServerRules.welcomePlayer){
            WelcomeMessage.onPlayerLoggedIn((ServerPlayer) event.getEntity());
        }
    }

    @Override
    public void loadServerRules(@NotNull ServerRGRuleManager manager) {
        manager.register(ServerPlusPlusServerRules.class);
        TranslationUtil.loadLanguage(ServerPlusPlus.class, ServerPlusPlus.MOD_ID, "en_us");
        TranslationUtil.loadLanguage(ServerPlusPlus.class, ServerPlusPlus.MOD_ID, "zh_cn");
    }
}
