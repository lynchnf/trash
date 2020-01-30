package norman.trash.service;

import norman.trash.NotFoundException;
import norman.trash.domain.Stmt;
import norman.trash.domain.repository.StmtRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StmtService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StmtService.class);
    @Autowired
    private StmtRepository repository;

    public Page<Stmt> findByAcct_Id(Long acctId, Pageable pageable) {
        return repository.findByAcct_Id(acctId, pageable);
    }

    public Stmt findById(Long id) throws NotFoundException {
        Optional<Stmt> optional = repository.findById(id);
        if (!optional.isPresent()) {
            throw new NotFoundException(LOGGER, "Statement", id);
        }
        return optional.get();
    }
}
