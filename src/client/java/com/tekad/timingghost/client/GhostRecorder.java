package com.tekad.timingghost.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GhostRecorder {
    private final List<String> frameLog = new ArrayList<>();
    private boolean ticking = false;
    private int tickCounter = 0;

    public void startRecording(){
        frameLog.clear();
        ticking = true;
        tickCounter = 0;
        System.out.println("[TimingGhost] recording started . . .");
    }

    public void stopAndSave(String trackName){
        ticking = false;

        try{
            FileWriter writer  = new FileWriter("timingGhost/" + trackName + "_ghost.json");
            writer.write("[\n");
            for (int i = 0; i < frameLog.size(); i++){
                writer.write("  "  + frameLog.get(i));
                if (i < frameLog.size() - 1){
                    writer.write(",");
                }
                writer.write("\n");
            }
            writer.write("]\n");
            writer.close();
            System.out.println("[TimingGhost] saved time for track: " + trackName);
        } catch (IOException e){
            System.err.println("[TimingGhost] wtf have you done, ghost failed to save: " + e.getMessage());
        }
    }

    public void tick(){
        if (!ticking){
            return;
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null){
            return;
        }

        String entry = String.format(
                "{ \"tick\": %d, \"x\": %.3f, \"y\": %.3f, \"z\": %.3f, \"yaw\": %.2f, \"pitch\": %.2f }",
                tickCounter++, player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch()
        );

        frameLog.add(entry);
    }
}
