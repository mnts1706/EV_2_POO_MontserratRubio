package cl.duoc.viviendas.repository;

import cl.duoc.viviendas.dao.ViviendaDAO;
import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;

import java.util.ArrayList;
import java.util.List;

public class ViviendaRepository {

    private final ViviendaDAO dao;

    private final List<Departamento> departamentos;
    private final List<Casa> casas;


    public ViviendaRepository() {

        dao = new ViviendaDAO();

        departamentos = dao.cargarDepartamentos();
        casas = dao.cargarCasas();
    }


    public List<Vivienda> obtenerTodasLasViviendas() {

        List<Vivienda> viviendas = new ArrayList<>();

        viviendas.addAll(departamentos);
        viviendas.addAll(casas);

        return viviendas;
    }


    public void agregarDepartamento(Departamento departamento) {

        validarCodigoDisponible(departamento.getCodigo());

        departamentos.add(departamento);

        try {
            dao.guardarDepartamentos(departamentos);
        } catch (RuntimeException e) {
            departamentos.remove(departamento);
            throw e;
        }
    }

    public void agregarCasa(Casa casa) {

        validarCodigoDisponible(casa.getCodigo());

        casas.add(casa);

        try {
            dao.guardarCasas(casas);
        } catch (RuntimeException e) {
            casas.remove(casa);
            throw e;
        }
    }


    private void validarCodigoDisponible(String codigo) {

        boolean existeDepartamento = departamentos.stream()
                .anyMatch(d -> d.getCodigo().equalsIgnoreCase(codigo));

        boolean existeCasa = casas.stream()
                .anyMatch(c -> c.getCodigo().equalsIgnoreCase(codigo));

        if (existeDepartamento || existeCasa) {
            throw new IllegalArgumentException(
                    "Ya existe una vivienda con ese código.");
        }
    }


    public boolean eliminarVivienda(String codigo) {

        for (int i = 0; i < departamentos.size(); i++) {

            if (departamentos.get(i).getCodigo().equalsIgnoreCase(codigo)) {

                Departamento eliminado = departamentos.remove(i);

                try {
                    dao.guardarDepartamentos(departamentos);
                } catch (RuntimeException e) {
                    departamentos.add(i, eliminado);
                    throw e;
                }

                return true;
            }
        }

        for (int i = 0; i < casas.size(); i++) {

            if (casas.get(i).getCodigo().equalsIgnoreCase(codigo)) {

                Casa eliminada = casas.remove(i);

                try {
                    dao.guardarCasas(casas);
                } catch (RuntimeException e) {
                    casas.add(i, eliminada);
                    throw e;
                }

                return true;
            }
        }

        return false;
    }


    public List<Departamento> obtenerDepartamentos() {
        return new ArrayList<>(departamentos);
    }


    public List<Casa> obtenerCasas() {
        return new ArrayList<>(casas);
    }
}