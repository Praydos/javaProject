package org.example.demo1;

//package com.example.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDAO {

    public static void insertTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (product_id, transaction_type, quantity, remarks) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transaction.getProductId());
            pstmt.setString(2, transaction.getTransactionType());
            pstmt.setInt(3, transaction.getQuantity());
            pstmt.setString(4, transaction.getRemarks());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
