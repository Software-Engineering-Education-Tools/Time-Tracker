import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.ProjectManager;
import filesystem.FilesystemHelper;

public class Main implements ProjectComponent {
    PluginController pluginController;

    @Override
    public void initComponent() {
        initFilesystem();
        initPlugin();
    }

    private void initFilesystem() {
        FilesystemHelper.createAplicationDirectory();
    }

    private void initPlugin() {
        pluginController = new PluginController();
        ProjectManager.getInstance().addProjectManagerListener(pluginController);
    }

    @Override
    public void disposeComponent() {
        pluginController.shutdown();
    }
}

