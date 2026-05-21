import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

public class Main extends Application {

    private boolean darkMode = false;

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        //  TEXT EDITOR
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);

        //  HTML EDITOR
        HTMLEditor htmlEditor = new HTMLEditor();

        // TABS
        TabPane tabPane = new TabPane();

        Tab textTab = new Tab("Text Editor", textArea);
        Tab htmlTab = new Tab("HTML Editor", htmlEditor);

        textTab.setClosable(false);
        htmlTab.setClosable(false);

        tabPane.getTabs().addAll(textTab, htmlTab);

        // MENU
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");

        fileMenu.getItems().addAll(newFile, openFile, saveFile, new SeparatorMenuItem(), exit);

        Menu viewMenu = new Menu("View");
        MenuItem darkToggle = new MenuItem("Toggle Dark Mode");

        viewMenu.getItems().add(darkToggle);

        menuBar.getMenus().addAll(fileMenu, viewMenu);

        //  FILE CHOOSER
        FileChooser fileChooser = new FileChooser();

        // NEW
        newFile.setOnAction(e -> {
            textArea.clear();
            htmlEditor.setHtmlText("");
        });

        // OPEN
        openFile.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    String content = new String(Files.readAllBytes(file.toPath()));

                    if (file.getName().endsWith(".html") || file.getName().endsWith(".htm")) {
                        htmlEditor.setHtmlText(content);
                        tabPane.getSelectionModel().select(htmlTab);
                    } else {
                        textArea.setText(content);
                        tabPane.getSelectionModel().select(textTab);
                    }

                } catch (Exception ex) {
                    showAlert("Error opening file");
                }
            }
        });

        // SAVE
        saveFile.setOnAction(e -> {
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {

                    if (tabPane.getSelectionModel().getSelectedItem() == htmlTab) {
                        writer.write(htmlEditor.getHtmlText());
                    } else {
                        writer.write(textArea.getText());
                    }

                } catch (Exception ex) {
                    showAlert("Error saving file");
                }
            }
        });


        exit.setOnAction(e -> stage.close());

        //  DARK MODE
        darkToggle.setOnAction(e -> {
            darkMode = !darkMode;

            if (darkMode) {
                root.setStyle("-fx-background-color: #1e1e1e;");
                textArea.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: white;");
                menuBar.setStyle("-fx-background-color: #333333;");
                htmlEditor.setStyle("-fx-background-color: #2b2b2b;");
            } else {
                root.setStyle("");
                textArea.setStyle("");
                menuBar.setStyle("");
                htmlEditor.setStyle("");
            }
        });

        //  LAYOUT FIX
        root.setTop(menuBar);
        root.setCenter(tabPane);

        // SCENE
        Scene scene = new Scene(root, 900, 600);

        stage.setTitle("Notepad ");
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }

    public static void main(String[] args) {
        launch();
    }
}