package org.itmo.isLab1.artists.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArtistProfileResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final String surname;
    private final String biography;
    private final String location;
}
