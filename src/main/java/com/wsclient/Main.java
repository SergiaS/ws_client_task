package com.wsclient;

import com.wsclient.model.RequestCommand;
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

// SUBSCRIBE;aggTrade;btcusdt
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

            simulateMessageSender(clientEndpoint);

            enterCommandAndSendMessage(reader, clientEndpoint);

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void enterCommandAndSendMessage(BufferedReader reader, WebSocketClientEndpoint clientEndpoint) throws IOException {
        String command;
        while (!(command = reader.readLine()).equals("stop")) {
            RequestCommand requestCommand;
            if (command.contains("SUBSCRIBE")) {
                String[] splitCommands = command.split(";");
                long count = Arrays.stream(splitCommands)
                        .filter(Main::commandValidation).count();
                if (count != 3) {
                    wrongInput();
                    continue;
                }
                requestCommand = new RequestCommand(splitCommands[0], splitCommands[1], splitCommands[2].toLowerCase());
                System.out.println("You have " + requestCommand.getAction());
            } else {
                wrongInput();
                continue;
            }

            String jsonObj = "" +
                    "{\n" +
                    "\"method\": \"" + requestCommand.getAction() + "\",\n" +
                    "\"params\":\n" +
                    "[\n" +
                    "\"" + requestCommand.getSymbol() + "@" + requestCommand.getChannel() + "\"\n" +
                    "],\n" +
                    "\"id\": " + (requestCommand.getAction().equalsIgnoreCase("SUBSCRIBE") ? 1 : 3) + "\n" +
                    "}";
            clientEndpoint.sendMessage(jsonObj);
        }
    }

    private static void simulateMessageSender(WebSocketClientEndpoint clientEndpoint) throws IOException {
        String json = "{\n" +
                "\"method\": \"SUBSCRIBE\",\n" +
                "\"params\":\n" +
                "[\n" +
                "\"btcusdt@aggTrade\"\n" +
                "],\n" +
                "\"id\": 1\n" +
                "}";

        clientEndpoint.sendMessage(json);
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
