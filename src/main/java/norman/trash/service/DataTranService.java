package norman.trash.service;

import norman.trash.domain.DataTran;
import norman.trash.domain.repository.DataTranRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DataTranService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataTranService.class);
    @Autowired
    private DataTranRepository repository;

    public Page<DataTran> findByDataFileId(Long dataFileId, Pageable pageable) {
        return repository.findByDataFile_Id(dataFileId, pageable);
    }
}
