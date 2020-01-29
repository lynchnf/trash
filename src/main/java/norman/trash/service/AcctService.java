package norman.trash.service;

import norman.trash.NotFoundException;
import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import norman.trash.domain.repository.AcctRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AcctService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcctService.class);
    @Autowired
    private AcctRepository repository;

    public Acct save(Acct acct) {
        return repository.save(acct);
    }

    public Acct findById(Long id) throws NotFoundException {
        Optional<Acct> optional = repository.findById(id);
        if (!optional.isPresent()) {
            throw new NotFoundException(LOGGER, "Account", id);
        }
        return optional.get();
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public Page<Acct> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Acct> findByNameContainingAndType(String name, AcctType type, Pageable pageable) {
        return repository.findByNameContainingIgnoreCaseAndType(name, type, pageable);
    }

    public Page<Acct> findByNameContaining(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<Acct> findByType(AcctType type, Pageable pageable) {
        return repository.findByType(type, pageable);
    }
}
