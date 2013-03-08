package myApp.repository;

import myApp.model.accesslog.AccessLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;


/**
 * User: eluibon
 * Date: 04/01/13
 * Time: 16.17
 */
public interface AccessLogEntryRepository extends JpaRepository<AccessLogEntry, Long>, QueryDslPredicateExecutor {

}
