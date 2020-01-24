package norman.trash.controller;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AcctForm {
    private Long id;
    private Integer version = 0;
    @NotBlank
    @Size(max = 50)
    private String name;
    @NotNull
    private AcctType type;

    public AcctForm() {
    }

    public AcctForm(Acct acct) {
        id = acct.getId();
        version = acct.getVersion();
        name = acct.getName();
        type = acct.getType();
    }

    public Acct toAcct() {
        Acct acct = new Acct();
        acct.setId(id);
        acct.setVersion(version);
        acct.setName(StringUtils.trimToNull(name));
        acct.setType(type);
        return acct;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AcctType getType() {
        return type;
    }

    public void setType(AcctType type) {
        this.type = type;
    }
}
