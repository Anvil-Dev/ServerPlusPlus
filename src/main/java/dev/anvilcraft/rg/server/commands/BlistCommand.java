package dev.anvilcraft.rg.server.commands;

import net.minecraft.server.players.NameAndId;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class BlistCommand {
    private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED = new SimpleCommandExceptionType(Component.translatable("commands.ban.failed"));
    private static final SimpleCommandExceptionType ERROR_NOT_BANNED = new SimpleCommandExceptionType(Component.translatable("commands.pardon.failed"));
    public static final FilesUtil.MapFile<String, Boolean> PERMISSION = new FilesUtil.MapFile<>("blist", Object::toString, Boolean.class);

    public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            ModCommands.root(dispatcher, "blist")
                .requires(stack -> RGValidator.CommandRuleValidator.hasPermission(() -> ServerPlusPlusServerRules.commandBlist, stack) && WlistCommand.hasPermission(PERMISSION, stack))
                .executes(BlistCommand::list)
                .then(
                    Commands.literal("permission")
                        .requires(stack -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(
                            Commands.literal("add")
                                .then(
                                    Commands.argument("targets", GameProfileArgument.gameProfile())
                                        .executes(BlistCommand::permissionAdd)
                                )
                        )
                        .then(
                            Commands.literal("remove")
                                .then(
                                    Commands.argument("targets", GameProfileArgument.gameProfile())
                                        .executes(BlistCommand::permissionRemove)
                                )
                        )
                )
                .then(
                    Commands.literal("add")
                        .then(
                            Commands.argument("targets", GameProfileArgument.gameProfile())
                                .executes(BlistCommand::add)
                                .then(
                                    Commands.argument("reson", StringArgumentType.greedyString())
                                        .executes(BlistCommand::add)
                                )
                        )
                )
                .then(
                    Commands.literal("remove")
                        .then(
                            Commands.argument("targets", GameProfileArgument.gameProfile())
                                .suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(commandContext.getSource().getServer().getPlayerList().getBans().getUserList(), suggestionsBuilder))
                                .executes(BlistCommand::remove)
                        )
                )
        );
    }

    public static int add(@NotNull CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        UserBanList userBanList = source.getServer().getPlayerList().getBans();
        int i = 0;
        Component component = null;
        try {
            component = Component.literal(StringArgumentType.getString(context, "reson"));
        } catch (IllegalArgumentException ignored) {
        }
        for (NameAndId gameProfile : GameProfileArgument.getGameProfiles(context, "targets")) {
            if (!userBanList.isBanned(gameProfile)) {
                UserBanListEntry userBanListEntry = new UserBanListEntry(gameProfile, null, source.getTextName(), null, component == null ? null : component.getString());
                userBanList.add(userBanListEntry);
                ++i;
                source.sendSuccess(() -> Component.translatable("commands.ban.success", Component.literal(gameProfile.name()), userBanListEntry.getReason()), true);
                ServerPlayer serverPlayer = source.getServer().getPlayerList().getPlayer(gameProfile.id());
                if (serverPlayer != null) {
                    serverPlayer.connection.disconnect(Component.translatable("multiplayer.disconnect.banned"));
                }
            }
        }

        if (i == 0) {
            throw ERROR_ALREADY_BANNED.create();
        } else {
            return i;
        }
    }

    public static int remove(@NotNull CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        UserBanList userBanList = source.getServer().getPlayerList().getBans();
        int i = 0;
        for (NameAndId gameProfile : GameProfileArgument.getGameProfiles(context, "targets")) {
            if (userBanList.isBanned(gameProfile)) {
                userBanList.remove(gameProfile);
                ++i;
                source.sendSuccess(() -> Component.translatable("commands.pardon.success", Component.literal(gameProfile.name())), true);
            }
        }
        if (i == 0) {
            throw ERROR_NOT_BANNED.create();
        } else {
            return i;
        }
    }

    public static int list(@NotNull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Collection<UserBanListEntry> collection = source.getServer().getPlayerList().getBans().getEntries();
        if (collection.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("commands.banlist.none"), false);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.banlist.list", collection.size()), false);

            for (UserBanListEntry userBanListEntry : collection) {
                source.sendSuccess(() -> Component.translatable("commands.banlist.entry", userBanListEntry.getDisplayName(), userBanListEntry.getSource(), userBanListEntry.getReason()), false);
            }
        }
        return collection.size();
    }

    private static int permissionAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        PERMISSION.init(context);
        int i = 0;
        Collection<NameAndId> targets = GameProfileArgument.getGameProfiles(context, "targets");
        for (NameAndId target : targets) {
            PERMISSION.map.put(target.id().toString(), true);
            context.getSource().sendSuccess(() -> TranslationUtil.trans("command_blist.message.granted_permission", target.name()), true);
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
            context.getSource().sendSuccess(() -> TranslationUtil.trans("command_blist.message.revoked_permission", target.name()), true);
            ++i;
        }
        ModCommands.notifyPlayersCommandsChanged(context.getSource().getServer());
        PERMISSION.save();
        return i;
    }
}
