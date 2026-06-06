package org.example.services.interfaces;

import org.example.entities.Sale;
import org.example.entities.VideoGame;

import java.util.List;
import java.util.Optional;

public interface VideoGameService {

    void addVideoGame(VideoGame videoGame);

    List<VideoGame> findAll();

    Optional<VideoGame> findByTitle(String title);

    List<VideoGame> findByPlatform(String platform);

    void update(String title, VideoGame updated);

    boolean delete(String title);

    Sale sellVideoGame(String title, int quantity);

    List<Sale> findAllSales();
}
