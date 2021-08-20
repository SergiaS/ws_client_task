package com.wsclient;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class FileSecurityUtil {

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_INSTANCE = "AES/ECB/PKCS5Padding";
    private static final byte[] KEY_VALUE = new byte[]{'0','2','3','4','5','6','7','9','8','2','3','4','6','7','9','8'};

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
//        encryptFile("20210820-105840_2.txt");
        decryptFileDataByBytes("20210820-120738.txt");
//        System.out.println("\n\n");
//        decryptFileDataByLine("20210820-100930.txt");
//        compressFile(sourceFile);
//        decompressFileData("compressed.txt");
    }

    private static void encryptFile(String filename) {
        try {
            Key key = new SecretKeySpec(KEY_VALUE, ALGORITHM);
            Cipher cipher = Cipher.getInstance(FileSecurityUtil.CIPHER_INSTANCE);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            CipherOutputStream cipherOut = new CipherOutputStream(new FileOutputStream(filename.replace(".txt","_enc2.txt")), cipher);
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            String line;
            while ((line = reader.readLine()) != null) {
                cipherOut.write((line + "\n").getBytes());
            }
            cipherOut.flush();
            cipherOut.close();
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    private static void decryptFileDataByBytes(String filename) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Key key = new SecretKeySpec(KEY_VALUE, ALGORITHM);
        Cipher aes = Cipher.getInstance(FileSecurityUtil.CIPHER_INSTANCE);
        aes.init(Cipher.DECRYPT_MODE, key);
        try (FileInputStream fis = new FileInputStream(filename);
             CipherInputStream in = new CipherInputStream(fis, aes);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] b = new byte[1024];
            int numberOfByteRead;

            while ((numberOfByteRead = in.read(b)) >= 0) {
                baos.write(b, 0, numberOfByteRead);
                System.out.println(baos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void decryptFileDataByLine(String filename) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Key key = new SecretKeySpec(KEY_VALUE, ALGORITHM);
        Cipher cipherInstance = Cipher.getInstance(FileSecurityUtil.CIPHER_INSTANCE);
        cipherInstance.init(Cipher.DECRYPT_MODE, key);
        try (
            FileInputStream fis = new FileInputStream(filename);

            CipherInputStream in = new CipherInputStream(fis, cipherInstance);
            Reader reader = new InputStreamReader(in);

        ) {
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuilder res = new StringBuilder();
            String sTmp;
            while ((sTmp = bufferedReader.readLine()) != null) {
                res.append(sTmp).append("\n");
            }
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compressFile(String sourceFile) throws IOException {
        FileOutputStream fos = new FileOutputStream("compressed.txt");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
    }

    private static void decompressFileData(String filename) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(filename));
        ZipEntry zipEntry = zis.getNextEntry();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (zipEntry != null) {
            int len;
            while ((len = zis.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            zipEntry = zis.getNextEntry();
        }
        System.out.println(baos);
    }

    private byte[] encryptMessage(byte[] message, byte[] keyBytes)
            throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(message);
    }
}
