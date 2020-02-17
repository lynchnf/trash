package norman.trash.service;

import norman.trash.NotFoundException;
import norman.trash.OptimisticLockingException;
import norman.trash.domain.DataFile;
import norman.trash.domain.DataFileStatus;
import norman.trash.domain.repository.DataFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DataFileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataFileService.class);
    @Autowired
    private DataFileRepository repository;

    public Page<DataFile> findByOriginalFilenameAndStatus(String originalFilename, DataFileStatus status,
            Pageable pageable) {
        return repository.findByOriginalFilenameContainingIgnoreCaseAndStatus(originalFilename, status, pageable);
    }

    public Page<DataFile> findByOriginalFilename(String originalFilename, Pageable pageable) {
        return repository.findByOriginalFilenameContainingIgnoreCase(originalFilename, pageable);
    }

    public Page<DataFile> findByStatus(DataFileStatus status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }

    public Page<DataFile> findAll(PageRequest pageable) {
        return repository.findAll(pageable);
    }

    public DataFile findById(Long id) throws NotFoundException {
        Optional<DataFile> optional = repository.findById(id);
        if (!optional.isPresent()) {
            throw new NotFoundException(LOGGER, "Data File", id);
        }
        return optional.get();
    }

    public DataFile save(DataFile dataFile) throws OptimisticLockingException {
        try {
            return repository.save(dataFile);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockingException(LOGGER, "Data File", dataFile.getId(), e);
        }
    }
}
