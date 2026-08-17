package dev.anvilcraft.rg.server.mixin;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.rg.server.ServerPlusPlusServerRules;
import net.minecraft.server.commands.TransferCommand;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.PermissionSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TransferCommand.class)
abstract class TransferCommandMixin {
    private static final PermissionCheck ALLOW_ALL = new AllowAllPermissionCheck();

    @ModifyArg(
        method = "register",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/Commands;hasPermission(Lnet/minecraft/server/permissions/PermissionCheck;)Lnet/minecraft/server/permissions/PermissionProviderCheck;"
        )
    )
    private static PermissionCheck registerPermission(PermissionCheck permission) {
        return ServerPlusPlusServerRules.commandTransfer ? ALLOW_ALL : permission;
    }

    private static final class AllowAllPermissionCheck implements PermissionCheck {
        @Override
        public boolean check(PermissionSet permissions) {
            return true;
        }

        @Override
        public MapCodec<? extends PermissionCheck> codec() {
            return MapCodec.unit(this);
        }
    }
}
