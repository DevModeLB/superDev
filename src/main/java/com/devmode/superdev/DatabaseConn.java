package com.devmode.superdev;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConn {
    Connection getConnection() throws SQLException, ClassNotFoundException;
}
