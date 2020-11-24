package wrss.wz.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wrss.wz.website.entity.PromEnrollmentEntity;
import wrss.wz.website.entity.StudentEntity;

import java.util.List;

@Repository
public interface PromEnrollmentRepository extends JpaRepository<PromEnrollmentEntity, Long> {
    List<PromEnrollmentEntity> findAllByUser(StudentEntity user);
}