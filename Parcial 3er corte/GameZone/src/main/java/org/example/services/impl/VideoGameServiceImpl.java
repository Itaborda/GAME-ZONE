package org.example.services.impl;

import org.example.entities.Sale;
import org.example.entities.VideoGame;
import org.example.repository.VideoGameRepository;
import org.example.repository.VideoGameRepositoryImpl;
import org.example.services.interfaces.VideoGameService;
import org.example.utils.JsonUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VideoGameServiceImpl implements VideoGameService {

    private final VideoGameRepository repository = new VideoGameRepositoryImpl();

    @Override
    public void addVideoGame(VideoGame videoGame) {

        if (videoGame.getTitle() == null || videoGame.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede ser nulo o vacío.");
        }
        if (videoGame.getPrice() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (videoGame.getStock() < 0) {
            throw new IllegalArgumentException("El stock debe ser mayor o igual a 0.");
        }

        boolean exists = repository.findAll().stream()
                .anyMatch(g -> g.getTitle().equalsIgnoreCase(videoGame.getTitle()));
        if (exists) {

            throw new IllegalStateException("El videojuego ya existe en el catálogo");
        }
        repository.save(videoGame);
    }

    @Override
    public List<VideoGame> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<VideoGame> findByTitle(String title) {
        return repository.findByTitle(title);
    }

    @Override
    public List<VideoGame> findByPlatform(String platform) {
        return repository.findByPlatform(platform);
    }

    @Override
    public void update(String title, VideoGame updated) {
        repository.update(title, updated);
    }

    @Override
    public boolean delete(String title) {
        return repository.delete(title);
    }

    @Override
    public Sale sellVideoGame(String title, int quantity) {
        Optional<VideoGame> opt = repository.findByTitle(title);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("El videojuego '" + title + "' no existe en el catálogo.");
        }
        VideoGame game = opt.get();
        if (game.getStock() < quantity) {
            throw new IllegalStateException("Stock insuficiente. Disponible: " + game.getStock());
        }

        game.setStock(game.getStock() - quantity);
        repository.update(title, game);

        double unitPrice = game.calculateFinalPrice();
        Sale sale = new Sale(UUID.randomUUID().toString(), game, quantity, unitPrice);
        JsonUtils.appendSale(sale);
        return sale;
    }

    @Override
    public List<Sale> findAllSales() {
        return JsonUtils.readSales();
    }
}
