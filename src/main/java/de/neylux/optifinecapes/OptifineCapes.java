package de.neylux.optifinecapes;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = OptifineCapes.MOD_ID, dist = Dist.CLIENT)
public class OptifineCapes {
    public static final String MOD_ID = "optifinecapes";
    private static final PlayerCapeManager playerCapeManager = new PlayerCapeManager();

    public static PlayerCapeManager getPlayerCapeManager() {
        return playerCapeManager;
    }
}
