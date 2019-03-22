package database;

import config.Config;
import filesystem.FilesystemHelper;
import logger.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import events.*;


import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class DatabaseConnector {
    private Connection connection = null;
    private String[] relevantFiles;

    public void connect() {
        try {
            // create a database connection
            String dbPath = FilesystemHelper.getDatabaseFile().getAbsolutePath();
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS user(user TEXT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ideEvents(id INTEGER PRIMARY KEY AUTOINCREMENT, time DATE, type TEXT, message TEXT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS projectEvents(id INTEGER PRIMARY KEY AUTOINCREMENT, time DATE, type TEXT, message TEXT, project TEXT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS editorEvents(id INTEGER PRIMARY KEY AUTOINCREMENT, time DATE, type TEXT, message TEXT, filename TEXT, project TEXT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS codeEvents(id INTEGER PRIMARY KEY AUTOINCREMENT, time DATE, type TEXT, message TEXT, filename TEXT, project TEXT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS compileEvents(id INTEGER PRIMARY KEY AUTOINCREMENT, time DATE, type TEXT, message TEXT, project TEXT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS fileList(id INTEGER PRIMARY KEY AUTOINCREMENT, filename TEXT, project TEXT, size INT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS configs(id INTEGER PRIMARY KEY AUTOINCREMENT, location TEXT, project TEXT, longDescription TEXT, shortDescription TEXT, semester TEXT, positionInSemester TEXT, projectID TEXT, tags TEXT, title TEXT, startDate TEXT, endDate TEXT, link TEXT)");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    String getID() {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT user FROM user");
            if (rs.isClosed()) {
                String uuid = UUID.randomUUID().toString();
                String insertUUID = "INSERT INTO user (user) VALUES ('" + uuid + "')";
                statement.executeUpdate(insertUUID);
                return uuid;
            } else {
                System.err.println("user = " + rs.getString("user"));
                return rs.getString("user");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return "none";
    }

    private void writeValuesIntoDatabase(String table, ArrayList<String> fieldsList, ArrayList<String> valuesList) {
        String[] fields = fieldsList.toArray(new String[fieldsList.size()]);
        String[] values = valuesList.toArray(new String[valuesList.size()]);

        String sql = "INSERT INTO %s(%s) VALUES (%s)";
        String fieldsString = String.join(",", fields);
        String valuesString = "";
        for (String value : values) {
            valuesString += "?,";
        }
        valuesString = valuesString.substring(0, valuesString.length() - 1);
        sql = String.format(sql, table, fieldsString, valuesString);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int a = 0; a < values.length; a++) {
                pstmt.setString(a + 1, values[a]);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void logIdeEvent(IDEEvent event) {
        ArrayList<String> standardFieldsArrayList = getStandardFieldNames();
        ArrayList<String> standardValuesArrayList = getStandardValues(event);
        writeValuesIntoDatabase("ideEvents", standardFieldsArrayList, standardValuesArrayList);
    }

    private void logProjectEvent(ProjectEvent event) {
        ArrayList<String> standardFieldsArrayList = getStandardFieldNames();
        standardFieldsArrayList.add("project");
        ArrayList<String> standardValuesArrayList = getStandardValues(event);
        standardValuesArrayList.add(event.getName());
        writeValuesIntoDatabase("projectEvents", standardFieldsArrayList, standardValuesArrayList);
    }

    private void logEditorEvent(EditorEvent event) {
        ArrayList<String> standardFieldsArrayList = getStandardFieldNames();
        standardFieldsArrayList.add("project");
        standardFieldsArrayList.add("filename");
        ArrayList<String> standardValuesArrayList = getStandardValues(event);
        standardValuesArrayList.add(event.getName());
        standardValuesArrayList.add(event.getFileName());
        writeValuesIntoDatabase("editorEvents", standardFieldsArrayList, standardValuesArrayList);
    }

    private void logCompileEvent(CompileEvent event) {
        ArrayList<String> standardFieldsArrayList = getStandardFieldNames();
        standardFieldsArrayList.add("project");
        ArrayList<String> standardValuesArrayList = getStandardValues(event);
        standardValuesArrayList.add(event.getName());
        writeValuesIntoDatabase("compileEvents", standardFieldsArrayList, standardValuesArrayList);
    }

    private void logCodeEvent(CodeEvent event) {
        ArrayList<String> standardFieldsArrayList = getStandardFieldNames();
        standardFieldsArrayList.add("filename");
        standardFieldsArrayList.add("project");
        ArrayList<String> standardValuesArrayList = getStandardValues(event);
        standardValuesArrayList.add(event.getFilename());
        standardValuesArrayList.add(event.getName());
        writeValuesIntoDatabase("codeEvents", standardFieldsArrayList, standardValuesArrayList);
    }

    public void logConfig(Config config, String location) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT location FROM configs");
            boolean configFound = false;
            while (rs.next()) {
                if (rs.getString("location").equals(location)) {
                    Logger.log("Config already in DB, not adding.");
                    configFound = true;
                    break;
                }
            }
            if (!configFound) {
                String sql = "INSERT INTO configs(location," +
                        "project, " +
                        "longDescription," +
                        "shortDescription," +
                        "semester," +
                        "positionInSemester," +
                        "projectID," +
                        "tags," +
                        "title," +
                        "startDate," +
                        "endDate," +
                        "link)" +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, location);
                    pstmt.setString(2, config.getProject());
                    pstmt.setString(3, config.getLongDescription());
                    pstmt.setString(4, config.getShortDescription());
                    pstmt.setString(5, String.valueOf(config.getSemester()));
                    pstmt.setString(6, String.valueOf(config.getPositionInSemester()));
                    pstmt.setString(7, String.valueOf(config.getId()));
                    pstmt.setString(8, config.getTags().toString());
                    pstmt.setString(9, config.getTitle());
                    pstmt.setString(10, config.getStartDate().toString());
                    pstmt.setString(11, config.getEndDate().toString());
                    pstmt.setString(12, config.getLink());
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertCodeEvents(CodeEvent event) {
        String updateFileListSQL = "UPDATE fileList SET size = ? WHERE id = ?";
        String extension = event.getFilename().substring(event.getFilename().lastIndexOf(".") + 1);
        for (String foundExtension : relevantFiles) {
            if (extension.equals(foundExtension)) {
                try {
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery("SELECT * FROM fileList WHERE project='" + event.getName() + "'AND filename='" + event.getFilename() + "'");
                    if (rs.isClosed()) {
                        Logger.log("New file edited. Adding to Files.", "DatabaseConnector");
                        logNewFile(event.getName(), event.getFilename(), Integer.parseInt(event.getMessage()));
                    } else {
                        Logger.log("Existent file edited. Adding Code event.", "DatabaseConnector");
                        int id = rs.getInt("id");
                        int previousSize = Integer.parseInt(rs.getString("size"));
                        try (PreparedStatement pstmt = connection.prepareStatement(updateFileListSQL)) {
                            pstmt.setString(1, event.getMessage());
                            pstmt.setInt(2, id);
                            pstmt.executeUpdate();
                        } catch (SQLException e) {
                            System.err.println(e.getMessage());
                        }
                        event.setMessage(Integer.toString(Integer.parseInt(event.getMessage()) - previousSize));
                        logCodeEvent(event);

                    }
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    private ArrayList<String> getStandardFieldNames() {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("time");
        arrayList.add("type");
        arrayList.add("message");
        return arrayList;
    }

    private ArrayList<String> getStandardValues(Event event) {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(event.getTimestamp().toString());
        arrayList.add(event.getEventType().toString());
        arrayList.add(event.getMessage());
        return arrayList;
    }

    private void logNewFile(String project, String name, int size) {
        String updateFileListSQL = "INSERT INTO fileList(project, filename, size) VALUES(?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(updateFileListSQL)) {
            pstmt.setString(1, project);
            pstmt.setString(2, name);
            pstmt.setInt(3, size);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void logEvent(Event event) {
        switch (event.getEventType()) {
            case IDE:
                IDEEvent ideEvent = (IDEEvent) event;
                logIdeEvent(ideEvent);
                break;
            case PROJECT:
                ProjectEvent projectEvent = (ProjectEvent) event;
                logProjectEvent(projectEvent);
                break;
            case EDITOR:
                EditorEvent editorEvent = (EditorEvent) event;
                logEditorEvent(editorEvent);
                break;
            case COMPILE:
                CompileEvent compileEvent = (CompileEvent) event;
                logCompileEvent(compileEvent);
                break;
            case CODE:
                CodeEvent codeEvent = (CodeEvent) event;
                insertCodeEvents(codeEvent);
                break;
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getDashboardData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("dates", getDatesTotal());
            jsonObject.put("numProjects", getNumProjects().getString("numProjects"));
            jsonObject.put("lines", getLinesTotal().getString("lines"));
            jsonObject.put("projects", getProjects());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getProjectData(String project) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("dates", getDatesSingleProject(project));
            jsonObject.put("lines", getLines(project).getString("lines"));
            jsonObject.put("compiles", getCompilesSingleProject(project));
            jsonObject.put("files", getFilesSingleProject(project));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getGraphData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("dataHourly", getHourlyGraphdata());
            jsonObject.put("dataDaily", getDailyGraphdata());
            jsonObject.put("projects", getProjects());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONArray getProjects() {
        try {
            JSONArray jsonArray;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT project as project FROM configs;");
            jsonArray = ResultSetConverter.convert(rs);
            return jsonArray;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getDatesTotal() {
        try {
            JSONArray jsonMax, jsonMin;
            JSONObject json = new JSONObject();
            Statement statement = connection.createStatement();
            ResultSet rsMax = statement.executeQuery("SELECT max(strftime('%Y-%m-%d', time)) as maxTime FROM projectEvents;");
            jsonMax = ResultSetConverter.convert(rsMax);
            ResultSet rsMin = statement.executeQuery("SELECT min(strftime('%Y-%m-%d', time)) as minTime FROM projectEvents;");
            jsonMin = ResultSetConverter.convert(rsMin);
            json.put("minTime", jsonMin.getJSONObject(0).get("minTime"));
            json.put("maxTime", jsonMax.getJSONObject(0).get("maxTime"));
            return json;
        } catch (SQLException | JSONException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private JSONObject getLinesTotal() {
        try {
            JSONArray jsonArray;
            JSONObject json = new JSONObject();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT sum(message) as lines FROM codeEvents;");
            jsonArray = ResultSetConverter.convert(rs);
            json.put("lines", jsonArray.getJSONObject(0).getString("lines"));
            return json;
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getLines(String project) {
        try {
            JSONArray jsonArray;
            JSONObject json = new JSONObject();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT sum(message) as lines FROM codeEvents WHERE project = '" + project + "';");
            jsonArray = ResultSetConverter.convert(rs);
            Logger.log(jsonArray.toString(), "getLines");
            json.put("lines", jsonArray.getJSONObject(0).getString("lines"));
            return json;
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getCompilesSingleProject(String project) {
        try {
            JSONArray jsonSuccesses, jsonFails;
            JSONObject json = new JSONObject();
            Statement statement = connection.createStatement();
            ResultSet rsSuccesses = statement.executeQuery("SELECT count(message) as successes FROM compileEvents WHERE project= '" + project + "\' AND message='successful';");
            jsonSuccesses = ResultSetConverter.convert(rsSuccesses);
            ResultSet rsFails = statement.executeQuery("SELECT count(message) as fails FROM compileEvents WHERE project= \'" + project + "\' AND message='failed';");
            jsonFails = ResultSetConverter.convert(rsFails);
            json.put("fails", jsonFails.getJSONObject(0).get("fails"));
            json.put("successes", jsonSuccesses.getJSONObject(0).get("successes"));
            return json;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray getFilesSingleProject(String project) {
        try {
            JSONArray jsonArray;
            JSONObject json = new JSONObject();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT filename as file, count(filename) as edits FROM codeEvents WHERE project= '" + project + "\' GROUP BY filename;");
            jsonArray = ResultSetConverter.convert(rs);
            Logger.log(jsonArray.toString(), "getFilesSingleProject");
            return jsonArray;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getDatesSingleProject(String project) {
        try {
            JSONArray jsonMax, jsonMin;
            JSONObject json = new JSONObject();
            Statement statement = connection.createStatement();
            ResultSet rsMax = statement.executeQuery("SELECT max(strftime('%Y-%m-%d', time)) as maxTime FROM codeEvents WHERE project= '" + project + "\';");
            jsonMax = ResultSetConverter.convert(rsMax);
            ResultSet rsMin = statement.executeQuery("SELECT min(strftime('%Y-%m-%d', time)) as minTime FROM codeEvents WHERE project= \'" + project + "\';");
            jsonMin = ResultSetConverter.convert(rsMin);
            json.put("minTime", jsonMin.getJSONObject(0).get("minTime"));
            json.put("maxTime", jsonMax.getJSONObject(0).get("maxTime"));
            return json;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private JSONObject getNumProjects() {
        try {
            JSONArray jsonArray;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT count(DISTINCT project) as numProjects FROM projectEvents group by type;");
            jsonArray = ResultSetConverter.convert(rs);
            return jsonArray.getJSONObject(0);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray getHourlyGraphdata() {
        try {
            JSONArray jsonArray;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT SUM(message) as loc, " +
                    "strftime('%H', time) as time " +
                    "FROM codeEvents " +
                    "GROUP BY strftime('%H', time);");
            jsonArray = ResultSetConverter.convert(rs);
            return jsonArray;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray getDailyGraphdata() {
        try {
            JSONArray jsonArray;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT SUM(message)as loc,\n" +
                    "    strftime('%w', time) as time\n" +
                            "    FROM codeEvents\n" +
                            "    group by strftime('%w', time);");
            jsonArray = ResultSetConverter.convert(rs);
            return jsonArray;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSelectedProjectPath(String project) {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT location FROM configs WHERE project='" + project + "';");
            return rs.getString("location");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public void setRelevantFiles(String[] relevantFiles) {
        this.relevantFiles = relevantFiles;
    }
}
