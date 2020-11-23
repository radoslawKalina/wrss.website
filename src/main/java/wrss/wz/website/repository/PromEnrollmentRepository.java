package wrss.wz.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wrss.wz.website.entity.PromEnrollmentEntity;

@Repository
public interface PromEnrollmentRepository extends JpaRepository<PromEnrollmentEntity, Long> {
}