package visualizer;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import java.io.File;
import java.net.MalformedURLException;

public class VisualizerWindow {

    private JFXPanel visualizerContent;

    public VisualizerWindow(File content) {
        createPanel(content);
    }

    private void createPanel(File content) {
        try {
            final String url = content.toURI().toURL().toString();
            visualizerContent = new JFXPanel();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    WebView webView = new WebView();
                    visualizerContent.setScene(new Scene(webView));
                    // webView.getEngine().load("http://www.stackoverflow.com/");
                    webView.getEngine().load(url);
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public JFXPanel getContent() {
        return visualizerContent;
    }
}