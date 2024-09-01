package com.devmode.superdev.Controllers;

import com.devmode.superdev.MySqlConnector;
import com.devmode.superdev.SQLiteConnector;
import com.devmode.superdev.utils.SyncUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SyncController {

    private final SQLiteConnector sqliteConnector;
    private final MySqlConnector mysqlConnector;

    public SyncController(SQLiteConnector sqliteConnector, MySqlConnector mysqlConnector) {
        this.sqliteConnector = sqliteConnector;
        this.mysqlConnector = mysqlConnector;
    }

    public void syncAllTables() {
        try (Connection sqliteConnection = sqliteConnector.getConnection();
             Connection mysqlConnection = mysqlConnector.getConnection()) {

            String[] tables = {"category", "customer", "invoice", "'order'", "orderitem", "pointstransaction", "product", "supplier", "user"};

            for (String tableName : tables) {
                syncTable(sqliteConnection, mysqlConnection, tableName);
            }

            System.out.println("All tables synced successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Sync failed: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void syncTable(Connection sqliteConnection, Connection mysqlConnection, String tableName) throws SQLException {
        // Step 2: Fetch unsynced records from the SQLite database
        List<Map<String, Object>> unsyncedRecords = SyncUtils.fetchUnsyncedRecords(sqliteConnection, tableName);

        // Step 3: Sync records with MySQL
        SyncUtils.syncRecordsToMySQL(mysqlConnection, sqliteConnection, tableName, unsyncedRecords);

        // Step 4: Mark records as synced in the SQLite database
        List<Integer> recordIds = SyncUtils.getRecordIds(unsyncedRecords);
        SyncUtils.markRecordsAsSynced(sqliteConnection, tableName, recordIds);
    }
}
