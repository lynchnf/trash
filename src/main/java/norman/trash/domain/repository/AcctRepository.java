package norman.trash.domain.repository;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AcctRepository extends PagingAndSortingRepository<Acct, Long> {
    Page<Acct> findByNameContainingAndType(String name, AcctType type, Pageable pageable);

    Page<Acct> findByNameContaining(String name, Pageable pageable);

    Page<Acct> findByType(AcctType type, Pageable pageable);
}
