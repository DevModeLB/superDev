package com.devmode.superdev.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncUtils {

    // Fetch unsynced records from a specific table
    public static List<Map<String, Object>> fetchUnsyncedRecords(Connection sqliteConnection, String tableName) throws SQLException {
        List<Map<String, Object>> unsyncedRecords = new ArrayList<>();
        String query = "SELECT * FROM " + tableName + " WHERE synced = 0";
        try (Statement stmt = sqliteConnection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    row.put(rsmd.getColumnName(i), rs.getObject(i));
                }
                unsyncedRecords.add(row);
            }
        }
        return unsyncedRecords;
    }

    public static void syncRecordsToMySQL(Connection mysqlConnection, Connection sqliteConnection, String tableName, List<Map<String, Object>> records) throws SQLException {
        for (Map<String, Object> record : records) {
            // Construct the SQL query for insert or update in MySQL
            StringBuilder sql = new StringBuilder("INSERT INTO ");
            sql.append(tableName).append(" (");

            StringBuilder placeholders = new StringBuilder();
            StringBuilder updateClause = new StringBuilder();

            for (String key : record.keySet()) {
                if (placeholders.length() > 0) {
                    sql.append(", ");
                    placeholders.append(", ");
                    updateClause.append(", ");
                }
                sql.append(key);
                placeholders.append("?");
                updateClause.append(key).append(" = VALUES(").append(key).append(")");
            }

            sql.append(") VALUES (").append(placeholders).append(") ");
            sql.append("ON DUPLICATE KEY UPDATE ").append(updateClause);

            // Prepare the statement for MySQL
            try (PreparedStatement pstmt = mysqlConnection.prepareStatement(sql.toString())) {
                int index = 1;
                for (String key : record.keySet()) {
                    pstmt.setObject(index++, record.get(key));
                }

                // Execute the update or insert in MySQL
                pstmt.executeUpdate();

                // After successful sync, update the `synced` status in SQLite
                updateSyncedStatusInSQLite(sqliteConnection, tableName, record.get("id"));
            }
        }
    }

    private static void updateSyncedStatusInSQLite(Connection sqliteConnection, String tableName, Object recordId) throws SQLException {
        String sql = "UPDATE " + tableName + " SET synced = 1 WHERE id = ?";

        try (PreparedStatement pstmt = sqliteConnection.prepareStatement(sql)) {
            pstmt.setObject(1, recordId);
            pstmt.executeUpdate();
        }
    }



    // Get record IDs from the list of records
    public static List<Integer> getRecordIds(List<Map<String, Object>> records) {
        List<Integer> recordIds = new ArrayList<>();
        for (Map<String, Object> record : records) {
            recordIds.add((Integer) record.get("id"));
        }
        return recordIds;
    }

    // Mark records as synced in the SQLite database
    public static void markRecordsAsSynced(Connection sqliteConnection, String tableName, List<Integer> recordIds) throws SQLException {
        String updateQuery = "UPDATE " + tableName + " SET synced = 1 WHERE id = ?";
        try (PreparedStatement pstmt = sqliteConnection.prepareStatement(updateQuery)) {
            for (int id : recordIds) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        }
    }
}
