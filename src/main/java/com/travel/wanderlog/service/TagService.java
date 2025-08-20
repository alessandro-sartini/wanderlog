package com.travel.wanderlog.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.travel.wanderlog.dto.tag.TagCreateDto;
import com.travel.wanderlog.dto.tag.TagDto;
import com.travel.wanderlog.mapper.TagMapper;
import com.travel.wanderlog.model.Activity;
import com.travel.wanderlog.model.Tag;
import com.travel.wanderlog.repository.ActivityRepository;
import com.travel.wanderlog.repository.TagRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepo;
    private final ActivityRepository activityRepo;
    private final TagMapper mapper;

    // visualizza
    public List<TagDto> listAll() {
        return tagRepo.findAll().stream().map(mapper::toDto).toList();
    }

    // crea da Dto
    public TagDto create(TagCreateDto dto) {
        Tag t = mapper.toEntity(dto);
        return mapper.toDto(tagRepo.save(t));
    }
    

    @Transactional
    public List<TagDto> replaceActivityTags(Long activityId, List<Long> tagIds) {
        Activity a = activityRepo.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity non trovata: " + activityId));

        Set<Tag> newTags = tagIds.stream()
                .map(id -> tagRepo.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Tag non trovato: " + id)))
                .collect(Collectors.toSet());

        a.getTags().clear();
        a.getTags().addAll(newTags);
        return a.getTags().stream().map(mapper::toDto).toList();
    }

    @Transactional
    public List<TagDto> addTags(Long activityId, List<Long> tagIds) {
        Activity a = activityRepo.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity non trovata: " + activityId));
        for (Long id : tagIds) {
            Tag t = tagRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tag non trovato: " + id));
            a.getTags().add(t);
        }
        return a.getTags().stream().map(mapper::toDto).toList();
    }

    @Transactional
    public void removeTag(Long activityId, Long tagId) {
        Activity a = activityRepo.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity non trovata: " + activityId));
        a.getTags().removeIf(t -> t.getId().equals(tagId));
    }

    @Transactional
    public void delete(Long tagId) {
        if (!tagRepo.existsById(tagId)) {
            throw new IllegalArgumentException("Tag non trovato: " + tagId); 
        }
        tagRepo.deleteById(tagId);
    }

}
