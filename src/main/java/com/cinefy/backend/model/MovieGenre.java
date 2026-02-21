package com.cinefy.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movie_genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieGenre {

    @EmbeddedId
    private MovieGenreId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("genreId")
    @JoinColumn(name = "genre_id")
    private Genre genre;

    public MovieGenre(Movie movie, Genre genre) {
        this.id = new MovieGenreId(movie.getId(), genre.getId());
        this.movie = movie;
        this.genre = genre;
    }
}
