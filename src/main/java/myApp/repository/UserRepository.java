package myApp.repository;

import myApp.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import javax.persistence.QueryHint;
import java.util.List;


/**
 * User: eluibon
 * Date: 04/01/13
 * Time: 16.17
 */
public interface UserRepository extends JpaRepository<User, Long>, QueryDslPredicateExecutor {

    // JPA/Hibernate cache through JBoss-provided Infinispan cache
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    User findByUsername(String username) ;

    User findByEmail(String email) ;
}
