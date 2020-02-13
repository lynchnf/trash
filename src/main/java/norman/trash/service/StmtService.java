package norman.trash.service;

import norman.trash.MultipleOptimisticLockingException;
import norman.trash.NotFoundException;
import norman.trash.domain.Stmt;
import norman.trash.domain.repository.StmtRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StmtService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StmtService.class);
    @Autowired
    private StmtRepository repository;

    public Page<Stmt> findByAcctId(Long acctId, Pageable pageable) {
        return repository.findByAcct_Id(acctId, pageable);
    }

    public Stmt findById(Long id) throws NotFoundException {
        Optional<Stmt> optional = repository.findById(id);
        if (!optional.isPresent()) {
            throw new NotFoundException(LOGGER, "Statement", id);
        }
        return optional.get();
    }

    public Stmt findByAcctIdAndCloseDate(Long acctId, Date closeDate) {
        Iterable<Stmt> stmts = repository.findByAcct_IdAndCloseDate(acctId, closeDate);
        // There should always be exactly one.
        return stmts.iterator().next();
    }

    public void saveAll(List<Stmt> stmts) throws MultipleOptimisticLockingException {
        try {
            repository.saveAll(stmts);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new MultipleOptimisticLockingException(LOGGER, "Statements", e);
        }
    }
}
