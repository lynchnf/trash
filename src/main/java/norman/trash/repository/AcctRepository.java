package norman.trash.repository;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AcctRepository extends PagingAndSortingRepository<Acct, Long> {
    Page<Acct> findByNameContainingIgnoreCaseAndType(String name, AcctType type, Pageable pageable);

    Page<Acct> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Acct> findByType(AcctType type, Pageable pageable);
}
