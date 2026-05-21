import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Client extends Application {

    VBox chatBox = new VBox(10);
    ScrollPane scroll = new ScrollPane(chatBox);

    TextField msg = new TextField();

    DataInputStream in;
    DataOutputStream out;

    @Override
    public void start(Stage stage) {

        scroll.setFitToWidth(true);
        scroll.setPrefHeight(350);

        msg.setPromptText("Type message...");

        Button send = new Button("Send");
        Button img = new Button("Image");

        HBox bottom = new HBox(10, msg, send, img);
        VBox root = new VBox(10, scroll, bottom);

        stage.setScene(new Scene(root, 400, 450));
        stage.setTitle("CLIENT");
        stage.show();

        send.setOnAction(e -> sendText());
        img.setOnAction(e -> sendImage(stage));

        new Thread(this::connect).start();
    }

    void connect() {
        try {

            Socket socket = new Socket("localhost", 5000);

            append("Connected to server");

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            while (true) {

                String type = in.readUTF();

                if (type.equals("TEXT")) {

                    String text = in.readUTF();
                    append("SERVER: " + text);

                } else if (type.equals("IMAGE")) {

                    int size = in.readInt();
                    byte[] data = new byte[size];
                    in.readFully(data);

                    showImage("SERVER", data);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendText() {
        try {

            String text = msg.getText();

            append("CLIENT: " + text);

            out.writeUTF("TEXT");
            out.writeUTF(text);

            msg.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendImage(Stage stage) {
        try {

            FileChooser fc = new FileChooser();
            File file = fc.showOpenDialog(stage);

            if (file != null) {

                byte[] data = Files.readAllBytes(file.toPath());

                showImage("CLIENT", data);

                out.writeUTF("IMAGE");
                out.writeInt(data.length);
                out.write(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void append(String text) {
        Platform.runLater(() ->
                chatBox.getChildren().add(new Label(text))
        );
    }

    void showImage(String sender, byte[] data) {

        Platform.runLater(() -> {

            Label label = new Label(sender + " sent image:");

            Image img = new Image(new ByteArrayInputStream(data));

            ImageView view = new ImageView(img);
            view.setFitWidth(200);
            view.setPreserveRatio(true);

            VBox box = new VBox(5, label, view);

            chatBox.getChildren().add(box);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}