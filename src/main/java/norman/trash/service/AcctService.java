package norman.trash.service;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import norman.trash.domain.repository.AcctRepository;
import norman.trash.exception.NotFoundException;
import norman.trash.exception.OptimisticLockingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AcctService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcctService.class);
    @Autowired
    private AcctRepository repository;

    public Page<Acct> findByNameAndType(String name, AcctType type, Pageable pageable) {
        return repository.findByNameContainingIgnoreCaseAndType(name, type, pageable);
    }

    public Page<Acct> findByName(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<Acct> findByType(AcctType type, Pageable pageable) {
        return repository.findByType(type, pageable);
    }

    public Page<Acct> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Iterable<Acct> findAll() {
        return repository.findAll(Sort.by("name"));
    }

    public Acct findById(Long id) throws NotFoundException {
        Optional<Acct> optional = repository.findById(id);
        if (!optional.isPresent()) {
            throw new NotFoundException(LOGGER, "Account", id);
        }
        return optional.get();
    }

    public Acct save(Acct acct) throws OptimisticLockingException {
        try {
            return repository.save(acct);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockingException(LOGGER, "Account", acct.getId(), e);
        }
    }
}
