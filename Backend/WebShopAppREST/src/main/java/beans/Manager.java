package beans;

import java.util.Date;
import java.util.List;

import enumerations.Gender;
import enumerations.Role;

public class Manager extends User {
    private String chocolateFactory;

    public Manager() {
        super();
    }

   /* public Manager(String username, String password, String firstName, String lastName, Gender gender, Date dateOfBirth,
                   Role role, String chocolateFactory) {
        super(username, password, firstName, lastName, gender, dateOfBirth, role);
        this.chocolateFactory = chocolateFactory;
    }*/

    public String getChocolateFactory() {
        return chocolateFactory;
    }

    public void setChocolateFactory(String chocolateFactory) {
        this.chocolateFactory = chocolateFactory;
    }
}