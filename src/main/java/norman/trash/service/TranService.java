package norman.trash.service;

import norman.trash.NotFoundException;
import norman.trash.domain.Tran;
import norman.trash.repository.TranRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TranService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TranService.class);
    @Autowired
    private TranRepository repository;

    public Tran save(Tran tran) {
        return repository.save(tran);
    }

    public Tran findById(Long id) throws NotFoundException {
        Optional<Tran> optional = repository.findById(id);
        if (!optional.isPresent()) {
            throw new NotFoundException(LOGGER, "Transaction", id);
        }
        return optional.get();
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public Page<Tran> findByDebitStmt_IdOrCreditStmt_Id(Long debitId, Long creditId, Pageable pageable) {
        return repository.findByDebitStmt_IdOrCreditStmt_Id(debitId, creditId, pageable);
    }
}
