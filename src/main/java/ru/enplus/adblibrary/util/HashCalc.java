package ru.enplus.adblibrary.util;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for calculating cryptographic hashes of files.
 */
public class HashCalc {

    /**
     * Computes the hash of a file using the specified hash algorithm.
     *
     * @param grabbedApplication the path to the file for which the hash is to be computed
     * @param hashAlgorithm the algorithm to be used for hashing (e.g., SHA-1, SHA-256, SHA-512)
     * @return the computed hash as a hexadecimal string
     * @throws IOException if an I/O error occurs reading from the file
     * @throws NoSuchAlgorithmException if the specified hash algorithm is not available
     */
    private static String findHashForFile(String grabbedApplication, String hashAlgorithm) throws IOException, NoSuchAlgorithmException {
        Path filePath = Path.of(grabbedApplication);
        byte[] data = Files.readAllBytes(Paths.get(filePath.toUri()));
        byte[] hash = MessageDigest.getInstance(hashAlgorithm).digest(data);
        return new BigInteger(1, hash).toString(16);
    }

    /**
     * Computes the SHA-256 hash of a file.
     *
     * @param grabbedApplication the path to the file for which the SHA-256 hash is to be computed
     * @return the computed SHA-256 hash as a hexadecimal string
     * @throws NoSuchAlgorithmException if SHA-256 algorithm is not available
     * @throws IOException if an I/O error occurs reading from the file
     */
    public static String calculateSha256Hash(String grabbedApplication) throws NoSuchAlgorithmException, IOException {
        return findHashForFile(grabbedApplication, "SHA-256");
    }

    /**
     * Computes the SHA-1 hash of a file.
     *
     * @param grabbedApplication the path to the file for which the SHA-1 hash is to be computed
     * @return the computed SHA-1 hash as a hexadecimal string
     * @throws NoSuchAlgorithmException if SHA-1 algorithm is not available
     * @throws IOException if an I/O error occurs reading from the file
     */
    public static String calculateSha1Hash(String grabbedApplication) throws NoSuchAlgorithmException, IOException {
        return findHashForFile(grabbedApplication, "SHA-1");
    }

    /**
     * Computes the SHA-512 hash of a file.
     *
     * @param grabbedApplication the path to the file for which the SHA-512 hash is to be computed
     * @return the computed SHA-512 hash as a hexadecimal string
     * @throws NoSuchAlgorithmException if SHA-512 algorithm is not available
     * @throws IOException if an I/O error occurs reading from the file
     */
    public static String calculateSha512Hash(String grabbedApplication) throws NoSuchAlgorithmException, IOException {
        return findHashForFile(grabbedApplication, "SHA-512");
    }
}
