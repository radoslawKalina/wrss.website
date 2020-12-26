package wrss.wz.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wrss.wz.website.entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    //TODO: Check what are differences between JpaRepository and CRUDRepository
    RoleEntity findByRole(String role);
}