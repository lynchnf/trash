package norman.trash.service;

import norman.trash.NotFoundException;
import norman.trash.domain.Cat;
import norman.trash.repository.CatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatService.class);
    @Autowired
    private CatRepository repository;

    public Cat save(Cat cat) {
        return repository.save(cat);
    }

    public Cat findById(Long id) throws NotFoundException {
        Optional<Cat> optional = repository.findById(id);
        if (!optional.isPresent()) {
            throw new NotFoundException(LOGGER, "Account", id);
        }
        return optional.get();
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public Page<Cat> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Cat> findByNameContaining(String name, Pageable pageable) {
        return repository.findByNameContaining(name, pageable);
    }
}
