package filesystem;

import logger.Logger;

import java.io.File;
import java.io.IOException;

public class FilesystemHelper {

    public static final String APP_DIR = "TimeTracker";
    public static final String DB_NAME = "time-tracker.sqlite";

    public static void createAplicationDirectory() {
        File appDir = getAppDir();
        if (appDir.exists()) {
            Logger.log(appDir + " already exists", "FilesystemHelper");
        } else if (appDir.mkdirs()) {
            Logger.log(appDir + " was created", "FilesystemHelper");
        } else {
            Logger.log(appDir + " was not created", "FilesystemHelper");
        }
    }

    public static File getDatabaseFile() {
        String path = getAppDir().getAbsolutePath() + File.separator + DB_NAME;
        File dbFile = new File(path);
        if(dbFile.exists()) {
            return dbFile;
        } else {
            try {
                dbFile.createNewFile();
                return dbFile;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private static File getAppDir() {
        String path = System.getProperty("user.home") + File.separator + APP_DIR;
        return new File(path);
    }


}
