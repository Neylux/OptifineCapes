package de.neylux.optifinecapes;

import com.mojang.authlib.GameProfile;
import net.minecraft.resources.ResourceLocation;

public abstract class PlayerCapeHandler {
    private final GameProfile profile;
    private ResourceLocation capeTexture = null;
    private boolean capeAvailable = false;

    public PlayerCapeHandler(GameProfile profile) {
        this.profile = profile;
    }

    public abstract void fetchCape();

    public GameProfile getProfile() {
        return profile;
    }

    public void setCapeAvailable(boolean capeAvailable) {
        this.capeAvailable = capeAvailable;
    }

    public void setCapeTexture(ResourceLocation capeTexture) {
        this.capeTexture = capeTexture;
    }

    public ResourceLocation getCapeTexture() {
        return capeTexture;
    }

    public boolean isCapeAvailable() {
        return capeAvailable;
    }
}
