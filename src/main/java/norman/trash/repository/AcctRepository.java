package norman.trash.repository;

import norman.trash.domain.Acct;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AcctRepository extends PagingAndSortingRepository<Acct, Long> {
}
