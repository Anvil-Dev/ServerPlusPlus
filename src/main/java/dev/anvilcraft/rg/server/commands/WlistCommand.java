package dev.anvilcraft.rg.server.commands;

import net.minecraft.server.players.NameAndId;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.anvilcraft.rg.api.RGValidator;
import dev.anvilcraft.rg.api.server.TranslationUtil;
import dev.anvilcraft.rg.server.ServerPlusPlusServerRules;
import dev.anvilcraft.rg.tools.FilesUtil;
import dev.anvilcraft.rg.tools.ModCommands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class WlistCommand {
    public static final FilesUtil.MapFile<String, Boolean> PERMISSION = new FilesUtil.MapFile<>("wlist", Object::toString, Boolean.class);
    private static final SimpleCommandExceptionType ERROR_ALREADY_WHITELISTED = new SimpleCommandExceptionType(Component.translatable("commands.whitelist.add.failed"));
    private static final SimpleCommandExceptionType ERROR_NOT_WHITELISTED = new SimpleCommandExceptionType(Component.translatable("commands.whitelist.remove.failed"));

    public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            ModCommands.root(dispatcher, "wlist")
                .requires(stack -> RGValidator.CommandRuleValidator.hasPermission(() -> ServerPlusPlusServerRules.commandWlist, stack) && WlistCommand.hasPermission(PERMISSION, stack))
                .executes(WlistCommand::list)
                .then(
                    Commands.literal("permission")
                        .requires(stack -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(
                            Commands.literal("add")
                                .then(
                                    Commands.argument("targets", GameProfileArgument.gameProfile())
                                        .executes(WlistCommand::permissionAdd)
                                )
                        )
                        .then(
                            Commands.literal("remove")
                                .then(
                                    Commands.argument("targets", GameProfileArgument.gameProfile())
                                        .executes(WlistCommand::permissionRemove)
                                )
                        )
                )
                .then(
                    Commands.literal("add")
                        .then(
                            Commands.argument("targets", GameProfileArgument.gameProfile())
                                .suggests((commandContext, suggestionsBuilder) -> {
                                    PlayerList playerList = commandContext.getSource().getServer().getPlayerList();
                                    return SharedSuggestionProvider.suggest(
                                        playerList.getPlayers()
                                            .stream()
                                            .filter((serverPlayer) -> !playerList.getWhiteList().isWhiteListed(new NameAndId(serverPlayer.getGameProfile())))
                                            .map((serverPlayer) -> new NameAndId(serverPlayer.getGameProfile()).name()),
                                        suggestionsBuilder
                                    );
                                })
                                .executes(WlistCommand::add)
                        )
                )
                .then(
                    Commands.literal("remove")
                        .then(
                            Commands.argument("targets", GameProfileArgument.gameProfile())
                                .suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(commandContext.getSource().getServer().getPlayerList().getWhiteListNames(), suggestionsBuilder))
                                .executes(WlistCommand::remove)
                        )
                )
        );
    }

    public static int add(@NotNull CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        UserWhiteList userWhiteList = source.getServer().getPlayerList().getWhiteList();
        int i = 0;
        for (NameAndId gameProfile : GameProfileArgument.getGameProfiles(context, "targets")) {
            if (!userWhiteList.isWhiteListed(gameProfile)) {
                UserWhiteListEntry userWhiteListEntry = new UserWhiteListEntry(gameProfile);
                userWhiteList.add(userWhiteListEntry);
                i++;
                source.sendSuccess(() -> Component.translatable("commands.whitelist.add.success", Component.literal(gameProfile.name())), true);
            }
        }
        if (i == 0) throw ERROR_ALREADY_WHITELISTED.create();
        else return i;
    }

    public static int remove(@NotNull CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        UserWhiteList userWhiteList = source.getServer().getPlayerList().getWhiteList();
        int i = 0;
        for (NameAndId gameProfile : GameProfileArgument.getGameProfiles(context, "targets")) {
            if (userWhiteList.isWhiteListed(gameProfile)) {
                UserWhiteListEntry userWhiteListEntry = new UserWhiteListEntry(gameProfile);
                userWhiteList.remove(userWhiteListEntry);
                i++;
                source.sendSuccess(() -> Component.translatable("commands.whitelist.remove.success", Component.literal(gameProfile.name())), true);
            }
        }
        if (i == 0) {
            throw ERROR_NOT_WHITELISTED.create();
        } else {
            source.getServer().kickUnlistedPlayers();
            return i;
        }
    }

    public static int list(@NotNull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String[] strings = source.getServer().getPlayerList().getWhiteListNames();
        if (strings.length == 0) {
            source.sendSuccess(() -> Component.translatable("commands.whitelist.none"), false);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.whitelist.list", strings.length, String.join(", ", strings)), false);
        }
        return strings.length;
    }

    public static boolean hasPermission(FilesUtil.MapFile<String, Boolean> permission, @NotNull CommandSourceStack stack) {
        if (stack.hasPermission(Commands.LEVEL_GAMEMASTERS)) return true;
        if (stack.isPlayer()) {
            ServerPlayer player = stack.getPlayer();
            if (player == null) return false;
            return permission.map.getOrDefault(new NameAndId(player.getGameProfile()).id().toString(), false);
        } else return true;
    }

    private static int permissionAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        PERMISSION.init(context);
        int i = 0;
        Collection<NameAndId> targets = GameProfileArgument.getGameProfiles(context, "targets");
        for (NameAndId target : targets) {
            PERMISSION.map.put(target.id().toString(), true);
            context.getSource().sendSuccess(() -> TranslationUtil.trans("command_wlist.message.granted_permission", target.name()), true);
            ++i;
        }
        ModCommands.notifyPlayersCommandsChanged(context.getSource().getServer());
        PERMISSION.save();
        return i;
    }

    private static int permissionRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        PERMISSION.init(context);
        int i = 0;
        Collection<NameAndId> targets = GameProfileArgument.getGameProfiles(context, "targets");
        for (NameAndId target : targets) {
            PERMISSION.map.put(target.id().toString(), false);
            context.getSource().sendSuccess(() -> TranslationUtil.trans("command_wlist.message.revoked_permission", target.name()), true);
            ++i;
        }
        ModCommands.notifyPlayersCommandsChanged(context.getSource().getServer());
        PERMISSION.save();
        return i;
    }
}
