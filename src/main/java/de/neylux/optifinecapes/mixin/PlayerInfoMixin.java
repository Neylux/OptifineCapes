package de.neylux.optifinecapes.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import de.neylux.optifinecapes.OptifineCapes;
import de.neylux.optifinecapes.PlayerCapeHandler;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PlayerInfo.class, priority = 2000)
public class PlayerInfoMixin {
    @Shadow
    @Final
    private GameProfile profile;
    private boolean capeLoaded = false;

    @ModifyReturnValue(method = "getSkin", at = @At("TAIL"))
    public PlayerSkin changeGetSkin(PlayerSkin original) {
        PlayerCapeHandler handler = OptifineCapes.getPlayerCapeManager().getCapeHandler(profile);

        if (!capeLoaded) {
            capeLoaded = true;
            handler.fetchCape();
        }

        return new PlayerSkin(
                original.texture(),
                null,
                handler.getCapeTexture() == null ? original.capeTexture() : handler.getCapeTexture(),
                original.elytraTexture(),
                original.model(),
                true
        );
    }
}
