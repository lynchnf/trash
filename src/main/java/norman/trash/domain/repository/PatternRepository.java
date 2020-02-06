package norman.trash.domain.repository;

import norman.trash.domain.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PatternRepository extends PagingAndSortingRepository<Pattern, Long> {
    Page<Pattern> findByRegexContainingIgnoreCase(String regex, Pageable pageable);
}
