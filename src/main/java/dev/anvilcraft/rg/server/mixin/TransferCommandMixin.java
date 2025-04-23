package dev.anvilcraft.rg.server.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.anvilcraft.rg.server.ServerPlusPlusServerRules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.TransferCommand;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TransferCommand.class)
public class TransferCommandMixin {
    @WrapOperation(method = "lambda$register$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/CommandSourceStack;hasPermission(I)Z"))
    private static boolean registerPermission(CommandSourceStack instance, int i, Operation<Boolean> original) {
        return ServerPlusPlusServerRules.commandTransfer || original.call(instance, i);
    }

    @WrapOperation(method = "register", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;argument(Ljava/lang/String;Lcom/mojang/brigadier/arguments/ArgumentType;)Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;", ordinal = 2))
    private static <T> @NotNull RequiredArgumentBuilder<CommandSourceStack, T> register(String string, ArgumentType<T> argumentType, @NotNull Operation<RequiredArgumentBuilder<CommandSourceStack, T>> original) {
        return original.call(string, argumentType).requires(stack -> stack.hasPermission(3));
    }
}
