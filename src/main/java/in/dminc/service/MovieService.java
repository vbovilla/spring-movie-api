package in.dminc.service;

import in.dminc.dto.MovieDto;
import in.dminc.dto.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {
    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    MovieDto getMovie(Integer movieId);

    List<MovieDto> getAllMovies();

    MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException;

    String deleteMovie(Integer movieId) throws IOException;

    MoviePageResponse getMoviesByPage(Integer pageNumber, Integer pageSize);

    MoviePageResponse getMoviesByPageAndSorted(Integer pageNumber, Integer pageSize, String sortByField, String sortDirection);
}
