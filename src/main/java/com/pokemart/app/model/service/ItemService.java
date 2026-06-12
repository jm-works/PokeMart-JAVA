package com.pokemart.app.model.service;

import com.pokemart.app.model.dto.ItemDto;
import com.pokemart.app.model.entity.Item;
import com.pokemart.app.model.repository.impl.ItemRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

public class ItemService {

    private final ItemRepository repository;
    private static final String IMAGES_DIR = "data/images";

    public ItemService() {
        this.repository = new ItemRepository();
        new File(IMAGES_DIR).mkdirs();
    }

    private static final java.math.BigDecimal MAX_PRICE = new java.math.BigDecimal("2000");

    public Item save(ItemDto dto) {
        validatePrice(dto);
        Item entity = toEntity(dto, null);
        return repository.save(entity);
    }

    public Item update(String id, ItemDto dto) {
        validatePrice(dto);
        Optional<Item> existing = repository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Item não encontrado.");
        }
        if (dto.getImagePath() == null || dto.getImagePath().isBlank()) {
            dto = new ItemDto(
                    dto.getBarcode(), dto.getName(), dto.getCategory(),
                    dto.getPrice(), dto.getStock(), existing.get().getImagePath()
            );
        }
        Item entity = toEntity(dto, id);
        return repository.save(entity);
    }

    public List<Item> findAll() {
        return repository.findAll();
    }

    public Optional<Item> findById(String id) {
        return repository.findById(id);
    }

    public void delete(String id) {
        repository.findById(id).ifPresent(item -> {
            if (item.getImagePath() != null && !item.getImagePath().isBlank()) {
                File img = new File(IMAGES_DIR + "/" + item.getImagePath());
                if (img.exists()) img.delete();
            }
        });
        repository.deleteById(id);
    }

    private void validatePrice(ItemDto dto) {
        if (dto.getPrice() == null || dto.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Preço não pode ser negativo.");
        if (dto.getPrice().compareTo(MAX_PRICE) > 0)
            throw new IllegalArgumentException("Preço máximo permitido é ₽ 2.000,00.");
    }

    private Item toEntity(ItemDto dto, String id) {
        String imagePath = copiarImagem(dto.getImagePath(), dto.getName());
        return Item.builder()
                .id(id)
                .barcode(dto.getBarcode())
                .name(dto.getName())
                .category(dto.getCategory())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .imagePath(imagePath)
                .build();
    }

    private String copiarImagem(String sourcePath, String itemName) {
        if (sourcePath == null || sourcePath.isBlank()) return null;

        File source = new File(sourcePath);

        if (!source.isAbsolute() && !sourcePath.contains("/") && !sourcePath.contains("\\")) {
            return sourcePath;
        }

        if (!source.exists() || !source.isFile()) return null;

        File imagesDir = new File(IMAGES_DIR).getAbsoluteFile();
        if (source.getParentFile().getAbsolutePath().equals(imagesDir.getAbsolutePath())) {
            return source.getName();
        }

        try {
            String original = source.getName();
            String ext = original.contains(".")
                    ? original.substring(original.lastIndexOf('.'))
                    : ".png";

            String safeName = itemName
                    .toLowerCase()
                    .replaceAll("[^a-z0-9_\\-]", "_")
                    .replaceAll("_+", "_");

            String fileName = safeName + ext;
            Path dest = Paths.get(IMAGES_DIR, fileName);
            Files.copy(source.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            System.err.println("Aviso: nao foi possivel copiar imagem — " + e.getMessage());
            return null;
        }
    }
}