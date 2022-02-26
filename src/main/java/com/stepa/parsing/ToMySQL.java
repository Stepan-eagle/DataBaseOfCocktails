package com.stepa.parsing;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.*;

public class ToMySQL {

    static final String DB_URL = "jdbc:mysql://localhost:3308/dev_cocktailtb_db";
    static final String USER = "dev_cocktailtb_db_user";
    static final String PASS = "dev_cocktailtb_db_password";

    public void createTable (String DB_URL,String USER,String PASS) {
        // Open a connection
        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
        ) {
            String sql = "CREATE TABLE cocktails " +
                    "(id INTEGER not NULL AUTO_INCREMENT, " +
                    " name VARCHAR(200), " +
                    " ingr VARCHAR(2000), " +
                    " pick VARCHAR(2000), " +
                    " technique BLOB," +
                    " comps VARCHAR(2000), " +
                    " PRIMARY KEY ( id ))";
            String alter = "ALTER TABLE cocktails CONVERT TO CHARACTER SET utf8";
            String drop = "drop table cocktails";
            stmt.executeUpdate(drop);
            System.out.println("Droped table in given database...");
            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");
            stmt.executeUpdate(alter);
            System.out.println("Alter table in given database...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertToTable (String DB_URL,String USER,String PASS,String name,String ingr,String pick,String technique, String comps) throws SQLException {
        String sql = " insert into cocktails (name, ingr, pick, technique, comps)"
                + " values (?, ?, ?, ?, ?)";
        byte[] byteData = technique.getBytes();
        Blob docInBlob = new SerialBlob(byteData);
        // Open a connection
        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)
        ) { // the mysql insert statement
            // create the mysql insert preparedstatement
            pstmt.setString (1, name);
            pstmt.setString (2, ingr);
            pstmt.setString   (3, pick);
            pstmt.setBlob(4, docInBlob);
            pstmt.setString (5, comps);

            // execute the preparedstatement
            pstmt.executeUpdate();
            System.out.println("Added row in given database...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
