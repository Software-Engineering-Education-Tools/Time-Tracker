package visualizer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import constants.Constants;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class VisualizerWindowFactory implements ToolWindowFactory {

    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        File visualizerContent;
        try {
            if (Constants.DEBUG_MODE) {
                visualizerContent = new File("C:\\Users\\melco\\OneDrive\\Dokumente\\BA\\Implementierung\\timetracker\\plugin\\src\\visualizer\\www\\index.html");
            } else {
                URI url = VisualizerWindowFactory.class.getResource("/visualizer/www/index.html").toURI();
                visualizerContent = new File(url);
            }

            VisualizerWindow visualizerWindow = new VisualizerWindow(visualizerContent);
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            Content content = contentFactory.createContent(visualizerWindow.getContent(), "", false);
            toolWindow.getContentManager().addContent(content);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}