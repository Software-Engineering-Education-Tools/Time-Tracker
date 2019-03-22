package data;

public interface DataProvider {
    String getDashboardData(); //Get Configuration data of project, get total time, total project size, other aggregated data
    String getProjectData(String project);
    String getGraphData(); // Get time spent, get coding activity, get other data
}