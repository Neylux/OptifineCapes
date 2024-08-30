package de.neylux.optifinecapes;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import de.neylux.optifinecapes.util.CapeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerCapeManager {
    private final HashMap<UUID, PlayerCapeHandler> capeHandlers = new HashMap<>();
    private final HashMap<UUID, ResourceLocation> capeCache = new HashMap<>();
    private final ExecutorService capeExecutor = Executors.newFixedThreadPool(4);
    private static final Logger LOGGER = LogUtils.getLogger();

    public PlayerCapeHandler getCapeHandler(GameProfile profile) {
        return capeHandlers.computeIfAbsent(profile.getId(), uuid -> new PlayerCapeHandler(profile, null, false) {
            @Override
            public void fetchCape() {
                ResourceLocation cachedCape = capeCache.get(getProfile().getId());
                if (cachedCape != null) {
                    setCapeAvailable(true);
                    setCapeTexture(cachedCape);
                    LOGGER.info("Loaded cached cape for: " + getProfile().getName());
                } else {
                    capeExecutor.submit(() -> CapeUtil.getCapeByProfile(getProfile()).ifPresentOrElse(capeImage -> {
                        setCapeAvailable(true);

                        Minecraft.getInstance().execute(() -> {
                            var capeTexture = Minecraft.getInstance().getTextureManager().register(
                                    "optifine-cape-" + getProfile().getId(),
                                    new DynamicTexture(capeImage)
                            );

                            setCapeTexture(capeTexture);
                            capeCache.put(getProfile().getId(), capeTexture);
                            LOGGER.info("Got cape of: " + getProfile().getName());
                        });
                    }, () -> setCapeAvailable(false)));
                }
            }
        });
    }
}
