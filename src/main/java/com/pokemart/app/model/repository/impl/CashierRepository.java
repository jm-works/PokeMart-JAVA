package com.pokemart.app.model.repository.impl;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

public class CashierRepository {

    private static final String PATH = "data/cashier.json";
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
