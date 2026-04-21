import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.swing.SwingUtilities;

public class ProteinUI extends Application {

    private ProteinViewer3D viewer;

    @Override
    public void start(Stage stage) {

        TextField searchField = new TextField();
        searchField.setPromptText("Enter protein name or PDB ID");

        Button searchBtn = new Button("Load Protein");

        Label status = new Label();
        TextArea infoArea = new TextArea();
        TextArea seqArea = new TextArea();

        infoArea.setEditable(false);
        seqArea.setEditable(false);

        // 🔥 SWING NODE (FIXED)
        SwingNode swingNode = new SwingNode();
        createSwingContent(swingNode);

        // 🔥 FORCE SIZE (CRITICAL FIX)
        StackPane viewerWrapper = new StackPane(swingNode);
        viewerWrapper.setStyle("-fx-background-color: black;");
        viewerWrapper.setMinSize(800, 600);


        // BUTTON ACTION
        searchBtn.setOnAction(e -> {

            String query = searchField.getText().trim();

            if (query.isEmpty()) {
                status.setText("Enter a protein name");
                return;
            }

            // CASE 1: PDB ID
            if (query.length() == 4 && query.matches("[a-zA-Z0-9]+")) {

                viewer.loadProtein(query.toUpperCase());

                infoArea.setText("Loaded directly using PDB ID");
                seqArea.setText("");
                status.setText("Loaded: " + query.toUpperCase());

                return;
            }

            // CASE 2: NAME SEARCH
            status.setText("Searching...");

            new Thread(() -> {

                ProteinModel protein = ProteinAPI.fetchProtein(query);

                Platform.runLater(() -> {

                    if (protein != null) {

                        viewer.loadProtein(protein.getPdbId());

                        infoArea.setText(
                                "Protein Name: " + protein.getName() + "\n\n" +
                                "PDB ID: " + protein.getPdbId() + "\n\n" +
                                protein.getInfo()
                        );

                        seqArea.setText(protein.getSequence());

                        status.setText("Loaded: " + protein.getPdbId());

                    } else {
                        status.setText("Protein not found");
                    }
                });
            }).start();
        });

        VBox leftPanel = new VBox(10,
                searchField,
                searchBtn,
                status,
                new Label("Protein Info"),
                infoArea,
                new Label("Sequence"),
                seqArea
        );

        leftPanel.setPrefWidth(300);

        // 🔥 MAIN LAYOUT FIX
        BorderPane root = new BorderPane();
        root.setLeft(leftPanel);
        root.setCenter(viewerWrapper);

        // 🔥 ENSURE CENTER EXPANDS
        BorderPane.setMargin(viewerWrapper, new javafx.geometry.Insets(0));

        Scene scene = new Scene(root, 1200, 700);

        stage.setTitle("Protein 3D Viewer");
        stage.setScene(scene);
        stage.show();
    }

    // 🔥 CRITICAL
    private void createSwingContent(SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            viewer = new ProteinViewer3D();

            // 🔥 FORCE SIZE HERE ALSO
            viewer.setPreferredSize(new java.awt.Dimension(1000, 700));

            swingNode.setContent(viewer);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}