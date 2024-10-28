package com.eternity.jessemood.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class guihandler {
    private static final int NUMBEROFIMAGES = 5;
    private static final int DISPLAY_DURATION = 500;
    private static long startTime = -1;
    private static long lastTime = -1;
    private static Identifier image_id = Identifier.of("eternaljesus", "textures/gui/jesus0.png");
    private static List<Identifier> images = new ArrayList<>();
    private static List<SoundEvent> sounds = new ArrayList<>();
    private static final Random RANDOM = new Random();

    public static void init() {
        // Load PNG images
        for (int i = 0; i < NUMBEROFIMAGES; i++) {
            images.add(Identifier.of("eternaljesus", "textures/gui/jesus" + i + ".png"));
        }
        
        // Placeholder for GIF frames, if applicable
        // images.add(Identifier.of("eternaljesus", "textures/gui/jesus_frame1.png"));
        // images.add(Identifier.of("eternaljesus", "textures/gui/jesus_frame2.png"));
        // Add more frames if you have split GIFs into individual images
        
        // Load sound events into the sounds list
        sounds.add(JessemoodClient.JESUS_BELL_SOUND);
        sounds.add(JessemoodClient.ANOTHER_SOUND); // Replace with actual sound event
        sounds.add(JessemoodClient.YET_ANOTHER_SOUND); // Replace with actual sound event
    }

    private static int getRandomNumber(int min, int max) {
        return RANDOM.nextInt(max - min) + min;
    }

    private static Identifier getRandomImage() {
        return images.get(getRandomNumber(0, images.size()));
    }

    private static SoundEvent getRandomSound() {
        return sounds.get(getRandomNumber(0, sounds.size()));
    }

    public static void playLocalSound(PlayerEntity player) {
        SoundEvent soundEvent = getRandomSound();
        if (MinecraftClient.getInstance().world != null && soundEvent != null) {
            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().world.playSound(
                    player,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    soundEvent,
                    SoundCategory.MASTER,
                    30.0F,
                    1.0F
            ));
        }
    }

    private static void resetDisplay() {
        image_id = getRandomImage();
        startTime = System.currentTimeMillis();
        lastTime = System.currentTimeMillis();
        playLocalSound(MinecraftClient.getInstance().player);
    }

    public static void display() {
        if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
            if (lastTime == -1 || System.currentTimeMillis() - lastTime > DISPLAY_DURATION * 2) {
                resetDisplay();
            }
        }
    }

    public static void render(DrawContext context) {
        if (startTime < 0 || MinecraftClient.getInstance().world == null) {
            return;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime >= DISPLAY_DURATION) {
            startTime = -1;
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        float opacity = 1.0f - (float) elapsedTime / DISPLAY_DURATION;
        RenderSystem.setShaderTexture(0, image_id);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, opacity);

        context.drawTexture(image_id, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

        RenderSystem.disableBlend();
    }
}
