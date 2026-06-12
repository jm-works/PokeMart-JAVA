package com.pokemart.app.model.repository.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.pokemart.app.model.repository.JsonFileRepository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class JsonFileRepositoryImpl<T> implements JsonFileRepository<T> {

    protected final Gson gson;
    protected final File file;
    protected final Class<T> type;

    public JsonFileRepositoryImpl(Class<T> type, String fileName) {
        this.type = type;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (src, typeOfSrc, ctx) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                        (json, typeOfT, ctx) -> LocalDateTime.parse(json.getAsString()))
                .create();

        File dir = resolveDataDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        this.file = new File(dir, fileName);
        if (!this.file.exists()) {
            FileWriter writer = null;
            try {
                this.file.createNewFile();
                writer = new FileWriter(this.file);
                writer.write("[]");
            } catch (IOException e) {
                throw new RuntimeException("Erro ao criar arquivo JSON: " + this.file.getPath(), e);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ignored) {}
                }
            }
        }
    }

    @Override
    public List<T> findAll() {
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            Type listType = TypeToken.getParameterized(List.class, type).getType();
            List<T> result = gson.fromJson(reader, listType);
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Erro ao ler JSON: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {}
            }
        }
    }

    @Override
    public Optional<T> findById(String id) {
        return findAll().stream()
                .filter(entity -> id.equals(getEntityId(entity)))
                .findFirst();
    }

    @Override
    public T save(T entity) {
        List<T> all = findAll();
        String id = getEntityId(entity);
        boolean isUpdate = false;

        if (id == null || id.trim().isEmpty()) {
            id = UUID.randomUUID().toString();
            setEntityId(entity, id);
            all.add(entity);
        } else {
            for (int i = 0; i < all.size(); i++) {
                if (id.equals(getEntityId(all.get(i)))) {
                    all.set(i, entity);
                    isUpdate = true;
                    break;
                }
            }
            if (!isUpdate) {
                all.add(entity);
            }
        }

        writeToFile(all);
        return entity;
    }

    @Override
    public void deleteById(String id) {
        List<T> all = findAll();
        boolean removed = all.removeIf(entity -> id.equals(getEntityId(entity)));
        if (removed) {
            writeToFile(all);
        }
    }

    private static File resolveDataDir() {
        try {
            File jarFile = new File(
                    JsonFileRepositoryImpl.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI());
            File jarDir = jarFile.isFile() ? jarFile.getParentFile() : jarFile;
            File candidate = new File(jarDir, "data");
            if (jarDir.getName().equals("classes") || jarDir.getName().equals("target")) {
                File projectRoot = jarDir.getName().equals("classes")
                        ? jarDir.getParentFile().getParentFile()
                        : jarDir.getParentFile();
                candidate = new File(projectRoot, "data");
            }
            return candidate;
        } catch (Exception e) {
            return new File("data");
        }
    }

    private void writeToFile(List<T> data) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            gson.toJson(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao escrever no arquivo JSON", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {}
            }
        }
    }

    private String getEntityId(T entity) {
        try {
            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            return (String) idField.get(entity);
        } catch (Exception e) {
            throw new RuntimeException("A entidade " + type.getSimpleName() + " deve ter um campo 'id' do tipo String", e);
        }
    }

    private void setEntityId(T entity, String id) {
        try {
            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar UUID para o campo 'id' na entidade " + type.getSimpleName(), e);
        }
    }
}