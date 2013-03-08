package myApp.model.user;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "ROLE")
public class Role implements Serializable {

    private static final long serialVersionUID = -3718310321840466736L;

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 3, max = 25)
    @Pattern(regexp = "[A-Za-z0-9 ]*", message = "must contain only letters, numbers and spaces")
    private String role;

    @NotNull
    @Size(min = 0, max = 500)
    @Pattern(regexp = "[A-Za-z0-9 ]*", message = "must contain only letters, numbers and spaces")
    private String description;

    @ManyToMany
    @JoinTable(
            name="ROLE_PERMISSION",
            joinColumns=@JoinColumn(name="ROLE_ID", referencedColumnName="ID"),
            inverseJoinColumns=@JoinColumn(name="PERMISSION_ID", referencedColumnName="ID")
    )
    private Set<Permission> permissions ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role1 = (Role) o;

        if (!role.equals(role1.role)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return role.hashCode();
    }
}