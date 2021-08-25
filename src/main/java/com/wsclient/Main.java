package com.wsclient;

import com.google.gson.Gson;
import com.wsclient.model.JsonStreamRequest;
import com.wsclient.websocket.WebSocketClientEndpoint;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

/**
 * With manual method use type to your console command like SUBSCRIBE;aggTrade;btcusdt
 */
public class Main {
    private final static String baseEndpoint = "wss://stream.binance.com:9443/ws";
    private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public static void main(String[] args) {

        String filename = dtf.format(LocalDateTime.now()) + ".txt";

        Cipher cipher = FileSecurityUtil.createCipher(true);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
             BufferedWriter writer = new BufferedWriter(new FileWriter(filename.replace(".txt", "_2.txt")));
             CipherOutputStream cipherOut = new CipherOutputStream(new FileOutputStream(filename), cipher);
             GZIPOutputStream gzip = new GZIPOutputStream(cipherOut);
        ) {
            WebSocketClientEndpoint clientEndpoint = new WebSocketClientEndpoint(new URI(baseEndpoint));

            clientEndpoint.addMessageHandler(new WebSocketClientEndpoint.MessageHandler() {
                @Override
                public void handleMessage(String message) {
                    try {
                        // encrypt and compress data
                        gzip.write((message + "\n").getBytes());

                        // raw data with prefix '_2'
                        writer.write(message + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            autoMessageSender(clientEndpoint);
//            manualMessageSender(reader, clientEndpoint);

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void manualMessageSender(BufferedReader reader, WebSocketClientEndpoint clientEndpoint) throws IOException {
        JsonStreamRequest jsonStrReq;
        String command;
        while (!(command = reader.readLine()).equals("stop")) {
            String method;
            String[] param = new String[1];
            if (command.contains("SUBSCRIBE")) {
                String[] splitCommands = command.split(";");
                long count = Arrays.stream(splitCommands)
                        .filter(Main::commandValidation).count();
                if (count != 3) {
                    wrongInput();
                    continue;
                }

                method = splitCommands[0];
                param[0] = splitCommands[2].toLowerCase() + "@" + splitCommands[1];

                System.out.println("You have " + method);
            } else {
                wrongInput();
                continue;
            }

            jsonStrReq = new JsonStreamRequest(method, param);
            Gson gson = new Gson();
            String message = gson.toJson(jsonStrReq);

            clientEndpoint.sendMessage(message);
        }
    }

    // just for test that it works
    private static void autoMessageSender(WebSocketClientEndpoint clientEndpoint) throws IOException {
        clientEndpoint.sendMessage(
                new Gson().toJson(JsonStreamRequest.sample()));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean commandValidation(String command) {
        return command.length() > 0;
    }

    private static void wrongInput() {
        System.out.println("Unknown command! Try again");
    }

}
