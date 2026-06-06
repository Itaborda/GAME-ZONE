package org.example.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.entities.Sale;
import org.example.entities.VideoGame;
import org.example.services.impl.VideoGameServiceImpl;
import org.example.services.interfaces.VideoGameService;

import java.util.List;

public class MainView {

    private final VideoGameService service = new VideoGameServiceImpl();

    private ObservableList<VideoGame> catalogData;

    private ObservableList<Sale> salesData;



    private TableView<VideoGame> catalogTable;

    public void launchView(Stage stage) {
        stage.setTitle("GameZone- AISA");


        Tab tabCatalogo = new Tab("Catálogo");
        tabCatalogo.setClosable(false);
        tabCatalogo.setContent(buildCatalogTab(stage));


        Tab tabBuscar = new Tab("Buscar");
        tabBuscar.setClosable(false);
        tabBuscar.setContent(buildSearchTab());

        Tab tabVender = new Tab("Vender");
        tabVender.setClosable(false);
        tabVender.setContent(new Pane()); // contenido vacío

        tabVender.setOnSelectionChanged(e -> {
            if (tabVender.isSelected()) {
                new SaleFormView(stage, service, this::refreshAll).show();
            }
        });


        Tab tabVentas = new Tab("Ventas");
        tabVentas.setClosable(false);
        tabVentas.setContent(buildSalesTab());

        TabPane tabPane = new TabPane(tabCatalogo, tabBuscar, tabVender, tabVentas);

        Scene scene = new Scene(tabPane, 780, 520);
        stage.setScene(scene);
        stage.show();
    }



