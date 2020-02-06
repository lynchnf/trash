package norman.trash.service;

import norman.trash.MultipleOptimisticLockingException;
import norman.trash.domain.Pattern;
import norman.trash.domain.repository.PatternRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class PatternService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatternService.class);
    @Autowired
    private PatternRepository repository;

    public Page<Pattern> findByRegex(String regex, Pageable pageable) {
        return repository.findByRegexContainingIgnoreCase(regex, pageable);
    }

    public Page<Pattern> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Iterable<Pattern> findAll() {
        // FIXME Sort by seq.
        return repository.findAll();
    }

    public Iterable<Pattern> saveAll(Iterable<Pattern> patterns) throws MultipleOptimisticLockingException {
        try {
            return repository.saveAll(patterns);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new MultipleOptimisticLockingException(LOGGER, "Patterns", e);
        }
    }
}
