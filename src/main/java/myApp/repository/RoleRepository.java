package myApp.repository;

import myApp.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;


/**
 * User: eluibon
 * Date: 04/01/13
 * Time: 16.17
 */
public interface RoleRepository extends JpaRepository<Role, Long>, QueryDslPredicateExecutor {
}
