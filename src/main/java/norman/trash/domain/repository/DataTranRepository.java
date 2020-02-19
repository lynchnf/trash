package norman.trash.domain.repository;

import norman.trash.domain.DataTran;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DataTranRepository extends PagingAndSortingRepository<DataTran, Long> {
    Page<DataTran> findByDataFile_Id(Long dataFileId, Pageable pageable);
}
