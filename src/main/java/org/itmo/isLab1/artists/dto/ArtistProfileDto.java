package org.itmo.isLab1.artists.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArtistProfileDto {
    private final Integer id;
    private final String email;
    private final String name;
    private final String surname;
    private final String biography;
    private final String location;
}
