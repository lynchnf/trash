package norman.trash.domain.repository;

import norman.trash.domain.Cat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CatRepository extends PagingAndSortingRepository<Cat, Long> {
    Page<Cat> findByNameContaining(String name, Pageable pageable);
}
