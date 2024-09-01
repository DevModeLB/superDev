package com.devmode.superdev.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.devmode.superdev.DatabaseManager;

public class DeleteFromDatabase {
    public static void deleteFromDatabase(String table, int id){
        String query = "DELETE FROM " + table + " WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                new ErrorDialog().showErrorDialog("Deleted successfully", "success");
            } else {
                new ErrorDialog().showErrorDialog("Something went wrong", "Error");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

