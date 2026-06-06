package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.entities.Sale;
import org.example.services.interfaces.VideoGameService;

public class SaleFormView {

    private final Stage owner;
    private final VideoGameService service;
    private final Runnable onSaved;

    public SaleFormView(Stage owner, VideoGameService service, Runnable onSaved) {
        this.owner   = owner;
        this.service = service;
        this.onSaved = onSaved;
    }

    public void show() {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Realizar Venta");
        stage.setResizable(false);

        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Título exacto del juego");

        TextField txtQty = new TextField();
        txtQty.setPromptText("Cantidad");

        Button btnVender   = new Button(" Vender");
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> stage.close());

        btnVender.setOnAction(e -> {
            String title = txtTitle.getText().trim();
            if (title.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "El título no puede estar vacío.");
                return;
            }
            int qty;
            try {
                qty = Integer.parseInt(txtQty.getText().trim());
                if (qty <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "La cantidad debe ser un número entero positivo.");
                return;
            }

            try {
                Sale sale = service.sellVideoGame(title, qty);
                showAlert(Alert.AlertType.INFORMATION,
                        " Venta realizada correctamente!\n" + "Juego: " + sale.getVideoGame().getTitle() + "\n" + "Cantidad: " + sale.getQuantity() + "\n" + "Total: $" + sale.getTotal());

                onSaved.run();
                stage.close();

            } catch (IllegalArgumentException | IllegalStateException ex) {showAlert(Alert.AlertType.WARNING, ex.getMessage());
            }
        });

        HBox btnBox = new HBox(10, btnVender, btnCancelar);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Título del juego:"), 0, 0); grid.add(txtTitle, 1, 0);
        grid.add(new Label("Cantidad:"),0, 1); grid.add(txtQty,   1, 1);
        grid.add(btnBox,0, 2, 2, 1);

        ColumnConstraints c1 = new ColumnConstraints(130);
        ColumnConstraints c2 = new ColumnConstraints(200);
        grid.getColumnConstraints().addAll(c1, c2);

        stage.setScene(new Scene(grid));
        stage.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg, ButtonType.OK).showAndWait();
    }
}
