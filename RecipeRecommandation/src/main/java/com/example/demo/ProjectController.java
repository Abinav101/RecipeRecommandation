package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProjectController {
    String jdbcUrl = "jdbc:mysql://localhost:3306/Airline";
    String userId;

    @GetMapping("/hello")
    public String hello() {
        System.out.println("Inside hello method");
        return "login";
    }

    @PostMapping(value = "/submit", produces = "text/html")
    public String submit(@RequestParam String button, Model model, @RequestParam("userId") String userId, @RequestParam("password") String password) {
        System.out.println("Inside submit method");
        this.userId = userId;
        if (button.equals("button1")) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, "root", "12345")) {
                String sql = "SELECT password, usertype FROM user WHERE userId = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, userId);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    System.out.println("The dbpass is " + rs.getString("password") + " user pass is " + password);
                    if (password.equals(rs.getString("password"))) {
                        if ("admin".equals(rs.getString("usertype"))) {
                            return "adminDashboard";
                        } else {
                            return "Dashboard";
                        }
                    } else {
                        System.out.println("Wrong credentials");
                        return "alert";
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception is " + e);
            }
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam("signupusername") String username, @RequestParam("signupuserId") String userId, @RequestParam("signupuserpassword") String password, @RequestParam("signupuseraddress") String address, Model model) {
        System.out.println("Inside signup method");
        System.out.println("The attributes are " + username + " " + userId + " " + password + " " + address);
        try (Connection connection = DriverManager.getConnection(jdbcUrl, "root", "12345")) {
            String sql = "INSERT INTO user VALUES (?, ?, ?, ?)";
            PreparedStatement pstatement = connection.prepareStatement(sql);
            pstatement.setString(1, username);
            pstatement.setString(2, userId);
            pstatement.setString(3, password);
            pstatement.setString(4, address);
            pstatement.execute();
            System.out.println("Database updated successfully");
        } catch (Exception e) {
            System.out.println("The exception occurred is " + e);
        }
        return "Dashboard";
    }

    @GetMapping("/viewattendance")
    public String viewAttendance(Model model) {
        System.out.println("Inside viewattendance");
        List<Map<String, Object>> data = fetchData();
        model.addAttribute("dataList", data);
        return "viewAttendance";
    }

    @ModelAttribute("dataList")
    public List<Map<String, Object>> fetchData() {
        List<Map<String, Object>> data = new ArrayList<>();
        System.out.println("The username is " + userId);
        try (Connection connection = DriverManager.getConnection(jdbcUrl, "root", "12345")) {
            String sql = "SELECT * FROM attendance WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Map<String, Object> mp = new HashMap<>();
                mp.put("Username", rs.getString("username"));
                mp.put("Subject", rs.getString("subject"));
                mp.put("Attendance", rs.getInt("attendance"));
                data.add(mp);
            }
            System.out.println("The map data are " + data);
        } catch (SQLException e) {
            System.out.println("The exception occurred: " + e);
        }
        return data;
    }

    @PutMapping("/createAccount")
    public String createAccount(@RequestBody UserData userData) {
        System.out.println("Request details are " + userData.getUserId() + " " + userData.getAddress() + " " + userData.getPassword() + " " + userData.getUsername());
        return "The data is inserted in db";
    }

    @GetMapping("/viewRecipes")
    public String viewFlights(@RequestParam(name = "viewType", defaultValue = "table") String viewType, Model model) {
        List<Map<String, Object>> data = fetchFlight();
        model.addAttribute("flightList", data);
        model.addAttribute("viewType", viewType);
        return "viewRecipes";
    }

    @ModelAttribute("flightList")
    public List<Map<String, Object>> fetchFlight() {
        List<Map<String, Object>> listOfFlights = new ArrayList<>();
        System.out.println("The username is " + userId);
        try (Connection connection = DriverManager.getConnection(jdbcUrl, "root", "12345")) {
            String sql = "SELECT * FROM flights";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Map<String, Object> mp = new HashMap<>();
                mp.put("Flightname", rs.getString("flightname"));
                mp.put("Flightnumber", rs.getString("flightnumber"));
                mp.put("FromCity", rs.getString("fromCity"));
                mp.put("ToCity", rs.getString("toCity"));
                mp.put("Date", rs.getDate("date"));
                mp.put("Price", rs.getInt("price"));
                listOfFlights.add(mp);
            }
            System.out.println("The map data are " + listOfFlights);
        } catch (SQLException e) {
            System.out.println("The exception occurred: " + e);
        }
        return listOfFlights;
    }

    @PostMapping("/bookFlight")
    public String bookFlight(@RequestParam(value = "selectedRowbutton1", required = false) String selectedRow, @RequestParam(value = "selectedRowbutton2", required = false) String selectedRow2, Model model) {
        System.out.println("The selected flight is " + selectedRow);
        System.out.println("The selected second row is " + selectedRow2);
        String flightName = "";
        String flightNum = "";
        if (null != selectedRow) {
            String flightColumns[] = selectedRow.split(",");
            for (String keyValue : flightColumns) {
                String keyValArr[] = keyValue.trim().split("=");
                System.out.println("The keyValArr" + keyValArr);
                if (keyValArr[0].substring(1).equals("Flightname")) {
                    flightName = keyValArr[1];
                }
                if (keyValArr[0].equals("Flightnumber")) {
                    flightNum = keyValArr[1];
                }
            }
            System.out.println("The name and number are " + flightName + " " + flightNum);
            model.addAttribute("flightname", flightName);
            model.addAttribute("flightnumber", flightNum);
        }
        return "flightBookedAlert";
    }

    @PostMapping("/addFlight")
    public String addFlight(@RequestParam("flightName") String flightName,
                            @RequestParam("flightNumber") String flightNumber,
                            @RequestParam("fromCity") String fromCity,
                            @RequestParam("toCity") String toCity,
                            @RequestParam("date") Date date,
                            @RequestParam("price") int price) {
        System.out.println("Inside addFlight method");
        try (Connection connection = DriverManager.getConnection(jdbcUrl, "root", "12345")) {
            String sql = "INSERT INTO flights (flightname, flightnumber, fromCity, toCity, date, price) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, flightName);
            statement.setString(2, flightNumber);
            statement.setString(3, fromCity);
            statement.setString(4, toCity);
            statement.setDate(5, date);
            statement.setInt(6, price);
            statement.executeUpdate();
            System.out.println("Flight added successfully");
        } catch (SQLException e) {
            System.out.println("Exception occurred while adding flight: " + e);
        }
        return "redirect:/viewRecipe";
    }

    @GetMapping("/AddFlights")
    public String addFlightsPage() {
        return "addFlight";
    }

    @GetMapping("/UpdateFlights")
    public String updateFlightsPage() {
        return "updateFlight";
    }

    @GetMapping("/DeleteFlights")
    public String deleteFlightsPage() {
        return "deleteFlight";
    }

    @GetMapping("/updateFlight")
    public String showUpdateForm(@RequestParam("flightNumber") String flightNumber, Model model) {
        System.out.println("Inside showUpdateForm method for flight number: " + flightNumber);
        try (Connection connection = DriverManager.getConnection(jdbcUrl, "root", "12345")) {
            String sql = "SELECT * FROM flights WHERE flightnumber = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, flightNumber);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                model.addAttribute("flightNumber", rs.getString("flightnumber"));
                model.addAttribute("flightName", rs.getString("flightname"));
                model.addAttribute("fromCity", rs.getString("fromCity"));
                model.addAttribute("toCity", rs.getString("toCity"));
                model.addAttribute("date", rs.getDate("date").toString()); // Convert Date to String
                model.addAttribute("price", rs.getInt("price"));
            }
        } catch (SQLException e) {
            System.out.println("Exception occurred while fetching flight details for update: " + e);
        }

        return "updateFlight";
    }

    @PostMapping("/updateFlight")
    public String updateFlight(@RequestParam("flightNumber") String flightNumber,
                               @RequestParam("flightName") String flightName,
                               @RequestParam("fromCity") String fromCity,
                               @RequestParam("toCity") String toCity,
                               @RequestParam("date") Date date,
                               @RequestParam("price") int price) {
        System.out.println("Inside updateFlight method for flight number: " + flightNumber);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, "root", "12345")) {
            String sql = "UPDATE flights SET flightname = ?, fromCity = ?, toCity = ?, date = ?, price = ? WHERE flightnumber = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, flightName);
            statement.setString(2, fromCity);
            statement.setString(3, toCity);
            statement.setDate(4, date);
            statement.setInt(5, price);
            statement.setString(6, flightNumber);
            statement.executeUpdate();
            System.out.println("Flight updated successfully");
        } catch (SQLException e) {
            System.out.println("Exception occurred while updating flight: " + e);
        }

        return "redirect:/viewRecipes";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        System.out.println("Inside dashboard method");
        // Fetch any required data for the dashboard
        List<Map<String, Object>> flightData = fetchFlight();
        model.addAttribute("flightList", flightData);

        // Add any other necessary attributes to the model
        // model.addAttribute("otherAttribute", value);

        return "Dashboard";
    }
}
