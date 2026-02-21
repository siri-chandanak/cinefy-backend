package com.cinefy.backend.model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDailyStatsId implements Serializable {
    private UUID movieId;
    private LocalDate day;
}