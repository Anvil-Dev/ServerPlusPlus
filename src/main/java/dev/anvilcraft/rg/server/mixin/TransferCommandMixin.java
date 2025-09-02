package dev.anvilcraft.rg.server.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.anvilcraft.rg.server.ServerPlusPlusServerRules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.PermissionSource;
import net.minecraft.server.commands.PermissionCheck;
import net.minecraft.server.commands.TransferCommand;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TransferCommand.class)
abstract class TransferCommandMixin {
    @WrapOperation(
        method = "register", at = @At(
        value = "INVOKE", target = "Lnet/minecraft/commands/Commands;hasPermission(I)Lnet/minecraft/server/commands/PermissionCheck;"
    )
    )
    private static <T extends PermissionSource> @NotNull PermissionCheck<T> registerPermission(
        int level,
        Operation<PermissionCheck<T>> original
    ) {
        return ServerPlusPlusServerRules.commandTransfer ? new PermissionCheck<>() {
            @Override
            public int requiredLevel() {
                return 0;
            }

            @Override
            public boolean test(T t) {
                return true;
            }
        } : original.call(level);
    }

    @WrapOperation(
        method = "register", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/commands/Commands;argument(Ljava/lang/String;Lcom/mojang/brigadier/arguments/ArgumentType;)Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;",
        ordinal = 2
    )
    )
    private static <T> @NotNull RequiredArgumentBuilder<CommandSourceStack, T> register(
        String string,
        ArgumentType<T> argumentType,
        @NotNull Operation<RequiredArgumentBuilder<CommandSourceStack, T>> original
    ) {
        return original.call(string, argumentType).requires(stack -> stack.hasPermission(3));
    }
}
