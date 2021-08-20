package com.wsclient;

import com.wsclient.model.RequestCommand;
import com.wsclient.websocket.WebSocketClientEndpoint;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

// SUBSCRIBE;aggTrade;btcusdt
public class TestApp {
    private final static String baseEndpoint = "wss://stream.binance.com:9443/ws";
    private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String ALGORITHM = "AES";
    private static final byte[] KEY_VALUE = new byte[]{'0','2','3','4','5','6','7','9','8','2','3','4','6','7','9','8'};

    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        String filename = dtf.format(LocalDateTime.now()) + ".txt";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
             BufferedWriter writer = new BufferedWriter(new FileWriter(dtf.format(LocalDateTime.now()) + "_2" + ".txt"));
        ) {
            WebSocketClientEndpoint clientEndpoint = new WebSocketClientEndpoint(new URI(baseEndpoint));

            Key key = new SecretKeySpec(KEY_VALUE, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            CipherOutputStream cipherOut = new CipherOutputStream(new FileOutputStream(filename), cipher);

            clientEndpoint.addMessageHandler(new WebSocketClientEndpoint.MessageHandler() {
                @Override
                public void handleMessage(String message) {
                    try {
                        cipherOut.write((message + "\n").getBytes());

                        writer.write(message + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    System.out.println(message);
                }
            });

//            String json = "{\n" +
//                    "\"method\": \"SUBSCRIBE\",\n" +
//                    "\"params\":\n" +
//                    "[\n" +
//                    "\"btcusdt@aggTrade\"\n" +
//                    "],\n" +
//                    "\"id\": 1\n" +
//                    "}";
//            clientEndpoint.sendMessage(json);
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            String command;
            while (!(command = reader.readLine()).equals("stop")) {
                RequestCommand requestCommand;
                if (command.contains("SUBSCRIBE")) {
                    String[] splitCommands = command.split(";");
                    long count = Arrays.stream(splitCommands)
                            .filter(TestApp::commandValidation).count();
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
            cipherOut.close();
        } catch (URISyntaxException | IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
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