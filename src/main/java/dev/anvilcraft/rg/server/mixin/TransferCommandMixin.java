package dev.anvilcraft.rg.server.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.serialization.MapCodec;
import dev.anvilcraft.rg.server.ServerPlusPlusServerRules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.commands.TransferCommand;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.PermissionProviderCheck;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.PermissionSetSupplier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TransferCommand.class)
abstract class TransferCommandMixin {
    @WrapOperation(
        method = "register", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/commands/Commands;hasPermission(Lnet/minecraft/server/permissions/PermissionCheck;)Lnet/minecraft/server/permissions/PermissionProviderCheck;"
    )
    )
    private static <T extends PermissionSetSupplier> @NotNull PermissionProviderCheck<T> registerPermission(
        PermissionCheck permission,
        Operation<PermissionProviderCheck<T>> original
    ) {
        if (!ServerPlusPlusServerRules.commandTransfer) return original.call(permission);
        PermissionCheck allowAll = new PermissionCheck() {
            @Override
            public boolean check(PermissionSet permissions) {
                return true;
            }

            @Override
            public MapCodec<? extends PermissionCheck> codec() {
                return MapCodec.unit(this);
            }
        };
        return new PermissionProviderCheck<>(allowAll);
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
        return original.call(string, argumentType).requires(stack -> Commands.hasPermission(Commands.LEVEL_ADMINS).test(stack));
    }
}
