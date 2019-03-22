package config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Config {
    private JSONObject configJSON;

    private String project;
    private String longDescription;
    private String shortDescription;
    private Date startDate;
    private Date endDate;
    private JSONArray tags;
    private int semester;
    private int positionInSemester;
    private int id;

    private String[] relevantFiles;
    private String title;
    private Date date;
    private String link;

    public Config(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();

        while (line != null) {
            sb.append(line).append("\n");
            line = buf.readLine();
        }
        String fileAsString = sb.toString();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            this.configJSON = new JSONObject(fileAsString);
            this.project = configJSON.getString("project");
            this.longDescription = configJSON.getString("longDescription");
            this.shortDescription = configJSON.getString("shortDescription");
            this.semester = configJSON.getInt("semester");
            this.positionInSemester = configJSON.getInt("position_in_semester");
            this.id = configJSON.getInt("id");
            this.tags = configJSON.getJSONArray("tags");

            relevantFiles = new String[configJSON.getJSONArray("relevantFiles").length()];
            for (int i = 0; i < configJSON.getJSONArray("relevantFiles").length(); i++) {
                this.relevantFiles[i] = configJSON.getJSONArray("relevantFiles").getString(i);
            }
            JSONObject lecture_mapping = configJSON.getJSONObject("lecture_mapping");
            this.title = lecture_mapping.getString("title");
            this.startDate = format.parse(configJSON.getString("startDate"));
            this.endDate = format.parse(configJSON.getString("endDate"));
            this.date = format.parse(lecture_mapping.getString("date"));
            this.link = lecture_mapping.getString("link");


        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getConfigJSON() {
        return this.configJSON;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getPositionInSemester() {
        return positionInSemester;
    }

    public void setPositionInSemester(int positionInSemester) {
        this.positionInSemester = positionInSemester;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getRelevantFiles() {
        return relevantFiles;
    }

    public void setRelevantFiles(String[] relevantFiles) {
        this.relevantFiles = relevantFiles;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public JSONArray getTags() {
        return tags;
    }
}