package de.neylux.optifinecapes;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
import java.util.concurrent.TimeUnit;

public class PlayerCapeManager {
    private final HashMap<UUID, PlayerCapeHandler> capeHandlers = new HashMap<>();
    private final Cache<UUID, ResourceLocation> capeCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();
    private final ExecutorService capeExecutor = Executors.newFixedThreadPool(4);
    private static final Logger LOGGER = LogUtils.getLogger();

    public PlayerCapeHandler getCapeHandler(GameProfile profile) {
        return capeHandlers.computeIfAbsent(profile.getId(), uuid -> new PlayerCapeHandler(profile) {
            @Override
            public void fetchCape() {
                try {
                    ResourceLocation capeTexture = capeCache.get(getProfile().getId(), () -> capeExecutor.submit(
                            () -> CapeUtil.getCapeByProfile(getProfile())
                                    .map(capeImage -> Minecraft.getInstance().getTextureManager().register(
                                            "optifine-cape-" + getProfile().getId(),
                                            new DynamicTexture(capeImage)
                                    ))
                                    .orElseThrow()
                    ).get());

                    setCapeAvailable(true);
                    setCapeTexture(capeTexture);
                    LOGGER.info("Loaded cape for: " + getProfile().getName());
                } catch (Exception e) {
                    LOGGER.error("Failed to get cape for: " + getProfile().getName(), e);
                    setCapeAvailable(false);
                }
            }
        });
    }
}
