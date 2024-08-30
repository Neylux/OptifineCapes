package de.neylux.optifinecapes;

import com.mojang.authlib.GameProfile;
import net.minecraft.resources.ResourceLocation;

public abstract class PlayerCapeHandler {
    private final GameProfile profile;
    private ResourceLocation capeTexture;
    private boolean capeAvailable;

    public PlayerCapeHandler(GameProfile profile, ResourceLocation capeTexture, boolean capeAvailable) {
        this.profile = profile;
        this.capeTexture = capeTexture;
        this.capeAvailable = capeAvailable;
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
