package in.dminc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.dminc.dto.MovieDto;
import in.dminc.dto.MoviePageResponse;
import in.dminc.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static in.dminc.utils.AppConstants.*;

@RestController
@RequestMapping("/api/v1/movie")
@Slf4j
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovie(@RequestPart MultipartFile file, @RequestPart String movieDtoObj) throws IOException {
        MovieDto movieDto = convertToMovieDto(movieDtoObj);
        MovieDto savedMovieDto = movieService.addMovie(movieDto, file);
        return new ResponseEntity<>(savedMovieDto, HttpStatus.CREATED);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovie(@PathVariable Integer movieId) {
        MovieDto movie = movieService.getMovie(movieId);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        List<MovieDto> movies = movieService.getAllMovies();
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable Integer movieId,
                                                @RequestPart MultipartFile file,
                                                @RequestPart String movieDtoObj) throws IOException {
        if (file.isEmpty()) file = null;
        MovieDto movieDto = convertToMovieDto(movieDtoObj);
        MovieDto updatedMovie = movieService.updateMovie(movieId, movieDto, file);
        return new ResponseEntity<>(updatedMovie, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovie(@PathVariable Integer movieId) throws IOException {
        return new ResponseEntity<>(movieService.deleteMovie(movieId), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<MoviePageResponse> getMoviesByPage(
            @RequestParam(defaultValue = PAGE_NUMBER) Integer pageNumber,
            @RequestParam(defaultValue = PAGE_SIZE) Integer pageSize
    ) {
        MoviePageResponse moviesByPage = movieService.getMoviesByPage(pageNumber, pageSize);
        return new ResponseEntity<>(moviesByPage, HttpStatus.OK);
    }

    @GetMapping("/page-sort")
    public ResponseEntity<MoviePageResponse> getMoviesByPageAndSort(
            @RequestParam(defaultValue = PAGE_NUMBER) Integer pageNumber,
            @RequestParam(defaultValue = PAGE_SIZE) Integer pageSize,
            @RequestParam(defaultValue = SORT_BY_FIELD) String sortByField,
            @RequestParam(defaultValue = SORT_ORDER) String sortOrder

    ) {
        MoviePageResponse moviesByPage = movieService.getMoviesByPageAndSorted(pageNumber, pageSize, sortByField, sortOrder);
        return new ResponseEntity<>(moviesByPage, HttpStatus.OK);
    }

    // convert string to Java class object
    private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj, MovieDto.class);
    }
}
