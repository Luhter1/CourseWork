package org.itmo.isLab1.artists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Value;

@Value
public class MediaDto {
    @JsonProperty("id")
    Long id;

    @JsonProperty("uri")
    String uri;

    @JsonProperty("mediaType")
    MediaTypeEnum mediaType;

    @JsonProperty("fileSize")
    Long fileSize;

    @JsonProperty("metadata")
    JsonNode metadata;

    @JsonProperty("isPrimary")
    Boolean isPrimary;
}
