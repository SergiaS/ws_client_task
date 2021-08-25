package com.wsclient;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.*;

public final class FileSecurityUtil {

    public static final String ALGORITHM = "AES";
    public static final byte[] KEY_VALUE = new byte[]{'0','2','3','4','5','6','7','9','8','2','3','4','6','7','9','8'};

    public static void main(String[] args) {
//        encryptFile("20210824-195725_2.txt");
//        decryptFile("20210824-195725_2_enc2.txt");

//        gzipFile("20210822-151456_2.txt");
//        ungzipFileToFile("comp_gzip.gz");
//        ungzipFileToConsole("comp_gzip.gz");

        decompressAndDecrypt("20210824-195725.txt");
    }

    private static void decompressAndDecrypt(String filename) {
        Cipher cipher = createCipher(false);
        try (FileInputStream fis = new FileInputStream(filename);
             CipherInputStream cis = new CipherInputStream(fis, cipher);
             GZIPInputStream gis = new GZIPInputStream(cis);
        ) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));
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

    private static void encryptFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename));) {
            Cipher cipher = createCipher(true);
            CipherOutputStream cipherOut = new CipherOutputStream(new FileOutputStream(filename.replace(".txt", "_enc2.txt")), cipher);

            String line;
            while ((line = reader.readLine()) != null) {
                cipherOut.write((line + "\n").getBytes());
            }
            cipherOut.flush();
            cipherOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void decryptFile(String filename) {
        Cipher cipherInstance = createCipher(false);
        try (FileInputStream fis = new FileInputStream(filename);
             CipherInputStream in = new CipherInputStream(fis, cipherInstance);
             Reader reader = new InputStreamReader(in);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

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

    private static void gzipFile(String filename) {
        try (FileInputStream fis = new FileInputStream(filename);
             FileOutputStream fos = new FileOutputStream("comp_gzip.gz");
             GZIPOutputStream gos = new GZIPOutputStream(fos);) {

            byte[] buffer = new byte[1024];
            for (int len; (len = fis.read(buffer)) != -1; ) {
                gos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ungzipFileToFile(String filename) {
        try (FileInputStream fis = new FileInputStream(filename);
             GZIPInputStream gis = new GZIPInputStream(fis);
             FileOutputStream fos = new FileOutputStream("uncomp_gzip.txt");) {

            byte[] bytes = new byte[1024];
            for (int len; (len = gis.read(bytes)) > 0; ) {
                fos.write(bytes, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ungzipFileToConsole(String filename) {
        try (FileInputStream fis = new FileInputStream(filename);
             GZIPInputStream gis = new GZIPInputStream(fis);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));) {

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

    private static void compressFileByZip(String sourceFile) throws IOException {
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

    private static void decompressFileByZip(String filename) throws IOException {
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

    public static Cipher createCipher(boolean isNeedEncrypt) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            SecretKey secretKey = new SecretKeySpec(KEY_VALUE, ALGORITHM);
            cipher.init(isNeedEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secretKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return cipher;
    }

}
