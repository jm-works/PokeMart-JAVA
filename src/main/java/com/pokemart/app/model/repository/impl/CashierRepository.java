package com.pokemart.app.model.repository.impl;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

public class CashierRepository {

    private static final String PATH;
    static {
        String resolved = "data/cashier.json";
        try {
            java.io.File jarFile = new java.io.File(
                    CashierRepository.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI());
            java.io.File jarDir = jarFile.isFile() ? jarFile.getParentFile() : jarFile;
            java.io.File dataDir;
            if (jarDir.getName().equals("classes") || jarDir.getName().equals("target")) {
                java.io.File projectRoot = jarDir.getName().equals("classes")
                        ? jarDir.getParentFile().getParentFile()
                        : jarDir.getParentFile();
                dataDir = new java.io.File(projectRoot, "data");
            } else {
                dataDir = new java.io.File(jarDir, "data");
            }
            resolved = new java.io.File(dataDir, "cashier.json").getAbsolutePath();
        } catch (Exception ignored) {}
        PATH = resolved;
    }
    private final Gson gson = new Gson();

    public CashierRepository() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        File f = new File(PATH);
        if (!f.exists()) save(BigDecimal.ZERO);
    }

    public BigDecimal load() {
        try (FileReader r = new FileReader(PATH)) {
            CashierData data = gson.fromJson(r, CashierData.class);
            if (data == null || data.balance == null) return BigDecimal.ZERO;
            return new BigDecimal(data.balance);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public void save(BigDecimal balance) {
        CashierData data = new CashierData();
        data.balance = balance.toPlainString();
        try (FileWriter w = new FileWriter(PATH)) {
            gson.toJson(data, w);
        } catch (IOException e) {
            System.err.println("Aviso: nao foi possivel salvar caixa — " + e.getMessage());
        }
    }

    private static class CashierData {
        String balance;
    }
}