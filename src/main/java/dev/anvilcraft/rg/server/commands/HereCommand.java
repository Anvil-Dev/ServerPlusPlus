package dev.anvilcraft.rg.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.anvilcraft.rg.api.RGValidator;
import dev.anvilcraft.rg.server.ServerPlusPlusServerRules;
import dev.anvilcraft.rg.tools.ModCommands;
import dev.anvilcraft.rg.tools.PosUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class HereCommand {
    public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            ModCommands.root(dispatcher, "here")
                .requires(stack -> RGValidator.CommandRuleValidator.hasPermission(() -> ServerPlusPlusServerRules.commandHere, stack))
                .executes(HereCommand::execute)
        );
    }

    public static int execute(@NotNull CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) return 0;
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        for (MutableComponent component : PosUtils.playerPos(player)) {
            server.getPlayerList().broadcastSystemMessage(component, false);
        }
        return 1;
    }
}
