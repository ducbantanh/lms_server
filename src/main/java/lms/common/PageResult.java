package lms.common;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

public record PageResult<T>(List<T> items, int page, int size, long total, int totalPages) {

    public static <E, T> PageResult<T> of(Page<E> page, Function<E, T> mapper) {
        return new PageResult<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
