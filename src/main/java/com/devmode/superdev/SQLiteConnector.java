package com.devmode.superdev;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteConnector implements DatabaseConn {

    private static final String URL = "jdbc:sqlite:superdev.db"; // Update this path

    @Override
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(URL);
    }

    public void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            createTables(stmt);
            insertDefaultData(stmt);
            System.out.println("Database initialized successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        }
    }

    private void createTables(Statement stmt) throws SQLException {
        String[] tableCreationQueries = {
                "CREATE TABLE IF NOT EXISTS category (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE, synced INTEGER DEFAULT 0, deleted INTEGER DEFAULT 0);",
                "CREATE TABLE IF NOT EXISTS customer (\n" +
                        "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "    phone TEXT,\n" +
                        "    points INTEGER DEFAULT 0,\n" +
                        "    synced INTEGER DEFAULT 0,\n" +
                        "    deleted INTEGER DEFAULT 0\n" +
                        ");",
                "CREATE TABLE IF NOT EXISTS invoice (id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, totalAmount DECIMAL(10,2) NOT NULL, synced INTEGER DEFAULT 0, deleted INTEGER DEFAULT 0);",
                "CREATE TABLE IF NOT EXISTS orders (id INTEGER PRIMARY KEY AUTOINCREMENT, orderDate TEXT NOT NULL, totalAmount DECIMAL(10,2) NOT NULL, status TEXT NOT NULL CHECK(status IN ('paid', 'unpaid')), userID INTEGER, invoiceID INTEGER, customerID INTEGER, synced INTEGER DEFAULT 0, deleted INTEGER DEFAULT 0, FOREIGN KEY (userID) REFERENCES user(id), FOREIGN KEY (invoiceID) REFERENCES invoice(id), FOREIGN KEY (customerID) REFERENCES customer(id));",
                "CREATE TABLE IF NOT EXISTS orderitem (id INTEGER PRIMARY KEY AUTOINCREMENT, subtotal DECIMAL(10,2) NOT NULL, orderID INTEGER, productID INTEGER, synced INTEGER DEFAULT 0, deleted INTEGER DEFAULT 0, FOREIGN KEY (orderID) REFERENCES 'order'(id), FOREIGN KEY (productID) REFERENCES product(id));",
                "CREATE TABLE IF NOT EXISTS pointstransaction (id INTEGER PRIMARY KEY AUTOINCREMENT, points INTEGER NOT NULL, transactionDate TEXT NOT NULL, transactionType TEXT, orderID INTEGER, customerID INTEGER, synced INTEGER DEFAULT 0, deleted INTEGER DEFAULT 0, FOREIGN KEY (orderID) REFERENCES 'order'(id), FOREIGN KEY (customerID) REFERENCES customer(id));",
                "CREATE TABLE IF NOT EXISTS product (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, description TEXT, price DECIMAL(10,2) NOT NULL, stockQuantity INTEGER NOT NULL, barCode TEXT NOT NULL UNIQUE, categoryID INTEGER, supplierID INTEGER, image TEXT NOT NULL, synced INTEGER DEFAULT 0, deleted INTEGER DEFAULT 0, FOREIGN KEY (categoryID) REFERENCES category(id) ON DELETE CASCADE, FOREIGN KEY (supplierID) REFERENCES supplier(id) ON DELETE CASCADE);",
                "CREATE TABLE IF NOT EXISTS supplier (id INTEGER PRIMARY KEY AUTOINCREMENT, phone_nb TEXT, name TEXT NOT NULL, synced INTEGER DEFAULT 0, deleted INTEGER DEFAULT 0);",
                "CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTOINCREMENT, password TEXT NOT NULL, username TEXT NOT NULL UNIQUE, role TEXT CHECK(role IN ('admin', 'cashier')), status TEXT NOT NULL DEFAULT 'active' CHECK(status IN ('active', 'inactive')), synced INTEGER DEFAULT 0, deleted INTEGER DEFAULT 0);",
                "CREATE TABLE IF NOT EXISTS settings (id INTEGER PRIMARY KEY AUTOINCREMENT, setting_name TEXT NOT NULL UNIQUE, setting_value TEXT NOT NULL, synced INTEGER DEFAULT 0, deleted INTEGER DEFAULT 0);"
        };

        for (String query : tableCreationQueries) {
            stmt.execute(query);
        }
        System.out.println("SQLITE Tables have been created successfully.");
    }

    private void insertDefaultData(Statement stmt) throws SQLException {
        String insertDefaultUser = """
                INSERT OR IGNORE INTO user (id, password, username, role, status) VALUES
                (1, '1234', 'admin', 'admin', 'active');
                """;
        stmt.execute(insertDefaultUser);

        String insertDefaultCustomer = """
            INSERT OR IGNORE INTO customer (phone, points, synced, deleted) VALUES
            ('0000000', 0, 0, 0);
            """;

        stmt.execute(insertDefaultCustomer);

        String[] defaultSettings = {
                "INSERT OR IGNORE INTO settings (setting_name, setting_value) VALUES ('currency_rate', '89000');",
                "INSERT OR IGNORE INTO settings (setting_name, setting_value) VALUES ('points_step', '10');",
                "INSERT OR IGNORE INTO settings (setting_name, setting_value) VALUES ('step_points', '1');",
                "INSERT OR IGNORE INTO settings (setting_name, setting_value) VALUES ('point_amount', '0.11235');",
                "INSERT OR IGNORE INTO settings (setting_name, setting_value) VALUES ('points_activation', 'active');"
        };

        for (String sql : defaultSettings) {
            stmt.execute(sql);
        }
    }

}
