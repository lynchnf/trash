package norman.trash.service;

import norman.trash.NotFoundException;
import norman.trash.OptimisticLockingException;
import norman.trash.domain.Cat;
import norman.trash.domain.repository.CatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatService.class);
    @Autowired
    private CatRepository repository;

    public Page<Cat> findByName(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<Cat> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Cat findById(Long id) throws NotFoundException {
        Optional<Cat> optional = repository.findById(id);
        if (!optional.isPresent()) {
            throw new NotFoundException(LOGGER, "Category", id);
        }
        return optional.get();
    }

    public Cat save(Cat cat) throws OptimisticLockingException {
        try {
            return repository.save(cat);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockingException(LOGGER, "Category", cat.getId(), e);
        }
    }
}
