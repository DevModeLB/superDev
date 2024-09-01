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
        // Use a transaction to ensure atomicity of the sync process
        try {
            sqliteConnection.setAutoCommit(false);
            mysqlConnection.setAutoCommit(false);

            // Sync non-deleted records
            List<Map<String, Object>> unsyncedRecords = SyncUtils.fetchUnsyncedNonDeletedRecords(sqliteConnection, tableName);
            if (!unsyncedRecords.isEmpty()) {
                SyncUtils.syncRecordsToMySQL(mysqlConnection, sqliteConnection, tableName, unsyncedRecords);

                List<Integer> syncedRecordIds = SyncUtils.getRecordIds(unsyncedRecords);
                SyncUtils.markRecordsAsSynced(sqliteConnection, tableName, syncedRecordIds);
            }

            // Handle deleted records
            List<Map<String, Object>> deletedRecords = SyncUtils.fetchUnsyncedDeletedRecords(sqliteConnection, tableName);
            if (!deletedRecords.isEmpty()) {
                SyncUtils.deleteRecordsFromMySQL(mysqlConnection, tableName, deletedRecords);
                SyncUtils.removeDeletedRecordsFromSQLite(sqliteConnection, tableName, deletedRecords);
            }

            // Handle records that are marked as synced but deleted
            List<Map<String, Object>> syncedDeletedRecords = SyncUtils.fetchSyncedAndDeletedRecords(sqliteConnection, tableName);
            if (!syncedDeletedRecords.isEmpty()) {
                SyncUtils.deleteRecordsFromMySQL(mysqlConnection, tableName, syncedDeletedRecords);
                SyncUtils.removeDeletedRecordsFromSQLite(sqliteConnection, tableName, syncedDeletedRecords);
            }

            // Commit transaction if all operations are successful
            sqliteConnection.commit();
            mysqlConnection.commit();
        } catch (SQLException e) {
            // Rollback transaction in case of error
            sqliteConnection.rollback();
            mysqlConnection.rollback();
            throw e;  // rethrow the exception after rollback
        } finally {
            // Reset auto-commit mode to true
            sqliteConnection.setAutoCommit(true);
            mysqlConnection.setAutoCommit(true);
        }
    }
}
