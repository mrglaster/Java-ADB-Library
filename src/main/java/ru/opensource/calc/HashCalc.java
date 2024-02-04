package ru.opensource.calc;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashCalc {
    private static String findHashForFile(String grabbedApplication, String hashAlgorithm) throws IOException, NoSuchAlgorithmException {
        Path filePath = Path.of(grabbedApplication);
        byte[] data = Files.readAllBytes(Paths.get(filePath.toUri()));
        byte[] hash = MessageDigest.getInstance(hashAlgorithm).digest(data);
        return new BigInteger(1, hash).toString(16);

    }
    public static String calculateSha256Hash(String grabbedApplication) throws NoSuchAlgorithmException, IOException {
      return findHashForFile(grabbedApplication, "SHA-256");
    }

    public static String calculateSha1Hash(String grabbedApplication)  throws NoSuchAlgorithmException, IOException  {
        return findHashForFile(grabbedApplication, "SHA-1");
    }

    public static String calculateSha512Hash(String grabbedApplication) throws NoSuchAlgorithmException, IOException{
        return findHashForFile(grabbedApplication, "SHA-512");
    }
}
