package in.dminc.dto;

import java.util.List;

public record MoviePageResponse(List<MovieDto> movies,
                                Integer pageNumber,
                                Integer pageSize,
                                Long totalRecords,
                                Integer totalPages,
                                Boolean isLastPage) {
}
