package wrss.wz.website.repository;

import org.springframework.data.repository.CrudRepository;
import wrss.wz.website.entity.Student;

public interface TestRepository extends CrudRepository<Student, Long> {
}
