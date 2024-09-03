package com.devmode.superdev.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncUtils {

    // Fetch unsynced records that are not deleted from a specific table
    public static List<Map<String, Object>> fetchUnsyncedNonDeletedRecords(Connection sqliteConnection, String tableName) throws SQLException {
        List<Map<String, Object>> unsyncedRecords = new ArrayList<>();
        String query = "SELECT * FROM " + tableName + " WHERE synced = 0 AND deleted = 0";
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

    // Fetch unsynced records that are marked as deleted from a specific table
    public static List<Map<String, Object>> fetchUnsyncedDeletedRecords(Connection sqliteConnection, String tableName) throws SQLException {
        List<Map<String, Object>> deletedRecords = new ArrayList<>();
        String query = "SELECT * FROM " + tableName + " WHERE synced = 0 AND deleted = 1";
        try (Statement stmt = sqliteConnection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    row.put(rsmd.getColumnName(i), rs.getObject(i));
                }
                deletedRecords.add(row);
            }
        }
        return deletedRecords;
    }
    public static List<Map<String, Object>> fetchSyncedAndDeletedRecords(Connection sqliteConnection, String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName + " WHERE synced = 1 AND deleted = 1";
        List<Map<String, Object>> records = new ArrayList<>();

        try (Statement stmt = sqliteConnection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                records.add(row);
            }
            System.out.println(records.isEmpty());
        }
        return records;
    }

    public static void syncRecordsToMySQL(Connection mysqlConnection, Connection sqliteConnection, String tableName, List<Map<String, Object>> records) throws SQLException {
        // Escape tableName if it's a reserved keyword or has special characters
        String escapedTableName = "`" + tableName + "`";

        for (Map<String, Object> record : records) {
            // Construct the SQL query for insert or update in MySQL
            StringBuilder sql = new StringBuilder("INSERT INTO ");
            sql.append(escapedTableName).append(" (");
            StringBuilder placeholders = new StringBuilder();
            StringBuilder updateClause = new StringBuilder();

            for (String key : record.keySet()) {
                if (placeholders.length() > 0) {
                    sql.append(", ");
                    placeholders.append(", ");
                    updateClause.append(", ");
                }
                // Escape key if it's a reserved keyword or has special characters
                String escapedKey = "`" + key + "`";
                sql.append(escapedKey);
                placeholders.append("?");
                updateClause.append(escapedKey).append(" = VALUES(").append(escapedKey).append(")");
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

    // Delete records from MySQL based on a list of records marked as deleted in SQLite
    public static void deleteRecordsFromMySQL(Connection mysqlConnection, String tableName, List<Map<String, Object>> deletedRecords) throws SQLException {
        System.out.println("Starting deletion process");
        System.out.println("Deleted records size: " + deletedRecords.size());
        if (deletedRecords.isEmpty()) {
            System.out.println("No records to delete.");
        } else {
            String sql = "DELETE FROM " + tableName + " WHERE id = ?";
            try (PreparedStatement pstmt = mysqlConnection.prepareStatement(sql)) {
                for (Map<String, Object> record : deletedRecords) {
                    System.out.println("TEST");
                    Object id = record.get("id");
                    if (id != null) {
                        pstmt.setObject(1, id);
                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Delete successfully from MySQL. ID: " + id);
                        } else {
                            System.out.println("No record found to delete for ID: " + id);
                        }
                    } else {
                        System.out.println("Invalid ID for deletion: " + id);
                    }
                }
                mysqlConnection.commit(); // Commit if necessary
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    mysqlConnection.rollback(); // Rollback if an error occurs
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        }
        System.out.println("Deletion process finished");

    }

    // Remove deleted records from the SQLite database or mark them as synced
    public static void removeDeletedRecordsFromSQLite(Connection sqliteConnection, String tableName, List<Map<String, Object>> deletedRecords) throws SQLException {
        String removeQuery = "DELETE FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement pstmt = sqliteConnection.prepareStatement(removeQuery)) {
            for (Map<String, Object> record : deletedRecords) {
                pstmt.setObject(1, record.get("id"));
                pstmt.executeUpdate();
            }
        }
    }
}
