package myApp.repository;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: eluibon
 * Date: 13/02/13
 * Time: 9.48
 */
public class UserConstraintException extends Exception {

    public static enum Violations {USERNAME_NOT_UNIQUE,EMAIL_NOT_UNIQUE,UNKNOWN_ERROR}

    private Set<Violations> constraintsViolations ;

    public UserConstraintException(Set<Violations> constraintsViolations) {
        this.constraintsViolations = constraintsViolations;
    }

    public Set<Violations> getConstraintsViolations() {
        return constraintsViolations;
    }

    public void setConstraintsViolations(Set<Violations> constraintsViolations) {
        this.constraintsViolations = constraintsViolations;
    }
}
