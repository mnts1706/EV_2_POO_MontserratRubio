package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

    public class ViviendaDAO {

        private final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        private final Path archivoDepartamentos =
                Path.of("departamentos.json");

        private final Path archivoCasas =
                Path.of("casas.json");


        public List<Departamento> cargarDepartamentos() {

            if (!Files.exists(archivoDepartamentos)) {
                return new ArrayList<>();
            }

            try {
                String json = Files.readString(archivoDepartamentos);

                if (json.isBlank()) {
                    return new ArrayList<>();
                }

                Type tipo = new TypeToken<List<Departamento>>() {}.getType();

                List<Departamento> departamentos = gson.fromJson(json, tipo);

                return departamentos == null
                        ? new ArrayList<>()
                        : departamentos;

            } catch (IOException e) {
                throw new IllegalStateException(
                        "Error al leer departamentos.json", e);
            }
        }


        public List<Casa> cargarCasas() {

            if (!Files.exists(archivoCasas)) {
                return new ArrayList<>();
            }

            try {
                String json = Files.readString(archivoCasas);

                if (json.isBlank()) {
                    return new ArrayList<>();
                }

                Type tipo = new TypeToken<List<Casa>>() {}.getType();

                List<Casa> casas = gson.fromJson(json, tipo);

                return casas == null
                        ? new ArrayList<>()
                        : casas;

            } catch (IOException e) {
                throw new IllegalStateException(
                        "Error al leer casas.json",  e);
            }
        }

        public void guardarDepartamentos(List<Departamento> departamentos) {

            try {
                String json = gson.toJson(departamentos);

                Files.writeString(archivoDepartamentos, json);

            } catch (IOException e) {
                throw new IllegalStateException(
                        "Error al guardar departamentos.json", e);
            }
        }


        public void guardarCasas(List<Casa> casas) {

            try {
                String json = gson.toJson(casas);

                Files.writeString(archivoCasas, json);

            } catch (IOException e) {
                throw new IllegalStateException(
                        "Error al guardar casas.json", e);
            }
        }
    }

