package com.travel.wanderlog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.travel.wanderlog.dto.tag.TagCreateDto;
import com.travel.wanderlog.dto.tag.TagDto;
import com.travel.wanderlog.service.TagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;

    @GetMapping
    public List<TagDto> list() {
        return tagService.listAll();
    }

    @PostMapping
    public ResponseEntity<TagDto> create(@RequestBody TagCreateDto dto) {
        var created = tagService.create(dto);
        return ResponseEntity.ok(created);
    }

    // assegnazioni su activity
    // Sostituisce TUTTI i tag dell'activity con quelli passati
    // Esempio: PUT /api/tags/activities/17 body: [1,3,5]
    @PutMapping("/activities/{activityId}")
    public List<TagDto> replaceActivityTags(@PathVariable Long activityId, @RequestBody List<Long> tagIds) {
        return tagService.replaceActivityTags(activityId, tagIds);
    }

    // Aggiunge tag all'activity (accumula, non rimuove i già presenti)
    // Esempio: POST /api/tags/activities/17 body: [4,6]
    @PostMapping("/activities/{activityId}")
    public List<TagDto> add(@PathVariable Long activityId, @RequestBody List<Long> tagIds) {
        return tagService.addTags(activityId, tagIds);
    }

    // Rimuove un singolo tag da un'activity
    // Esempio: DELETE /api/tags/activities/17/5
    @DeleteMapping("/activities/{activityId}/{tagId}")
    public ResponseEntity<Void> remove(@PathVariable Long activityId, @PathVariable Long tagId) {
        tagService.removeTag(activityId, tagId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/activities/{tagId}")
    public ResponseEntity<Void> delete(@PathVariable Long tagId) {
        tagService.delete(tagId);

        return ResponseEntity.noContent().build();
    }
}
