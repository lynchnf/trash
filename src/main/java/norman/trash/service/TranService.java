package norman.trash.service;

import norman.trash.domain.Tran;
import norman.trash.domain.repository.TranRepository;
import norman.trash.exception.MultipleOptimisticLockingException;
import norman.trash.exception.NotFoundException;
import norman.trash.exception.OptimisticLockingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TranService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TranService.class);
    @Autowired
    private TranRepository repository;

    public Page<Tran> findByStmtIdAndName(Long stmtId, String name, Pageable pageable) {
        return repository.findByStmt_IdAndNameContainingIgnoreCase(stmtId, name, pageable);
    }

    public Page<Tran> findByStmtId(Long stmtId, Pageable pageable) {
        return repository.findByStmt_Id(stmtId, pageable);
    }

    public Tran findById(Long id) throws NotFoundException {
        Optional<Tran> optional = repository.findById(id);
        if (!optional.isPresent()) {
            throw new NotFoundException(LOGGER, "Transaction", id);
        }
        return optional.get();
    }

    public Tran save(Tran tran) throws OptimisticLockingException {
        try {
            return repository.save(tran);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockingException(LOGGER, "Transaction", tran.getId(), e);
        }
    }

    @Transactional
    public void saveAll(List<Tran> trans) throws MultipleOptimisticLockingException {
        try {
            repository.saveAll(trans);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new MultipleOptimisticLockingException(LOGGER, "Transactions", e);
        }
    }
}
