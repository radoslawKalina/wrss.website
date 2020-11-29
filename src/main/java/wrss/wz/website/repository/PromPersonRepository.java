package wrss.wz.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wrss.wz.website.entity.PromPersonEntity;

@Repository
public interface PromPersonRepository extends JpaRepository<PromPersonEntity, Long> {
}
