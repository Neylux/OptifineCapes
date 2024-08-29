package de.neylux.optifinecapes.util;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

public class CapeUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static NativeImage getCapeByProfile(GameProfile profile) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http", "s.optifine.net", "/capes/" + profile.getName() + ".png", null))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                    .build();

            CompletableFuture<NativeImage> futureCapeImage = client.sendAsync(request, BodyHandlers.ofInputStream())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            return response.body();
                        } else {
                            return null;
                        }
                    })
                    .thenApply(inputStream -> {
                        if (inputStream != null) {
                            try {
                                return parseCape(NativeImage.read(inputStream));
                            } catch (IOException e) {
                                LOGGER.error("Inputstream not available for" + profile.getId() + " while fetching cape.");
                            }
                        }
                        return null;
                    });

            // Wait for the asynchronous operation to complete and return the result
            return futureCapeImage.join();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static NativeImage parseCape(NativeImage image) {
        int imageWidth = 64;
        int imageHeight = 32;
        int imageSrcWidth = image.getWidth();
        int srcHeight = image.getHeight();

        for (int imageSrcHeight = image.getHeight(); imageWidth < imageSrcWidth
                || imageHeight < imageSrcHeight; imageHeight *= 2) {
            imageWidth *= 2;
        }

        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < imageSrcWidth; x++) {
            for (int y = 0; y < srcHeight; y++) {
                imgNew.setPixelRGBA(x, y, image.getPixelRGBA(x, y));
            }
        }
        image.close();
        return imgNew;
    }
}
