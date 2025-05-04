package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.entities.InfoLieu;
import tn.esprit.services.ServiceLieu;


public class EventsParLieuController {

    @FXML
    private TableView<InfoLieu> tableView;

    @FXML
    private TableColumn<InfoLieu, Integer> colLieuId;

    @FXML
    private TableColumn<InfoLieu, String> colLieuNom;

    @FXML
    private TableColumn<InfoLieu, String> colEventNom;

    @FXML
    private TableColumn<InfoLieu, Integer> colEventId;

    @FXML
    private TableColumn<InfoLieu, Integer> colReservationId;

    @FXML
    private TableColumn<InfoLieu, String> colDescription;

    private final ServiceLieu serviceLieu = new ServiceLieu();

    @FXML
    public void initialize() {
        configureColumns();
    }

    public void setLieuId(int lieuId) {
        ObservableList<InfoLieu> data = serviceLieu.getEvenementsParLieu(lieuId);
        tableView.setItems(data);
    }

    private void configureColumns() {
        colLieuId.setCellValueFactory(new PropertyValueFactory<>("idLieu"));
        colLieuNom.setCellValueFactory(new PropertyValueFactory<>("nomLieu"));
        colEventNom.setCellValueFactory(new PropertyValueFactory<>("nomEvenement"));
        colEventId.setCellValueFactory(new PropertyValueFactory<>("idEvenement"));
        colReservationId.setCellValueFactory(new PropertyValueFactory<>("idReservation"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("commentaires"));
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.close();
    }
}