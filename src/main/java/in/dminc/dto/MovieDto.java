package in.dminc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Integer movieId;

    @NotBlank(message = "Please provide movie's title")
    private String title;

    @NotBlank(message = "Please provide movie's director name")
    private String director;

    @NotBlank(message = "Please provide movie's studio name")
    private String studio;

    private List<String> movieCast;

    private Integer releaseYear;

    @NotBlank(message = "Please provide movie's poster")
    private String poster;

    @NotBlank(message = "Please provide movie's poster url")
    private String posterUrl;
}
