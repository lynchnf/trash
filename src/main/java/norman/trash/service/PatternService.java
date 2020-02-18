package norman.trash.service;

import norman.trash.domain.Pattern;
import norman.trash.domain.repository.PatternRepository;
import norman.trash.exception.MultipleOptimisticLockingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return repository.findAll(Sort.by("seq"));
    }

    public void saveAll(Iterable<Pattern> patterns, List<Long> idList) throws MultipleOptimisticLockingException {
        try {
            for (Pattern pattern : patterns) {
                idList.remove(pattern.getId());
            }
            repository.saveAll(patterns);
            Iterable<Pattern> pattensToDelete = repository.findAllById(idList);
            repository.deleteAll(pattensToDelete);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new MultipleOptimisticLockingException(LOGGER, "Patterns", e);
        }
    }
}
