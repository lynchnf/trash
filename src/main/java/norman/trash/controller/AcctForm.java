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
    @Size(max = 50)
    private String addressName;
    @Size(max = 50)
    private String address1;
    @Size(max = 50)
    private String address2;
    @Size(max = 50)
    private String city;
    @Size(max = 2)
    private String state;
    @Size(max = 10)
    private String zipCode;
    @Size(max = 20)
    private String phoneNumber;

    public AcctForm() {
    }

    public AcctForm(Acct acct) {
        id = acct.getId();
        version = acct.getVersion();
        name = acct.getName();
        type = acct.getType();
        addressName = acct.getAddressName();
        address1 = acct.getAddress1();
        address2 = acct.getAddress2();
        city = acct.getCity();
        state = acct.getState();
        zipCode = acct.getZipCode();
        phoneNumber = acct.getPhoneNumber();
    }

    public Acct toAcct() {
        Acct acct = new Acct();
        acct.setId(id);
        acct.setVersion(version);
        acct.setName(StringUtils.trimToNull(name));
        acct.setType(type);
        acct.setAddressName(StringUtils.trimToNull(addressName));
        acct.setAddress1(StringUtils.trimToNull(address1));
        acct.setAddress2(StringUtils.trimToNull(address2));
        acct.setCity(StringUtils.trimToNull(city));
        acct.setState(StringUtils.trimToNull(state));
        acct.setZipCode(StringUtils.trimToNull(zipCode));
        acct.setPhoneNumber(StringUtils.trimToNull(phoneNumber));
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

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