    private Pane buildCatalogTab(Stage owner) {

        catalogTable = new TableView<>();

        TableColumn<VideoGame, String> colTitle    = new TableColumn<>("Título");
        TableColumn<VideoGame, String> colPlatform = new TableColumn<>("Plataforma");
        TableColumn<VideoGame, String> colGenre    = new TableColumn<>("Género");
        TableColumn<VideoGame, Double> colPrice    = new TableColumn<>("Precio Final");
        TableColumn<VideoGame, Integer> colStock   = new TableColumn<>("Stock");

        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colPlatform.setCellValueFactory(new PropertyValueFactory<>("platform"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colPrice.setCellValueFactory(d ->
                new javafx.beans.property.SimpleDoubleProperty(
                        d.getValue().calculateFinalPrice()).asObject());
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        catalogTable.getColumns().addAll(colTitle, colPlatform, colGenre, colPrice, colStock);

        catalogData = FXCollections.observableArrayList(service.findAll());
        catalogTable.setItems(catalogData);

        // Botones
        Button btnAgregar  = new Button("+ Agregar");
        Button btnEditar   = new Button("Editar");
        Button btnEliminar = new Button(" Eliminar");

        btnAgregar.setOnAction(e -> {
            new VideoGameFormView(owner, service, this::refreshCatalog, null).show();
        });

        btnEditar.setOnAction(e -> {
            VideoGame selected = catalogTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Selecciona un videojuego para editar.");
                return;
            }
            new VideoGameFormView(owner, service, this::refreshCatalog, selected).show();
        });

        btnEliminar.setOnAction(e -> {
            VideoGame selected = catalogTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Selecciona un videojuego para eliminar.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar \"" + selected.getTitle() + "\"?",
                    ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    service.delete(selected.getTitle());
                    showAlert(Alert.AlertType.INFORMATION, "Videojuego eliminado correctamente.");
                    refreshCatalog();
                }
            });
        });

        HBox toolbar = new HBox(10, btnAgregar, btnEditar, btnEliminar);
        toolbar.setPadding(new Insets(10));
        toolbar.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(5, toolbar, catalogTable);
        VBox.setVgrow(catalogTable, Priority.ALWAYS);
        root.setPadding(new Insets(10));
        return root;
    }


    private Pane buildSearchTab() {

        TextField txtSearch  = new TextField();
        txtSearch.setPromptText("Título del videojuego ");
        Button btnByTitle    = new Button("Buscar por título");

        TextField txtPlatform = new TextField();
        txtPlatform.setPromptText("Nombre de plataforma...");
        Button btnByPlatform  = new Button("Buscar por plataforma");

        TableView<VideoGame> resultTable = new TableView<>();

        TableColumn<VideoGame, String>  rTitle    = new TableColumn<>("Título");
        TableColumn<VideoGame, String>  rPlatform = new TableColumn<>("Plataforma");
        TableColumn<VideoGame, String>  rGenre    = new TableColumn<>("Género");
        TableColumn<VideoGame, Double>  rPrice    = new TableColumn<>("Precio Final");
        TableColumn<VideoGame, Integer> rStock    = new TableColumn<>("Stock");

        rTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        rPlatform.setCellValueFactory(new PropertyValueFactory<>("platform"));
        rGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        rPrice.setCellValueFactory(d ->
                new javafx.beans.property.SimpleDoubleProperty(
                        d.getValue().calculateFinalPrice()).asObject());
        rStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        resultTable.getColumns().addAll(rTitle, rPlatform, rGenre, rPrice, rStock);

        btnByTitle.setOnAction(e -> {
            String q = txtSearch.getText().trim();
            if (q.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Escribe un título para buscar.");
                return;
            }
            var opt = service.findByTitle(q);
            if (opt.isPresent()) {
                resultTable.setItems(FXCollections.observableArrayList(opt.get()));
            } else {
                resultTable.setItems(FXCollections.observableArrayList());
                showAlert(Alert.AlertType.INFORMATION, "No se encontró ningún juego con ese título.");
            }
        });

        btnByPlatform.setOnAction(e -> {
            String p = txtPlatform.getText().trim();
            if (p.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Escribe una plataforma para buscar.");
                return;
            }
            List<VideoGame> found = service.findByPlatform(p);
            if (found.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No se encontraron juegos para esa plataforma.");
            }
            resultTable.setItems(FXCollections.observableArrayList(found));
        });

        HBox row1 = new HBox(10, new Label("Título:"), txtSearch, btnByTitle);
        HBox row2 = new HBox(10, new Label("Plataforma:"), txtPlatform, btnByPlatform);
        row1.setAlignment(Pos.CENTER_LEFT);
        row2.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(10, row1, row2, resultTable);
        VBox.setVgrow(resultTable, Priority.ALWAYS);
        root.setPadding(new Insets(15));
        return root;
    }


    private Pane buildSellTab(Stage owner) {
        Button btnVender = new Button(" Realizar nueva venta");
        btnVender.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");

        btnVender.setOnAction(e ->
                new SaleFormView(owner, service, this::refreshAll).show());

        VBox root = new VBox(20, btnVender);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        return root;
    }



    private Pane buildSalesTab() {

        TableView<Sale> salesTable = new TableView<>();

        TableColumn<Sale, String> sId    = new TableColumn<>("ID");
        TableColumn<Sale, String> sGame  = new TableColumn<>("Juego");
        TableColumn<Sale, Integer> sQty  = new TableColumn<>("Cantidad");
        TableColumn<Sale, Double>  sUnit = new TableColumn<>("Precio unit.");
        TableColumn<Sale, Double>  sTotal= new TableColumn<>("Total");
        TableColumn<Sale, String>  sDate = new TableColumn<>("Fecha");

        sId.setCellValueFactory(new PropertyValueFactory<>("id"));
        sGame.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getVideoGame().getTitle()));
        sQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        sUnit.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        sTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        sDate.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getSaleDate().toString()));

        salesTable.getColumns().addAll(sId, sGame, sQty, sUnit, sTotal, sDate);

        salesData = FXCollections.observableArrayList(service.findAllSales());
        salesTable.setItems(salesData);

        Button btnRefresh = new Button("Actualizar");
        btnRefresh.setOnAction(e -> salesData.setAll(service.findAllSales()));

        VBox root = new VBox(8, btnRefresh, salesTable);
        VBox.setVgrow(salesTable, Priority.ALWAYS);
        root.setPadding(new Insets(10));
        return root;
    }


    private void refreshCatalog() {
        catalogData.setAll(service.findAll());
    }

    private void refreshAll() {
        catalogData.setAll(service.findAll());
        if (salesData != null) salesData.setAll(service.findAllSales());
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg, ButtonType.OK).showAndWait();
    }
}
