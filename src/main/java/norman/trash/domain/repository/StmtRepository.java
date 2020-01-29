package norman.trash.domain.repository;

import norman.trash.domain.Stmt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StmtRepository extends PagingAndSortingRepository<Stmt, Long> {
    Page<Stmt> findByAcct_Id(Long acctId, Pageable pageable);
}
