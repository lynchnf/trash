package norman.trash.controller.view;

import norman.trash.domain.Acct;

import java.util.ArrayList;
import java.util.List;

public class DataAcctMatchView {
    private List<AcctView> sameFidAccts = new ArrayList<>();
    private List<AcctView> noFidAccts = new ArrayList<>();

    public DataAcctMatchView(List<Acct> sameFidAccts, List<Acct> noFidAccts) {
        for (Acct acct : sameFidAccts) {
            this.sameFidAccts.add(new AcctView(acct));
        }
        for (Acct acct : noFidAccts) {
            this.noFidAccts.add(new AcctView(acct));
        }
    }

    public List<AcctView> getSameFidAccts() {
        return sameFidAccts;
    }

    public List<AcctView> getNoFidAccts() {
        return noFidAccts;
    }
}
