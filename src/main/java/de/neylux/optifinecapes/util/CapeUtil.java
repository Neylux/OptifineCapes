package de.neylux.optifinecapes.util;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CapeUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Optional<NativeImage> getCapeByProfile(@NotNull GameProfile profile) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request;

        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI("http", "s.optifine.net", "/capes/" + profile.getName() + ".png", null))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                    .build();
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to retrieve cape for uuid: " + profile.getId(), e);
            return Optional.empty();
        }

        CompletableFuture<NativeImage> futureCapeImage = client.sendAsync(request, BodyHandlers.ofInputStream())
                .thenApply(response -> response.statusCode() == 200 ? response.body() : null)
                .thenApply(inputStream -> {
                    if (inputStream != null) {
                        try {
                            return resizeCape(NativeImage.read(inputStream));
                        } catch (IOException e) {
                            LOGGER.error("Failed to resize cape texture for uuid: " + profile.getId(), e);
                            return null;
                        }
                    }
                    return null;
                }).exceptionally(e -> {
                    LOGGER.error("Unexpected error occurred when fetching cape for uuid: " + profile.getId(), e);
                    return null;
                });

        return Optional.ofNullable(futureCapeImage.join());
    }

    /**
     * Default optifine capes have a size of 46px x 22px;
     * "On load, a canvas is initialized with a size of 64px x 32px.
     * If the supplied cape image is larger in width or height, it doubles the dimensions of the canvas repeatedly until it is large enough to fit the original image."
     *
     * @param image the original native image with the optifine cape texture
     * @return the resized native image
     * @see <a href="https://optifine.readthedocs.io/capes.html#locking">Optifine Docs</a>
     */
    private static @NotNull NativeImage resizeCape(@NotNull NativeImage image) {
        int imageWidth = 64;
        int imageHeight = 32;
        int imageSrcWidth = image.getWidth();
        int imageSrcHeight = image.getHeight();

        // Invalid image sizes
        if (imageSrcWidth <= 0 || imageSrcHeight <= 0) {
            return image;
        }

        while (imageWidth < imageSrcWidth || imageHeight < imageSrcHeight) {
            imageWidth *= 2;
            imageHeight *= 2;
        }

        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < imageSrcWidth; x++) {
            for (int y = 0; y < imageSrcHeight; y++) {
                imgNew.setPixelRGBA(x, y, image.getPixelRGBA(x, y));
            }
        }
        image.close();
        return imgNew;
    }
}
