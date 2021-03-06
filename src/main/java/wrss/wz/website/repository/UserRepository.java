package wrss.wz.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wrss.wz.website.entity.StudentEntity;

@Repository
public interface UserRepository extends JpaRepository<StudentEntity, Long> {

    StudentEntity findByUsername(String username);
}