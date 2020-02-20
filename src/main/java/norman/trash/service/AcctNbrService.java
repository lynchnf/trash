package norman.trash.service;

import norman.trash.domain.AcctNbr;
import norman.trash.domain.repository.AcctNbrRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcctNbrService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcctNbrService.class);
    @Autowired
    private AcctNbrRepository repository;

    public List<AcctNbr> findByAcctOfxFidAndNumber(String ofxFid, String number) {
        return repository.findByAcct_OfxFidAndNumber(ofxFid, number);
    }
}
