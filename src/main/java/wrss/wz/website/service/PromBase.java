package wrss.wz.website.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import wrss.wz.website.entity.PromEnrollmentEntity;
import wrss.wz.website.entity.PromPersonEntity;
import wrss.wz.website.exception.custom.PromEnrollmentDoesNotExist;
import wrss.wz.website.exception.custom.PromPersonEntityNotExistException;
import wrss.wz.website.exception.custom.RecordBelongingException;
import wrss.wz.website.model.request.PromEnrollmentPersonRequest;
import wrss.wz.website.model.response.PromEnrollmentPersonResponse;
import wrss.wz.website.model.response.PromEnrollmentResponse;
import wrss.wz.website.repository.PromEnrollmentRepository;
import wrss.wz.website.repository.PromPersonRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PromBase {

    private final ModelMapper modelMapper;
    private final PromPersonRepository promPersonRepository;
    private final PromEnrollmentRepository promEnrollmentRepository;

    public PromEnrollmentRepository getRepository() {
        return promEnrollmentRepository;
    }

    public PromEnrollmentEntity getPromEnrollmentEntity(UUID enrollmentId, String username, boolean isAdmin) {

        return Optional.ofNullable(promEnrollmentRepository.findByPromEnrollmentId(enrollmentId))
                       .filter(enrollment -> checkEligibility(username, enrollment.getUser().getUsername(), isAdmin))
                       .orElseThrow(() -> new PromEnrollmentDoesNotExist("Prom enrollment with this Id does not exist"));
    }

    public PromEnrollmentResponse getEnrollment(UUID enrollmentId, String username, boolean isAdmin) {

        PromEnrollmentEntity promEnrollmentEntity = getPromEnrollmentEntity(enrollmentId, username, isAdmin);
        return modelMapper.map(promEnrollmentEntity, PromEnrollmentResponse.class);
    }

    public PromEnrollmentPersonResponse updateEnrollment(PromEnrollmentPersonRequest promEnrollmentPersonRequest,
                                                         UUID enrollmentId, String person, String username, boolean isAdmin) {

        PromEnrollmentEntity promEnrollmentEntity = getPromEnrollmentEntity(enrollmentId, username, isAdmin);
        PromPersonEntity promPersonEntityToUpdate = getPromPersonEntity(promEnrollmentEntity, person);

        PromPersonEntity promPersonEntityUpdated = modelMapper.map(promEnrollmentPersonRequest, PromPersonEntity.class);
        promPersonEntityUpdated.setPromPersonId(promPersonEntityToUpdate.getPromPersonId());

        PromPersonEntity updatedEntity = promPersonRepository.save(promPersonEntityUpdated);

        return modelMapper.map(updatedEntity, PromEnrollmentPersonResponse.class);
    }

    private boolean checkEligibility(String username, String enrollmentUsername, boolean isAdmin) {

        if (!isAdmin && !username.equals(enrollmentUsername)) {
            throw new RecordBelongingException("This record belong to another user");
        }
        return true;
    }

    private PromPersonEntity getPromPersonEntity(PromEnrollmentEntity promEnrollmentEntity, String person) {

        if (person.equals("mainPerson")) {
            return promEnrollmentEntity.getMainPerson();
        }

        if (person.equals("partner")) {
            return Optional.ofNullable(promEnrollmentEntity.getPartner())
                           .orElseThrow(() -> new PromPersonEntityNotExistException("Person to update does not exist"));
        }

        throw new PromPersonEntityNotExistException("Person to update does not exist");
    }
}
