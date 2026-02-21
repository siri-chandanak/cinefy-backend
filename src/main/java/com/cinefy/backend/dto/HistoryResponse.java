package com.cinefy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;
import java.time.Instant;

@Data
@AllArgsConstructor
public class HistoryResponse {
    private UUID movieId;
    private String title;
    private String posterUrl;
}
