package com.cinefy.backend.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MovieGenreId implements Serializable {

    private UUID movieId;

    private Short genreId;   // SMALLINT in DB → Long or Integer both ok
}
