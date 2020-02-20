package norman.trash.domain.repository;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AcctRepository extends PagingAndSortingRepository<Acct, Long> {
    Page<Acct> findByNameContainingIgnoreCaseAndType(String name, AcctType type, Pageable pageable);

    Page<Acct> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Acct> findByType(AcctType type, Pageable pageable);

    List<Acct> findByOfxFidOrderByName(String ofxFid);

    List<Acct> findByOfxFidNullOrderByName();
}
