package com.devmode.superdev.Controllers;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.devmode.superdev.DatabaseManager;
import com.devmode.superdev.models.LicenseResponse;
import com.devmode.superdev.utils.ErrorDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import okhttp3.*;

public class LicenseValidator {

    private static final String API_URL = "https://devmodeapi.onrender.com/liscences/api/validate"; // Replace with your actual API URL



    public static LicenseResponse validateLicense(String licenseKey) {
        try {
            // Create JSON object for the request body
            JsonObject jsonInput = new JsonObject();
            jsonInput.addProperty("liscence", licenseKey);
            String requestBody1 = jsonInput.toString();
            System.out.println("Request body: " + requestBody1);

            // Create OkHttpClient
            OkHttpClient client = new OkHttpClient();

            // Create RequestBody
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody1);

            // Create Request
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .build();

            // Send request and receive Response
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            // Print response for debugging
            System.out.println("Response Code: " + response.code());
            System.out.println("Response Body: " + responseBody);

            // Parse the response JSON
            Gson gson = new Gson();
            return gson.fromJson(responseBody, LicenseResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void storeLicense(String licenseKey, String expiresAt) {
        String sql = "INSERT INTO license(licenseKey, expiresAt) VALUES(?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, licenseKey);
            pstmt.setString(2, expiresAt);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Check if the stored license is still valid
    public static boolean isLicenseValid() {
        String sql = "SELECT expiresAt FROM license LIMIT 1";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String expiresAt = rs.getString("expiresAt");
                String datePart = expiresAt.substring(0, 10); // Extract "yyyy-MM-dd"
                LocalDate expiryDate = LocalDate.parse(datePart, DateTimeFormatter.ISO_DATE);
                if(!expiryDate.isAfter(LocalDate.now())){
                    new ErrorDialog().showErrorDialog("Expired Licence", "Error");
                    return false;
                }
                return true;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}