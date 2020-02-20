package norman.trash.domain.repository;

import norman.trash.domain.AcctNbr;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AcctNbrRepository extends PagingAndSortingRepository<AcctNbr, Long> {
    List<AcctNbr> findByAcct_OfxFidAndNumber(String ofxFid, String number);
}
