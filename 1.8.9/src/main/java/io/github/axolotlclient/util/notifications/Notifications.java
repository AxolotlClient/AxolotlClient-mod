package io.github.axolotlclient.util.notifications;

import io.github.axolotlclient.util.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Notifications {

    // ---------------------- Basic Notification System because 1.8.9 has none by itself (except Achievements which only work in worlds) --------------------------

    @Getter
    private static final Notifications Instance = new Notifications();
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final List<Status> statusQueue = new ArrayList<>();
    private Status currentStatus;
    private long statusCreationTime;
    private int lastX;
    private boolean fading;

    public void addStatus(String title, String description) {
        if (statusQueue.isEmpty() && currentStatus == null) {
            setStatus(new Status(title, description));
        } else {
            statusQueue.add(new Status(title, description));
        }
    }

    private void setStatus(Status status) {
        currentStatus = status;
        statusCreationTime = MinecraftClient.getTime();
        lastX = Util.getWindow().getWidth();
        fading = false;
        client.getSoundManager().play(new PositionedSoundInstance(new Identifier("random.bow"), 0.5F, 0.4F / (0.5F + 0.8F), 1, 0, 0));
    }

    public void renderStatus() {
        if (currentStatus != null) {
            int width = client.textRenderer.getStringWidth(currentStatus.getDescription());
            int x = lastX;
            x -= width + 5;
            if (MinecraftClient.getTime() - statusCreationTime < 100) {
                lastX -= lastX / 45;
            } else if (MinecraftClient.getTime() - statusCreationTime > 2000) {
                if (!fading) {
                    client.getSoundManager().play(new PositionedSoundInstance(new Identifier("random.bow"), 0.5F, 0.4F / (0.5F + 0.8F), 1, 0, 0));
                }
                fading = true;
                lastX += lastX / 40;
            }
            DrawableHelper.fill(x-5-1, 0, Util.getWindow().getWidth(), 30+1, 0xFF0055FF);
            DrawableHelper.fill(x - 5, 0, Util.getWindow().getWidth(), 30, 0xFF00CFFF);
            client.textRenderer.draw(currentStatus.getTitle(), x, 5, -1, true);
            client.textRenderer.draw(currentStatus.getDescription(), x+2, 17, 0xFFCCCCCC, true);
            if (x > Util.getWindow().getWidth() + 20) {
                currentStatus = null;
                if (!statusQueue.isEmpty()) {
                    Status s = statusQueue.get(0);
                    statusQueue.remove(0);
                    setStatus(s);
                }
            }
        }
    }

    @RequiredArgsConstructor
    private static class Status {
        @Getter
        private final String title, description;
    }
}

