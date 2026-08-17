package dev.anvilcraft.rg.server.mixin;

import dev.anvilcraft.rg.server.ServerPlusPlusServerRules;
import net.minecraft.server.commands.TransferCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TransferCommand.class)
abstract class TransferCommandMixin {
    @ModifyArg(
        method = "register",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/Commands;hasPermission(I)Lnet/minecraft/server/commands/PermissionCheck;"
        )
    )
    private static int registerPermission(int level) {
        return ServerPlusPlusServerRules.commandTransfer ? 0 : level;
    }
}
