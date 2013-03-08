package myApp.model.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "PERMISSION")
public class Permission implements Serializable {

    private static final long serialVersionUID = 2782754462204947766L;

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 3, max = 25)
    @Pattern(regexp = "[A-Za-z0-9 ]*", message = "must contain only letters, numbers and spaces")
    private String permission;

    @NotNull
    @Size(min = 0, max = 500)
    @Pattern(regexp = "[A-Za-z0-9 ]*", message = "must contain only letters, numbers and spaces")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission role1 = (Permission) o;

        if (!permission.equals(role1.permission)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return permission.hashCode();
    }
}