package org.example.repository;

import org.example.entities.VideoGame;

import java.util.List;
import java.util.Optional;

public interface VideoGameRepository {

    void save(VideoGame videoGame);

    List<VideoGame> findAll();

    Optional<VideoGame> findByTitle(String title);

    List<VideoGame> findByPlatform(String platform);

    void update(String title, VideoGame updated);

    boolean delete(String title);
}
