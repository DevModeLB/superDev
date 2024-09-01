package com.devmode.superdev.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkUtils {

    private static final String TEST_URL = "8.8.8.8"; // Google's public DNS server
    private static final int TEST_PORT = 53; // DNS port

    /**
     * Checks if the internet connection is available.
     * @return true if connected to the internet, false otherwise.
     */
    public static boolean isInternetAvailable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(TEST_URL, TEST_PORT), 1000); // Timeout of 1 second
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
