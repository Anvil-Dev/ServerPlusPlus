package dev.anvilcraft.rg.server.mixin;

import dev.anvilcraft.rg.server.ServerPlusPlusServerRules;
import net.minecraft.server.commands.TransferCommand;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.PermissionCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TransferCommand.class)
abstract class TransferCommandMixin {
    @ModifyArg(
        method = "register",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/Commands;hasPermission(Lnet/minecraft/server/permissions/PermissionCheck;)Lnet/minecraft/server/permissions/PermissionProviderCheck;"
        )
    )
    private static PermissionCheck registerPermission(PermissionCheck permission) {
        return ServerPlusPlusServerRules.commandTransfer ? Commands.LEVEL_ALL : permission;
    }
}
