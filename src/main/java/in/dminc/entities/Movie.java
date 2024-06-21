package in.dminc.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please provide movie's title")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's director name")
    private String director;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's studio name")
    private String studio;

    @ElementCollection
    @CollectionTable(name = "movie_cast",
            joinColumns = @JoinColumn(name = "movie_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"movie_id", "cast_name"}, name = "movie_cast_unique_constraint")}
    )
    @Column(name = "cast_name")
    private List<String> movieCast = new ArrayList<>();

    @Column(nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's poster")
    private String poster;
}
