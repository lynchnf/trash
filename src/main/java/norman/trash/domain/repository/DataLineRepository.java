package norman.trash.domain.repository;

import norman.trash.domain.DataLine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DataLineRepository extends PagingAndSortingRepository<DataLine, Long> {
    Page<DataLine> findByDataFile_Id(Long dataFileId, Pageable pageable);
}
