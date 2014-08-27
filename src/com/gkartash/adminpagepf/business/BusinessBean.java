package com.gkartash.adminpagepf.business;

import com.gkartash.adminpagepf.data.DBManager;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by gkartashevskyy on 8/15/2014.
 */
@ManagedBean
@SessionScoped
public class BusinessBean {

    private PreparedStatement statement;
    private Map<String, Boolean> mapOfUsersStatus = new HashMap<String, Boolean>();
    private List<Map.Entry<String, Boolean>> usersList;
    private LineChartModel lineModel;


    private String admin;



    private String password;

    static Logger logger = Logger.getLogger(BusinessBean.class.getCanonicalName());

    public BusinessBean() {

    }

    public LineChartModel getLineModel() {
        logger.info("Linemodel requested by PF ");
        return lineModel;
    }

    private void queryUsersStatus(){



        statement = DBManager.getUsersStatusQuery();


        try {
            ResultSet resultSet = statement.executeQuery();

            String user = "";
            boolean isOnline = false;
            Date lastStatusChange = null;

            while (resultSet.next()) {
                if (resultSet.getString(DBManager.CLIENT_FIELD).equals(user)) {
                    if (!lastStatusChange.after(new Date(resultSet.getTimestamp(1).getTime()))) {

                        isOnline = resultSet.getBoolean(DBManager.STATUS_FIELD);
                        logger.info("same user, another status");
                    }



                } else {
                    user = resultSet.getString(DBManager.CLIENT_FIELD);
                    isOnline = resultSet.getBoolean(DBManager.STATUS_FIELD);
                    lastStatusChange = new Date(resultSet.getTimestamp(1).getTime());
                    logger.info("Date " + lastStatusChange);
                }

                if (!user.equals("null")){
                    mapOfUsersStatus.put(user, isOnline);
                    logger.info("Map of users: " + user + ", " + isOnline);
                }

            }

            usersList = new ArrayList<Map.Entry<String, Boolean>>(mapOfUsersStatus.entrySet());



        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Map.Entry<String, Boolean>> getUsersList() {

        return usersList;
    }

    public String login() {



        statement = DBManager.getLoginStatement(admin, password);
        try {

            logger.info(statement.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                queryUsersStatus();
                createLineChart();
                return "index";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                "Invalid Login!", "Please Try Again!"));

        return "login";


    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    private void createLineChart() {

        statement = DBManager.getStatQuery();

        lineModel = new LineChartModel();

        LineChartSeries s1 = new LineChartSeries();
        s1.setLabel("Users Online");
        int userOnlineCounter = 0;
        int counter = 0;


        try {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                if (resultSet.getBoolean(DBManager.STATUS_FIELD)) {
                    userOnlineCounter++;
                } else {
                    userOnlineCounter--;
                }

                logger.info("User counter " + userOnlineCounter + ", time " + resultSet.getDate(1).getTime());

                counter++;




                //s1.set(resultSet.getDate(1).getTime(), userOnlineCounter);
                s1.set(counter, userOnlineCounter);



            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        logger.info("After while ");

        lineModel.addSeries(s1);
        lineModel.setTitle("Users");
        lineModel.setLegendPosition("e");
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(7);

    }








}
