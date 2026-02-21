package com.cinefy.backend.model;

import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMovieRatingId implements Serializable {
    private UUID userId;
    private UUID movieId;
}
