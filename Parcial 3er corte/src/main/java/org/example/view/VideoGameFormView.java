package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.entities.DigitalVideoGame;
import org.example.entities.PhysicalVideoGame;
import org.example.entities.VideoGame;
import org.example.services.interfaces.VideoGameService;

public class VideoGameFormView {

    private final Stage           owner;
    private final VideoGameService service;
    private final Runnable        onSaved;
    private final VideoGame       toEdit;

    public VideoGameFormView(Stage owner, VideoGameService service,
                             Runnable onSaved, VideoGame toEdit) {
        this.owner   = owner;
        this.service = service;
        this.onSaved = onSaved;
        this.toEdit  = toEdit;
    }

    public void show() {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(toEdit == null ? "Agregar Videojuego" : "Editar Videojuego");
        stage.setResizable(false);

        // ── Tipo ──────────────────────────────────────────────────────────
        ComboBox<String> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll("Digital", "Físico");
        cmbTipo.setValue(toEdit instanceof PhysicalVideoGame ? "Físico" : "Digital");

        // ── Campos comunes ────────────────────────────────────────────────
        TextField txtTitle    = new TextField(toEdit != null ? toEdit.getTitle()    : "");
        TextField txtPrice    = new TextField(toEdit != null ? String.valueOf(toEdit.getPrice())   : "");
        TextField txtPlatform = new TextField(toEdit != null ? toEdit.getPlatform() : "");
        TextField txtStock    = new TextField(toEdit != null ? String.valueOf(toEdit.getStock())   : "");
        TextField txtGenre    = new TextField(toEdit != null ? toEdit.getGenre()    : "");

        // ── Campos Digital ────────────────────────────────────────────────
        Label    lblSizeGB    = new Label("Tamaño (GB):");
        TextField txtSizeGB   = new TextField();
        Label    lblDlPlat    = new Label("Plataforma descarga:");
        TextField txtDlPlat   = new TextField();

        // ── Campos Físico ─────────────────────────────────────────────────
        Label    lblCond      = new Label("Condición (nuevo/usado):");
        TextField txtCond     = new TextField();
        Label    lblDist      = new Label("Distribuidor:");
        TextField txtDist     = new TextField();

        // Pre-fill si es edición
        if (toEdit instanceof DigitalVideoGame d) {
            txtSizeGB.setText(String.valueOf(d.getSizeGB()));
            txtDlPlat.setText(d.getDownloadPlatform());
        }
        if (toEdit instanceof PhysicalVideoGame p) {
            txtCond.setText(p.getCondition());
            txtDist.setText(p.getDistributor());
        }

        // ── Grid principal ────────────────────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Tipo:"),       0, 0); grid.add(cmbTipo,    1, 0);
        grid.add(new Label("Título:"),     0, 1); grid.add(txtTitle,   1, 1);
        grid.add(new Label("Precio:"),     0, 2); grid.add(txtPrice,   1, 2);
        grid.add(new Label("Plataforma:"), 0, 3); grid.add(txtPlatform,1, 3);
        grid.add(new Label("Stock:"),      0, 4); grid.add(txtStock,   1, 4);
        grid.add(new Label("Género:"),     0, 5); grid.add(txtGenre,   1, 5);

        // Filas dinámicas (6-9)
        grid.add(lblSizeGB, 0, 6); grid.add(txtSizeGB, 1, 6);
        grid.add(lblDlPlat, 0, 7); grid.add(txtDlPlat, 1, 7);
        grid.add(lblCond,   0, 6); grid.add(txtCond,   1, 6);
        grid.add(lblDist,   0, 7); grid.add(txtDist,   1, 7);


        Runnable toggleFields = () -> {
            boolean isDigital = "Digital".equals(cmbTipo.getValue());
            lblSizeGB.setVisible(isDigital);
            txtSizeGB.setVisible(isDigital);
            lblDlPlat.setVisible(isDigital);
            txtDlPlat.setVisible(isDigital);
            lblCond.setVisible(!isDigital);
            txtCond.setVisible(!isDigital);
            lblDist.setVisible(!isDigital);
            txtDist.setVisible(!isDigital);
        };
        toggleFields.run();
        cmbTipo.setOnAction(e -> toggleFields.run());


        Button btnGuardar  = new Button(" Guardar");
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> stage.close());

        btnGuardar.setOnAction(e -> {
            try {
                String  title    = txtTitle.getText().trim();
                double  price    = Double.parseDouble(txtPrice.getText().trim());
                String  platform = txtPlatform.getText().trim();
                int     stock    = Integer.parseInt(txtStock.getText().trim());
                String  genre    = txtGenre.getText().trim();

                VideoGame vg;
                if ("Digital".equals(cmbTipo.getValue())) {
                    double sizeGB = Double.parseDouble(txtSizeGB.getText().trim());
                    String dlPlat = txtDlPlat.getText().trim();
                    vg = new DigitalVideoGame(title, price, platform, stock, genre, sizeGB, dlPlat);
                } else {
                    String cond = txtCond.getText().trim();
                    String dist = txtDist.getText().trim();
                    vg = new PhysicalVideoGame(title, price, platform, stock, genre, cond, dist);
                }

                if (toEdit == null) {
                    service.addVideoGame(vg);
                } else {
                    service.update(toEdit.getTitle(), vg);
                }

                new Alert(Alert.AlertType.INFORMATION,
                        "¡Videojuego guardado correctamente!", ButtonType.OK).showAndWait();
                onSaved.run();
                stage.close();

            } catch (IllegalStateException ex) {
                new Alert(Alert.AlertType.WARNING, ex.getMessage(), ButtonType.OK).showAndWait();
            } catch (IllegalArgumentException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
            }
        });

        HBox btnBox = new HBox(10, btnGuardar, btnCancelar);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(btnBox, 0, 8, 2, 1);

        ColumnConstraints c1 = new ColumnConstraints(170);
        ColumnConstraints c2 = new ColumnConstraints(200);
        grid.getColumnConstraints().addAll(c1, c2);

        stage.setScene(new Scene(grid));
        stage.showAndWait();
    }
}
