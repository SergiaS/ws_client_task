package com.wsclient.websocket;

import com.wsclient.model.RequestCommand;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

// SUBSCRIBE;aggTrade;btcusdt
public class WSStart {

    private final static String baseEndpoint = "wss://stream.binance.com:9443/ws";
    private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
             BufferedWriter writer = new BufferedWriter(new FileWriter(dtf.format(LocalDateTime.now()) + ".txt"));
        ) {
            WebSocketClientEndpoint clientEndpoint = new WebSocketClientEndpoint(new URI(baseEndpoint));
            clientEndpoint.addMessageHandler(new WebSocketClientEndpoint.MessageHandler() {
                @Override
                public void handleMessage(String message) {
                    try {
                        writer.write(message);
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    System.out.println(message);
                }
            });

            String command;
            while (!(command = reader.readLine()).equals("stop")) {

                RequestCommand requestCommand;
                if (command.contains("SUBSCRIBE")) {
                    String[] splitCommands = command.split(";");
                    long count = Arrays.stream(splitCommands).filter(WSStart::commandValidation).count();
                    if (count != 3) {
                        wrongInput();
                        continue;
                    }
                    requestCommand = new RequestCommand(splitCommands[0], splitCommands[1], splitCommands[2].toLowerCase());
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
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
