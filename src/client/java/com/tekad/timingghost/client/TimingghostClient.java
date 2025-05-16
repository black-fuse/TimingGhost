package com.tekad.timingghost.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimingghostClient implements ClientModInitializer {
    private GhostRecorder recorder = new GhostRecorder();
    private boolean recording = false;
    private boolean recordActive = true;
    private boolean debugMode = true;

    @Override
    public void onInitializeClient() {
        System.out.println("[TimingGhost] initialised");

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (overlay) {
                String text = message.getString();
                System.out.println("[TimingGhost] [ACTIONBAR] " + text);

                if (text.matches("^\\d{2}:\\d{3}$") || text.equals("00:000")) {
                    if (!recording && recordActive) {
                        recording = true;
                        recorder.startRecording();
                        System.out.println("[TimingGhost] Timer started via action bar: " + text);
                    }
                }
            }
        });


        ClientReceiveMessageEvents.CHAT.register((message, signed, sender, parameters, receptionTimestamp) -> {
            String text = message.getString();
            System.out.println("[TimingGhost] [CHAT] Received: " + text);

            Matcher matcher1 = Pattern.compile("New best time on ([^:]+):").matcher(text);
            Matcher matcher2 = Pattern.compile("Personal best! You Finished ([^ ]+) in").matcher(text);
            Matcher matcher3 = Pattern.compile("You finished ([^ ]+) in").matcher(text);

            String track = null;
            if (matcher1.find()) {
                track = matcher1.group(1);
            } else if (matcher2.find()) {
                track = matcher2.group(1);
            } else if (matcher3.find()) {
                track = matcher3.group(1);
            }

            if (track != null && recording) {
                recording = false;
                recorder.stopAndSave(track);
                System.out.println("[TimingGhost] Finished recording: " + track);
            }
        });
    }
}
