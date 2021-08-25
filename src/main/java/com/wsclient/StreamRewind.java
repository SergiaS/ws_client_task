package com.wsclient;

import org.tukaani.xz.XZInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

/**
 * This is fake example which will rewind I/O stream
 */
public class StreamRewind {

    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("comp_gzip.gz");
             BufferedInputStream bis = new BufferedInputStream(fis);) {

            // Mark() is set on the input stream
            System.out.println("mark() called");
            bis.mark(1);

            // trying to decompress file by XZ algorithm
            System.out.println("Decompress file by XZ algorithm...");
            try (XZInputStream xzis = new XZInputStream(bis);) {
                // do to something if it's XZ algorithm
            } catch (IOException e) {
                // prints XZ error - Input is not in the XZ format
                // because this is GZIP algorithm, not XZ
                System.out.println("It isn't XZ algorithm!");
                e.printStackTrace();

                // reset() is invoked
                bis.reset();
                Thread.sleep(1000);
                System.out.println("Decompress file by GZIP algorithm...");
                try (GZIPInputStream gis = new GZIPInputStream(bis);
                     BufferedReader br = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));) {

                    // append result
                    StringBuilder res = new StringBuilder();
                    String sTmp;
                    while ((sTmp = br.readLine()) != null) {
                        res.append(sTmp).append("\n");
                    }

                    // print the result
                    System.out.println(res);
                } catch (Exception e1) {
                    System.out.println("It isn't GZIP algorithm!");
                    e1.printStackTrace();
                }
            }
            System.out.println("OK");
        } catch (Exception e) {
            System.out.println("Last CATCH");
            e.printStackTrace();
        }
    }
}
