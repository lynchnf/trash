package norman.trash.domain.repository;

import norman.trash.domain.DataFile;
import norman.trash.domain.DataFileStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DataFileRepository extends PagingAndSortingRepository<DataFile, Long> {
    Page<DataFile> findByOriginalFilenameContainingIgnoreCaseAndStatus(String originalFilename, DataFileStatus status,
            Pageable pageable);

    Page<DataFile> findByOriginalFilenameContainingIgnoreCase(String originalFilename, Pageable pageable);

    Page<DataFile> findByStatus(DataFileStatus status, Pageable pageable);
}
