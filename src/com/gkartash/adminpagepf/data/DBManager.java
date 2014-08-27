package com.gkartash.adminpagepf.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;


/**
 * Created by gkartashevskyy on 8/15/2014.
 */
public class DBManager {

    private static PreparedStatement st = null;
    private static Connection dbConnection = null;
    private static String url = "jdbc:postgresql://localhost:5432/dimeDB";
    private static String user = "postgres";
    private static String password = "56TGhhj&";

    private static final String SERVER_CONNECTIONS_TABLE = "serverconnections";
    private static final String CLIENT_CONNECTIONS_TABLE = "clientconnections";
    private static final String ADMINS_TABLE = "admins";
    private static final String MESSAGES_TABLE = "messages";

    public static final String STATUS_FIELD = "status";
    public static final String CLIENT_FIELD = "client";
    private static final String SENDTO_FIELD = "sendtoclient";
    public static final String TIME_FIELD = "event_time";
    private static final String MESSAGE_FIELD = "message";
    private static final String ADMIN_LOGIN = "login";
    private static final String ADMIN_PASSWORD = "password";

    public static final boolean SERVER_CLIENT_JOINED = true;
    public static final boolean SERVER_CLIENT_LEFT = false;

    public static final boolean CLIENT_SENDTO_ADD = true;
    public static final boolean CLIENT_SENDTO_DELETED = false;



    static Logger log = Logger.getLogger(DBManager.class.getCanonicalName());

    public static PreparedStatement getLoginStatement(String login, String pass) {




        try {
            if (dbConnection == null) {
                dbConnection = DriverManager.getConnection(url, user, password);
            }
            String requestLogin = "SELECT * FROM " + ADMINS_TABLE + " WHERE " + ADMIN_LOGIN + " = ? AND " +
                    ADMIN_PASSWORD + " = ?";
            st = dbConnection.prepareStatement(requestLogin);
            st.setString(1, login);
            st.setString(2, pass);
        } catch (SQLException e) {
            log.info("DB Connection failed " + e);
        }

        log.info("DB Connection succeed");

        return st;

    }

    public static PreparedStatement getUsersStatusQuery(){

        String status = "SELECT MAX(event_time), client, status " +
                        "FROM serverconnections " +
                        "GROUP BY client, status " +
                        "ORDER BY client ASC";

        try {
            st = dbConnection.prepareStatement(status);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return st;

    }

    public static PreparedStatement getStatQuery() {

        String stats = "SELECT event_time, status " +
                       "FROM serverconnections";

        try {
            st = dbConnection.prepareStatement(stats);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return st;

    }




}
