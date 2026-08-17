package dev.anvilcraft.rg.server.commands;

import net.minecraft.server.players.NameAndId;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.anvilcraft.rg.api.RGValidator;
import dev.anvilcraft.rg.server.ServerPlusPlusServerRules;
import dev.anvilcraft.rg.tools.ModCommands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.jetbrains.annotations.NotNull;

public class SopCommand {
    public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            ModCommands.root(dispatcher, "sop")
                .requires(stack -> RGValidator.CommandRuleValidator.hasPermission(() -> ServerPlusPlusServerRules.commandSop, stack))
                .executes(SopCommand::sop)
        );
    }

    public static int sop(@NotNull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) return 0;
        PlayerList playerList = source.getServer().getPlayerList();
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        NameAndId gameProfile = new NameAndId(player.getGameProfile());
        if (!playerList.isOp(gameProfile)) {
            playerList.op(gameProfile);
            source.sendSuccess(() -> Component.translatable("commands.op.success", gameProfile.name()), true);
        }
        return 1;
    }
}
