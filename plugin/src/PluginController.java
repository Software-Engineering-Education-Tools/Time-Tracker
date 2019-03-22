import com.intellij.openapi.compiler.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.vfs.*;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import config.Config;
import constants.Constants;
import database.DatabaseConnector;
import events.*;
import logger.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import data.DataProvider;
import server.Server;
import server.ServerMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PluginController implements VirtualFileListener, ProjectManagerListener, FileEditorManagerListener, CompilationStatusListener, DataProvider {
    private DatabaseConnector db;
    private Server server;
    private Config currentConfig;
    private Project currentProject;
    private String[] relevantFiles;
    private long startTime;

    public PluginController() {
        initDB();
        initServer();
        start();
    }

    private void initDB() {
        this.db = new DatabaseConnector();
        db.connect();
    }

    private void initServer() {
        server = new Server(Constants.SERVER_PORT, this);
        server.start();
    }

    private void start() {
        IDEEvent ideEvent = new IDEEvent(IDEEvent.MESSAGE_OPENED);
        db.logEvent(ideEvent);
    }

    public void shutdown() {
        IDEEvent ideEvent = new IDEEvent(IDEEvent.MESSAGE_CLOSED);
        db.logEvent(ideEvent);
        server.shutdown();
        db.close();
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        if (!event.getParent().toString().contains("/.")) { //Remove files that are supposed to be hidden
            try {
                BufferedReader reader = new BufferedReader(new FileReader(event.getFile().getPath()));
                int lines = 0;
                while ((reader.readLine()) != null) {
                    lines++;
                }

                CodeEvent codeEvent = new CodeEvent(currentProject.getBasePath(), Long.toString(lines), event.getFile().getName());

                db.logEvent(codeEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {

    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
    }

    @Override
    public void projectOpened(@NotNull Project project) {
        this.currentProject = project;
        initConfig(project);
        initEditorFilesEventListener(project);
        initFileEventListener(project);
        initCompileEventListener(project);

        startTime = System.currentTimeMillis();

        ProjectEvent projectEvent = new ProjectEvent(project.getBasePath(), ProjectEvent.MESSAGE_OPENED);
        db.logEvent(projectEvent);
    }

    private void initEditorFilesEventListener(Project project) {
        MessageBus messageBus = project.getMessageBus();
        MessageBusConnection messageBusConnection = messageBus.connect();
        messageBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);
    }

    private void initFileEventListener(Project project) {
        VirtualFileManager.getInstance().addVirtualFileListener(this);
    }

    private void initCompileEventListener(Project project) {
        MessageBus messageBus = project.getMessageBus();
        MessageBusConnection messageBusConnection = messageBus.connect();
        messageBusConnection.subscribe(CompilerTopics.COMPILATION_STATUS, this);
    }

    private void initConfig(Project project) {
        try {
            File file = new File(project.getBasePath() + "/mi-visualizer.config");
            currentConfig = new Config(file);
            relevantFiles = currentConfig.getRelevantFiles();
            db.setRelevantFiles(relevantFiles);
            db.logConfig(currentConfig, project.getBasePath());
            Logger.log("Config file found and created.", "PluginController");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        Logger.log("Project closing", "PluginController");
        long timeSpent = startTime - System.currentTimeMillis();
        ProjectEvent projectEvent = new ProjectEvent(project.getBasePath(), ProjectEvent.MESSAGE_CLOSED);
        db.logEvent(projectEvent);
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        EditorEvent editorEvent = new EditorEvent(currentProject.getBasePath(), EditorEvent.EDITOR_FILE_OPENED, file.getName());
        db.logEvent(editorEvent);
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        EditorEvent editorEvent = new EditorEvent(currentProject.getBasePath(), EditorEvent.EDITOR_FILE_CLOSED, file.getName());
        db.logEvent(editorEvent);
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        EditorEvent editorEventNewFile = new EditorEvent(currentProject.getBasePath(), EditorEvent.EDITOR_FILE_SELECTED, event.getNewFile().getName());
        db.logEvent(editorEventNewFile);
        if (event.getOldFile() != null && event.getOldFile().exists()) {
            EditorEvent editorEventOldFile = new EditorEvent(currentProject.getBasePath(), EditorEvent.EDITOR_FILE_UNSELECTED, event.getOldFile().getName());
            db.logEvent(editorEventOldFile);
        }
    }
    /*
        Compiling Events
     */

    @Override
    public void compilationFinished(boolean aborted, int errors, int warnings, @NotNull CompileContext compileContext) {
        if (errors == 0) {
            CompileEvent compileEvent = new CompileEvent(currentProject.getBasePath(), CompileEvent.COMPILE_SUCCESS);
            db.logEvent(compileEvent);
        } else {
            CompilerMessage[] messages = compileContext.getMessages(CompilerMessageCategory.ERROR);
            for (CompilerMessage message : messages) {
                CompileEvent compileEvent = new CompileEvent(currentProject.getBasePath(), CompileEvent.COMPILE_FAIL);
                db.logEvent(compileEvent);
            }
        }
    }

    /*
    Data Provider
     */
    @Override
    public String getDashboardData() {
        String payload = "";
        payload = payload + db.getDashboardData().toString();

        ServerMessage serverMessage = new ServerMessage("dashboardData", payload);
        return serverMessage.getMessageAsJSONString();
    }

    @Override
    public String getProjectData(String project) {
        JSONObject payload;
        Logger.log(project);
        if (currentConfig == null) {
            Logger.log("NO CONFIG FOUND", "PluginController");
            ServerMessage serverMessage = new ServerMessage("projectData", "{\"data\": \"none\"}");
            return serverMessage.getMessageAsJSONString();
        } else {
            if (project.equals("current")) {
                project = currentProject.getBasePath();
            } else {
                project = db.getSelectedProjectPath(project);
            }

            try {
                payload = db.getProjectData(project);
            } catch (NullPointerException e) {
                ServerMessage serverMessage = new ServerMessage("projectData", "{\"data\": \"none\"}");
                return serverMessage.getMessageAsJSONString();
            }
            try {
                payload.put("config", currentConfig.getConfigJSON());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ServerMessage serverMessage = new ServerMessage("projectData", payload.toString());
            return serverMessage.getMessageAsJSONString();
        }
    }

    @Override
    public String getGraphData() {
        String payload = "";
        payload = payload + db.getGraphData().toString();

        ServerMessage serverMessage = new ServerMessage("graphData", payload);
        return serverMessage.getMessageAsJSONString();
    }
}
