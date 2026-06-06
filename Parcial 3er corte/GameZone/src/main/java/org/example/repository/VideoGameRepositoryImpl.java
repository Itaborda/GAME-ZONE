package org.example.repository;

import org.example.entities.VideoGame;
import org.example.utils.JsonUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VideoGameRepositoryImpl implements VideoGameRepository {

    @Override
    public void save(VideoGame videoGame) {
        List<VideoGame> list = JsonUtils.readGames();
        list.add(videoGame);
        JsonUtils.saveGames(list);
    }

    @Override
    public List<VideoGame> findAll() {
        return JsonUtils.readGames();
    }

    @Override
    public Optional<VideoGame> findByTitle(String title) {
        return JsonUtils.readGames().stream()
                .filter(g -> g.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    @Override
    public List<VideoGame> findByPlatform(String platform) {
        return JsonUtils.readGames().stream()
                .filter(g -> g.getPlatform().equalsIgnoreCase(platform))
                .collect(Collectors.toList());
    }

    @Override
    public void update(String title, VideoGame updated) {
        List<VideoGame> list = JsonUtils.readGames();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTitle().equalsIgnoreCase(title)) {
                list.set(i, updated);
                break;
            }
        }
        JsonUtils.saveGames(list);
    }

    @Override
    public boolean delete(String title) {
        List<VideoGame> list = JsonUtils.readGames();
        boolean removed = list.removeIf(g -> g.getTitle().equalsIgnoreCase(title));
        if (removed) JsonUtils.saveGames(list);
        return removed;
    }
}
