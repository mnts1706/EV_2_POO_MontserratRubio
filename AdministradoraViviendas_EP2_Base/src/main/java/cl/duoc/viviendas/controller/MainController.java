package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.repository.ViviendaRepository;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainController {

    @FXML private ComboBox<String> cmbTipo;

    @FXML private TextField txtCodigo;
    @FXML private TextField txtSuperficie;
    @FXML private TextField txtHabitaciones;
    @FXML private TextField txtPiso;

    @FXML private CheckBox chkGastoComun;
    @FXML private CheckBox chkEstacionamiento;
    @FXML private CheckBox chkPatio;

    @FXML private TableView<Vivienda> tablaViviendas;
    @FXML private TableColumn<Vivienda, String> colTipo;
    @FXML private TableColumn<Vivienda, String> colCodigo;
    @FXML private TableColumn<Vivienda, Number> colSuperficie;
    @FXML private TableColumn<Vivienda, Number> colHabitaciones;
    @FXML private TableColumn<Vivienda, String> colDatosEspecificos;
    @FXML private TableColumn<Vivienda, String> colCosto;

    @FXML private Label lblMensaje;

    private ViviendaRepository repository;

    private final ObservableList<Vivienda> listaViviendas =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        repository = new ViviendaRepository();

        cmbTipo.setItems(FXCollections.observableArrayList(
                "Departamento",
                "Casa"
        ));

        configurarTabla();

        actualizarTabla();

        cambiarTipo();
    }

    private void configurarTabla() {

            colTipo.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(
                            data.getValue().getTipo()
                    )
            );

            colCodigo.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(
                            data.getValue().getCodigo()
                    )
            );

            colSuperficie.setCellValueFactory(data ->
                    new ReadOnlyDoubleWrapper(
                            data.getValue().getSuperficie()
                    )
            );

            colHabitaciones.setCellValueFactory(data ->
                    new ReadOnlyIntegerWrapper(
                            data.getValue().getHabitaciones()
                    )
            );

            colDatosEspecificos.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(
                            obtenerDatosEspecificos(data.getValue())
                    )
            );

            colCosto.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(
                            formatearCosto(data.getValue().calcularCostoArriendo())
                    )
            );

            tablaViviendas.setItems(listaViviendas);
        }

        private String obtenerDatosEspecificos(Vivienda vivienda) {

            if (vivienda instanceof Departamento) {

                Departamento departamento = (Departamento) vivienda;

                String gastoComun = departamento.isGastoComunAlDia()
                        ? "Sí" : "No";

                String estacionamiento =
                        departamento.tieneEstacionamientoAsignado()
                                ? "Sí" : "No";

                return "Piso: " + departamento.getPiso()
                        + " | Gasto común al día: " + gastoComun
                        + " | Estacionamiento: " + estacionamiento;
            }

            if (vivienda instanceof Casa) {

                Casa casa = (Casa) vivienda;

                return "Patio: " + (casa.isTienePatio() ? "Sí" : "No");
            }

            return "";
        }

        private String formatearCosto(double costo) {

            java.text.NumberFormat formato =
                    java.text.NumberFormat.getIntegerInstance(
                            new java.util.Locale("es", "CL")
                    );

            return "$" + formato.format(costo);
        }


    @FXML
    private void cambiarTipo() {

        String tipo = cmbTipo.getValue();

        boolean esDepartamento = "Departamento".equals(tipo);

        txtPiso.setDisable(!esDepartamento);
        chkGastoComun.setDisable(!esDepartamento);
        chkEstacionamiento.setDisable(!esDepartamento);

        chkPatio.setDisable(esDepartamento);

        if (esDepartamento) {
            chkPatio.setSelected(false);
        } else {
            txtPiso.clear();
            chkGastoComun.setSelected(false);
            chkEstacionamiento.setSelected(false);
        }
    }

    @FXML
    private void registrarVivienda() {

        try {

            String tipo = cmbTipo.getValue();

            if (tipo == null) {
                throw new IllegalArgumentException(
                        "Debe seleccionar el tipo de vivienda."
                );
            }

            String codigo = txtCodigo.getText().trim();

            double superficie = Double.parseDouble(
                    txtSuperficie.getText().trim()
            );

            int habitaciones = Integer.parseInt(
                    txtHabitaciones.getText().trim()
            );

            if ("Departamento".equals(tipo)) {

                int piso = Integer.parseInt(
                        txtPiso.getText().trim()
                );

                if (piso <= 0) {
                    throw new IllegalArgumentException(
                            "El piso debe ser mayor que cero."
                    );
                }

                Departamento departamento = new Departamento(
                        codigo,
                        superficie,
                        habitaciones,
                        piso,
                        chkGastoComun.isSelected()
                );

                if (chkEstacionamiento.isSelected()) {
                    departamento.asignarEstacionamiento();
                }

                repository.agregarDepartamento(departamento);

            } else {

                Casa casa = new Casa(
                        codigo,
                        superficie,
                        habitaciones,
                        chkPatio.isSelected()
                );

                repository.agregarCasa(casa);
            }

            actualizarTabla();
            limpiarFormulario();

            lblMensaje.setText(
                    "Vivienda registrada correctamente."
            );

        } catch (NumberFormatException e) {

            lblMensaje.setText(
                    "Error: revise que superficie, habitaciones y piso sean números válidos."
            );

        } catch (RuntimeException e) {

            lblMensaje.setText(
                    "Error: " + e.getMessage()
            );
        }
    }

    @FXML
    private void eliminarVivienda() {

        Vivienda seleccionada =
                tablaViviendas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            lblMensaje.setText(
                    "Debe seleccionar una vivienda para eliminar."
            );
            return;
        }

        try {

            repository.eliminarVivienda(
                    seleccionada.getCodigo()
            );

            actualizarTabla();

            lblMensaje.setText(
                    "Vivienda eliminada correctamente."
            );

        } catch (RuntimeException e) {

            lblMensaje.setText(
                    "Error al eliminar: " + e.getMessage()
            );
        }
    }

    private void actualizarTabla() {

        listaViviendas.setAll(
                repository.obtenerTodasLasViviendas()
        );
    }

    private void limpiarFormulario() {

        txtCodigo.clear();
        txtSuperficie.clear();
        txtHabitaciones.clear();
        txtPiso.clear();

        chkGastoComun.setSelected(false);
        chkEstacionamiento.setSelected(false);
        chkPatio.setSelected(false);

        cmbTipo.setValue(null);

        cambiarTipo();
    }
}
