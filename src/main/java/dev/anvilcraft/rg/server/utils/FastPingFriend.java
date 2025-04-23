package dev.anvilcraft.rg.server.utils;

import dev.anvilcraft.rg.api.server.TranslationUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FastPingFriend {
    public static void handleChat(@NotNull MinecraftServer server, @NotNull ServerPlayer player, @NotNull String msg) {
        ServerPlayer player1 = FastPingFriend.findPlayer(server, msg);
        if (player1 == null) return;
        player1.sendSystemMessage(FastPingFriend.getMessage(player), false);
        playSound(SoundEvents.ARROW_HIT_PLAYER, player1);
    }

    public static void handleChatUrgent(@NotNull MinecraftServer server, @NotNull ServerPlayer player, @NotNull String msg) {
        ServerPlayer player1 = FastPingFriend.findPlayer(server, msg);
        if (player1 == null) return;
        player1.connection.send(new ClientboundSetTitleTextPacket(FastPingFriend.getMessage(player)));
        playSound(SoundEvents.BELL_BLOCK, player1);
    }

    public static ServerPlayer findPlayer(@NotNull MinecraftServer server, String name) {
        return server.getPlayerList().getPlayerByName(name);
    }

    public static @NotNull Component getMessage(@NotNull ServerPlayer player) {
        MutableComponent playerName = Component.empty().append(player.getDisplayName()).withStyle(ChatFormatting.GOLD);
        return TranslationUtil.trans("carpet.rule.fastPingFriend.msg", playerName).withStyle(ChatFormatting.AQUA);
    }

    public static void playSound(SoundEvent event, @NotNull ServerPlayer player) {
        Holder<SoundEvent> holder = Holder.direct(event);
        Vec3 pos = player.getEyePosition();
        player.connection.send(new ClientboundSoundPacket(holder, SoundSource.MASTER, pos.x(), pos.y(), pos.z(), 1.0f, 1.0f, player.level().random.nextLong()));
    }
}
