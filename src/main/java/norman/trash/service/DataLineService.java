package norman.trash.service;

import norman.trash.domain.DataLine;
import norman.trash.domain.repository.DataLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DataLineService {
    @Autowired
    private DataLineRepository repository;

    public Page<DataLine> findByDataFileId(Long dataFileId, Pageable pageable) {
        return repository.findByDataFile_Id(dataFileId, pageable);
    }
}
