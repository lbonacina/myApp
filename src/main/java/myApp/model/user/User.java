package myApp.model.user;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/* TODO : separate user data from authentication information ? */
@Entity
@Table(name = "USER", uniqueConstraints = {@UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = "username")})
public class User implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = -6004763139641579839L;

    private final static int NUMBER_OF_ATTEMPTS_BEFORE_LOCKED_ACCOUNT = 3 ;
    private static final PasswordService passwordService = new DefaultPasswordService();
    private final static String DEFAULT_PASSWORD = "1234Abcd" ;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z ]*", message = "must contain only letters and spaces")
    private String firstName;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z ]*", message = "must contain only letters and spaces")
    private String lastName;

    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotNull
    @Size(min = 10, max = 25)
    @Column(name = "phone")
    private String phone;

    @NotNull
    @Size(min = 3, max = 25)
    @Pattern(regexp = "[A-Za-z0-9]*", message = "must contain only letters and numbers")
    private String username;

    @NotNull
    @Column(length = 200) // no check here, since password is crypted at this point, check is on AccountController
    private String password;

    // on new and reset password 'events' we need the decrypted password to be sent to the user, so we store it here
    // otherwise is null
    // the field is not persisted
    // also probably not the best solution...
    private String decryptedPassword;

    @Column(nullable = false)
    private boolean enabled ;

    @Column(nullable = false)
    private boolean superadmin ;

    @Column(nullable = false, name = "pwd_change_on_next_login")
    private boolean forcePasswordChangeOnNextLogin ;

    @NotNull
    @Column(name = "failed_login_attempt_count", nullable = false)
    private int failedLoginAttemptCount ;

    // AFAIK there is no wa in JPA2 to control fetch stategies apart from EAGER/LAZY
    // we need to use the Hibernate annotation directly
    // also it seems like FetchMode.JOIN does not work properly (need to test)
    @ManyToMany(fetch = FetchType.EAGER)
    //@Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 50)
    //@NotEmpty
    @JoinTable(name="USER_ROLE",
               joinColumns=@JoinColumn(name="USER_ID", referencedColumnName="ID"),
               inverseJoinColumns=@JoinColumn(name="ROLE_ID", referencedColumnName="ID"))
    protected Set<Role> roles ;

// --------------------------- CONSTRUCTORS ---------------------------

    public User() {

        id = null ;
        roles = new HashSet<Role>() ;
        enabled = true ;
        superadmin = false ;
        failedLoginAttemptCount = 0 ;
        resetPassword();
    }

// -------------------------- OTHER METHODS --------------------------

    public String getFullName() {

        return firstName + " " + lastName ;
    }

    public void disable() {
        failedLoginAttemptCount = 0 ;
        enabled = false ;
    }

    public void enable() {
        failedLoginAttemptCount = 0 ;
        enabled = true ;
    }

    public Set<String> getPermissionNames() {
        Set<String> set = new HashSet<String>();
        for (Role r : roles) {
            for (Permission p : r.getPermissions()) {
                set.add(p.getPermission()) ;
            }
        }
        return set;
    }

    public Set<String> getRoleNames() {
        Set<String> set = new HashSet<String>();
        for (Role r : roles) {
            set.add(r.getRole()) ;
        }
        return set;
    }

    public String getRolesAsString() {
        StringBuilder sb = new StringBuilder();
        for (Role r : roles) {
            sb.append(r.getRole()) ;
        }
        return sb.toString();
    }

    public void assignRoles(Collection<Role> newRoles) {

        for ( Iterator<Role> iter = roles.iterator() ; iter.hasNext() ; )
            if ( ! newRoles.contains(iter.next()) )
                iter.remove();

        for ( Role r : newRoles )
            roles.add(r) ;
    }

    // no real need to store a boolean locked in DB, since failedLoginAttemptCount already tells us if account is locked or no
    public boolean isLocked() {
        return (failedLoginAttemptCount >= NUMBER_OF_ATTEMPTS_BEFORE_LOCKED_ACCOUNT) ;
    }

    public void assignPassword(String plainTextPwd) {
        password = passwordService.encryptPassword(plainTextPwd);
        forcePasswordChangeOnNextLogin = false ;
    }

    public void resetPassword() {
        decryptedPassword = generateNewPassword() ;
        password = passwordService.encryptPassword(decryptedPassword);
        forcePasswordChangeOnNextLogin = true ;
    }

    private String generateNewPassword() {
        return DEFAULT_PASSWORD ;
        // you may use this to enable generation of random password on reset
        // passwords may then be sent via email, if supported
        //return RandomStringUtils.randomAlphabetic(8) ;
    }

    // increment failedLoginAttemptCount if we are below threshold and return the number of attempts left
    public int trackFailedLoginAttempt() {
        if ( failedLoginAttemptCount < NUMBER_OF_ATTEMPTS_BEFORE_LOCKED_ACCOUNT )
            failedLoginAttemptCount += 1;
        return NUMBER_OF_ATTEMPTS_BEFORE_LOCKED_ACCOUNT - failedLoginAttemptCount ;
    }

    // reset failedLoginAttemptCount
    public void trackSuccessfulLoginAttempt() {
        failedLoginAttemptCount = 0;
    }

    public void unlock() {
        failedLoginAttemptCount = 0 ;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFailedLoginAttemptCount() {
        return failedLoginAttemptCount;
    }

    public void setFailedLoginAttemptCount(int failedLoginAttemptCount) {
        this.failedLoginAttemptCount = failedLoginAttemptCount;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    protected void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSuperadmin() {
        return superadmin;
    }

    public void setSuperadmin(boolean superadmin) {
        this.superadmin = superadmin;
    }

    public boolean isForcePasswordChangeOnNextLogin() {
        return forcePasswordChangeOnNextLogin;
    }

    public void setForcePasswordChangeOnNextLogin(boolean forcePasswordChangeOnNextLogin) {
        this.forcePasswordChangeOnNextLogin = forcePasswordChangeOnNextLogin;
    }

    public String getDecryptedPassword() {
        return decryptedPassword;
    }

    // ------------------------ CANONICAL METHODS ------------------------


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (email != null ? !email.equals(user.email) : user.email != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", enabled=" + enabled +
                ", superadmin=" + superadmin +
                ", forcePasswordChangeOnNextLogin=" + forcePasswordChangeOnNextLogin +
                ", failedLoginAttemptCount=" + failedLoginAttemptCount +
                ", id=" + id +
                '}';
    }
}