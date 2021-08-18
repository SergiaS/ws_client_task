package com.wsclient;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileSecurityUtil {

    private static final String ALGORITHM = "AES";
    private static final byte[] KEY_VALUE = new byte[]{'0','2','3','4','5','6','7','9','8','2','3','4','6','7','9','8'};

    public static void encryptFile(String filename) {
        try {
            Key key = new SecretKeySpec(KEY_VALUE, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            CipherOutputStream out = new CipherOutputStream(new FileOutputStream(filename), cipher);
            BufferedReader reader = new BufferedReader(new FileReader("20210817-202601.txt"));

            String line;
            while ((line = reader.readLine()) != null) {
                out.write(line.getBytes());
            }
            out.flush();
            out.close();
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static void decryptFileData(String filename) {
        try {
            Key key = new SecretKeySpec(KEY_VALUE, ALGORITHM);
            Cipher aes = Cipher.getInstance(ALGORITHM);
            aes.init(Cipher.DECRYPT_MODE, key);

            FileInputStream fis = new FileInputStream(filename);
            CipherInputStream in = new CipherInputStream(fis, aes);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];
            int numberOfByteRead;

            while ((numberOfByteRead = in.read(b)) >= 0) {
                baos.write(b, 0, numberOfByteRead);
            }
            System.out.println(baos);
        } catch (NoSuchPaddingException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static void compressFile(String sourceFile) throws IOException {
        FileOutputStream fos = new FileOutputStream("compressed.txt");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
    }

    public static void decompressFileData(String filename) throws IOException {
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
}
