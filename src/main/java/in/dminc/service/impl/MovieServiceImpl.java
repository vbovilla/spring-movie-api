package in.dminc.service.impl;

import in.dminc.dto.MovieDto;
import in.dminc.dto.MoviePageResponse;
import in.dminc.entities.Movie;
import in.dminc.exceptions.MovieNotFoundException;
import in.dminc.repositories.MovieRepository;
import in.dminc.service.FileService;
import in.dminc.service.MovieService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        // check if file exists already
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileAlreadyExistsException("File already exists! Please upload a new file.!");
        }

        String posterFileName;
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            // upload file
            posterFileName = fileService.uploadFile(path, file);
        } else {
            posterFileName = file.getOriginalFilename();
        }


        // set value of field poster in movieDto
        movieDto.setPoster(posterFileName);

        // map movieDto to movie object.
        Movie movie = new Movie(
                null,   //JPA generates movieId
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
        String posterUrl = baseUrl + "/file/" + posterFileName;

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
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("No movie found with movieId: " + movieId));

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

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        // check if movie exists, with given movieId
        Movie existingMovie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("No movie found with movieId: " + movieId));

        // if file is null, do nothing.
        // if file is not null, then delete existing file associated with the movie and upload new file
        String posterName = existingMovie.getPoster();
        if (null != file) {
            Files.deleteIfExists(Paths.get(path + File.separator + posterName));
            posterName = fileService.uploadFile(path, file);
        }

        // update movieDTO poster value
        movieDto.setPoster(posterName);

        // map it to movie entity object
        Movie movie = new Movie(
                movieDto.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        // save the movie object --> return saved movie object
        Movie savedMovie = movieRepository.save(movie);

        // map it to saved movieDTO
        return new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                baseUrl + "/file/" + savedMovie.getPoster());
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        // check if movie exists, with given movieId
        Movie existingMovie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("No movie found with movieId: " + movieId));

        // delete poster file associated with the movie
        String poster = existingMovie.getPoster();
        Files.deleteIfExists(Paths.get(path + File.separator + poster));

        // delete movie record
        movieRepository.delete(existingMovie);

        return "Movie deleted with Id: " + movieId;
    }

    @Override
    public MoviePageResponse getMoviesByPage(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Movie> moviesPage = movieRepository.findAll(pageable);

        List<MovieDto> movies = moviesPage.getContent().stream().map(movie ->
                new MovieDto(
                        movie.getMovieId(),
                        movie.getTitle(),
                        movie.getDirector(),
                        movie.getStudio(),
                        movie.getMovieCast(),
                        movie.getReleaseYear(),
                        movie.getPoster(),
                        baseUrl + "/file/" + movie.getPoster())).collect(Collectors.toList());

        return new MoviePageResponse(movies,
                pageNumber,
                pageSize,
                moviesPage.getTotalElements(),
                moviesPage.getTotalPages(),
                moviesPage.isLast());
    }

    @Override
    public MoviePageResponse getMoviesByPageAndSorted(Integer pageNumber, Integer pageSize, String sortByField, String sortDirection) {

//        Sort sortBy = sortDirection.equalsIgnoreCase("asc") ?
//                Sort.by(Sort.Direction.ASC, sortByField) :
//                Sort.by(Sort.Direction.DESC, sortByField);

        Sort sortBy = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by(sortByField).ascending() : Sort.by(sortByField).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortBy);
        Page<Movie> moviesPage = movieRepository.findAll(pageable);

        List<MovieDto> movies = moviesPage.getContent().stream().map(movie ->
                new MovieDto(
                        movie.getMovieId(),
                        movie.getTitle(),
                        movie.getDirector(),
                        movie.getStudio(),
                        movie.getMovieCast(),
                        movie.getReleaseYear(),
                        movie.getPoster(),
                        baseUrl + "/file/" + movie.getPoster())).collect(Collectors.toList());

        return new MoviePageResponse(movies,
                pageNumber,
                pageSize,
                moviesPage.getTotalElements(),
                moviesPage.getTotalPages(),
                moviesPage.isLast());
    }
}
