package com.travel.wanderlog.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.travel.wanderlog.dto.tag.TagDto;
import com.travel.wanderlog.dto.tag.TagCreateDto;
import com.travel.wanderlog.model.Tag;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagDto toDto(Tag t);

    @Mapping(target = "id", ignore = true)
    Tag toEntity(TagCreateDto dto);
}
