package dev.anvilcraft.rg.server.event.listener;

import com.mojang.brigadier.CommandDispatcher;
import dev.anvilcraft.rg.server.ServerPlusPlus;
import dev.anvilcraft.rg.server.commands.BlistCommand;
import dev.anvilcraft.rg.server.commands.HereCommand;
import dev.anvilcraft.rg.server.commands.LocCommand;
import dev.anvilcraft.rg.server.commands.SopCommand;
import dev.anvilcraft.rg.server.commands.TodoCommand;
import dev.anvilcraft.rg.server.commands.WhereisCommand;
import dev.anvilcraft.rg.server.commands.WlistCommand;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = ServerPlusPlus.MOD_ID)
public class CommandRegisterEventListener {
    @SubscribeEvent
    public static void register(@NotNull RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        HereCommand.register(dispatcher);
        LocCommand.register(dispatcher);
        SopCommand.register(dispatcher);
        TodoCommand.register(dispatcher);
        WhereisCommand.register(dispatcher);
        WlistCommand.register(dispatcher);
        BlistCommand.register(dispatcher);
    }
}
