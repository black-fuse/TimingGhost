package com.tekad.timingghost.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimingghostClient implements ClientModInitializer {
    private GhostRecorder recorder = new GhostRecorder();
    private boolean recording = false;
    private boolean recordActive = true;
    private boolean debugMode = true;
    public static Logger LOGGER = LoggerFactory.getLogger("TimingGhost");

    @Override
    public void onInitializeClient() {
        System.out.println("[TimingGhost] initialised");




        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, parameters, timestamp) -> {
            if (debugMode){
                System.out.println("[TimingGhost] chat message recieved");
            }

            String text = message.getString().strip();

            if (debugMode){
                System.out.println("[TimingGhost] [CHAT] Received: " + text);
            }

            // Regex patterns
            Pattern pattern1 = Pattern.compile("New best time on ([^:]+):");
            Pattern pattern2 = Pattern.compile("Personal best! You Finished ([^ ]+) in");
            Pattern pattern3 = Pattern.compile("You finished ([^ ]+) in");

            Matcher matcher1 = pattern1.matcher(text);
            Matcher matcher2 = pattern2.matcher(text);
            Matcher matcher3 = pattern3.matcher(text);

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

        ClientReceiveMessageEvents.CHAT.register((msg, sig, sender, params, time) -> {
            LOGGER.info("CHAT: " + msg.getString());
        });

        ClientReceiveMessageEvents.GAME.register((msg, overlay) -> {
            LOGGER.info("GAME: " + msg.getString() + " (overlay=" + overlay + ")");
        });

        ClientReceiveMessageEvents.MODIFY_GAME.register((msg, overlay) -> {
            LOGGER.info("MODIFY_GAME: " + msg.getString() + " (overlay=" + overlay + ")");
            return msg;
        });



        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (overlay) {
                String text = message.getString();
                if (debugMode){
                    System.out.println("[TimingGhost] [ACTIONBAR] " + text);
                }

                if (text.matches("^\\d{2}:\\d{3}$") || text.equals("00:000")) {
                    if (!recording && recordActive) {
                        recording = true;
                        recorder.startRecording();
                        System.out.println("[TimingGhost] Timer started via action bar: " + text);
                    }
                }
            }
        });


    }
}
