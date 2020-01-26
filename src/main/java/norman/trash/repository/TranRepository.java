package norman.trash.repository;

import norman.trash.domain.Tran;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TranRepository extends PagingAndSortingRepository<Tran, Long> {
    Page<Tran> findByDebitStmt_IdOrCreditStmt_Id(Long debitId, Long creditId, Pageable pageable);
}
