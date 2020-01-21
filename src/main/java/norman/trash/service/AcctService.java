package norman.trash.service;

import norman.trash.LoggingException;
import norman.trash.domain.Acct;
import norman.trash.repository.AcctRepository;
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

    public Acct findById(Long id) throws LoggingException {
        Optional<Acct> optional = repository.findById(id);
        if (!optional.isPresent()) {
            throw new LoggingException(LOGGER, "Acct not found, id=\"" + id + "\"");
        }
        return optional.get();
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public Page<Acct> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
