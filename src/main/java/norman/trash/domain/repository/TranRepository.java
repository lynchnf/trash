package norman.trash.domain.repository;

import norman.trash.domain.Tran;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TranRepository extends PagingAndSortingRepository<Tran, Long> {
    Page<Tran> findByStmt_IdAndNameContainingIgnoreCase(Long stmtId, String name, Pageable pageable);

    Page<Tran> findByStmt_Id(Long stmtId, Pageable pageable);
}
