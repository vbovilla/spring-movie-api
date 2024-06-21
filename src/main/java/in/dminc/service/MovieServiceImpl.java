package in.dminc.service;

import in.dminc.dto.MovieDto;
import in.dminc.entities.Movie;
import in.dminc.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    private final MovieRepository movieRepository;
    private final FileService fileService;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        // upload file
        String uploadedFileName = fileService.uploadFile(path, file);

        // set value of field poster in movieDto
        movieDto.setPoster(uploadedFileName);

        // map movieDto to movie object.
        Movie movie = new Movie(
                movieDto.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        // save movie object
        Movie savedMovie = movieRepository.save(movie);

        // generate posterUrl
        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        // map movie object to saved movieDto object
        return new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        // check the data in DB, and if exists fetch the data of given item
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("No movie found with movieId: " + movieId));

        // generate posterUrl
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        // map to movieDto object and return it
        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
    }

    @Override
    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(movie -> new MovieDto(
                        movie.getMovieId(),
                        movie.getTitle(),
                        movie.getDirector(),
                        movie.getStudio(),
                        movie.getMovieCast(),
                        movie.getReleaseYear(),
                        movie.getPoster(),
                        baseUrl + "/file/" + movie.getPoster()))
                .collect(Collectors.toList());
    }
}
