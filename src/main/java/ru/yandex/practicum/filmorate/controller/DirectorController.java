package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.film.DirectorService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/directors")
public class DirectorController {

    private DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<Director> findAll() {
        log.debug("Поиск всех режиссеров");
        return directorService.findAllDirectors();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable("id") Integer id) {
        log.debug("Поиск режиссера с id = {}", id);
        Director director = directorService.findDirectorById(id);
        log.debug("Найден режиссер {}.", director);
        return director;
    }

    @PostMapping
    public Director create(@RequestBody Director director) {
        log.debug("Пришел запрос на создание режиссера.");
        Director createdDirector = directorService.createDirector(director);
        log.debug("Добавлен режиссер c id = {}", createdDirector.getId());
        return createdDirector;
    }

    @PutMapping
    public Director update(@RequestBody Director director) {
        log.debug("Пришел запрос на обновление данных о режиссере.");
        Director updatedDirector = directorService.updateDirector(director);
        log.debug("Обновлены данные о режиссере с id = {}", director.getId());
        return updatedDirector;
    }

    @DeleteMapping("/{id}")
    public void removeById(@PathVariable("id") Integer id) {
        log.debug("Удаление данных о режиссере с id = {}", id);
        directorService.removeDirectorById(id);
        log.debug("Данные удалены.");
    }
}
