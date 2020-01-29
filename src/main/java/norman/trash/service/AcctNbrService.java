package norman.trash.service;

import norman.trash.NotFoundException;
import norman.trash.domain.AcctNbr;
import norman.trash.domain.repository.AcctNbrRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AcctNbrService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcctNbrService.class);
    @Autowired
    private AcctNbrRepository repository;

    public AcctNbr save(AcctNbr acctNbr) {
        return repository.save(acctNbr);
    }

    public AcctNbr findById(Long id) throws NotFoundException {
        Optional<AcctNbr> optional = repository.findById(id);
        if (!optional.isPresent()) {
            throw new NotFoundException(LOGGER, "Account", id);
        }
        return optional.get();
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public Page<AcctNbr> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
