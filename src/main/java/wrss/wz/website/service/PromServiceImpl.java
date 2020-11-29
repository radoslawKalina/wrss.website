package wrss.wz.website.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import wrss.wz.website.entity.PromEnrollmentEntity;
import wrss.wz.website.entity.PromPersonEntity;
import wrss.wz.website.entity.StudentEntity;
import wrss.wz.website.exception.custom.PromEnrollmentDoesNotExist;
import wrss.wz.website.exception.custom.PromPersonEntityNotExistException;
import wrss.wz.website.exception.custom.RecordBelongsException;
import wrss.wz.website.model.request.PromEnrollmentPersonRequest;
import wrss.wz.website.model.request.PromEnrollmentRequest;
import wrss.wz.website.model.response.PromGetEnrollmentResponse;
import wrss.wz.website.model.response.PromEnrollmentResponse;
import wrss.wz.website.repository.PromEnrollmentRepository;
import wrss.wz.website.repository.PromPersonRepository;
import wrss.wz.website.repository.UserRepository;
import wrss.wz.website.service.interfaces.PromService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromServiceImpl implements PromService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PromPersonRepository promPersonRepository;
    private final PromEnrollmentRepository promEnrollmentRepository;

    @Override
    @Transactional
    public List<PromGetEnrollmentResponse> getAll(String username) {

        StudentEntity user = userRepository.findByUsername(username);
        List<PromEnrollmentEntity> allPersonEnrollments = promEnrollmentRepository.findAllByUser(user);

        List<PromGetEnrollmentResponse> response = new ArrayList<>();
        allPersonEnrollments.forEach(enrollment -> response.add(modelMapper.map(enrollment, PromGetEnrollmentResponse.class)));

        return response;
    }

    @Override
    @Transactional
    public PromGetEnrollmentResponse get(String enrollmentId, String username) {

        StudentEntity user = userRepository.findByUsername(username);
        PromEnrollmentEntity promEnrollmentEntity = getPromEnrollmentEntity(enrollmentId, user);
        return modelMapper.map(promEnrollmentEntity, PromGetEnrollmentResponse.class);
    }

    @Override
    @Transactional
    public PromEnrollmentResponse signUp(PromEnrollmentRequest promEnrollmentRequest, String username) {

        StudentEntity user = userRepository.findByUsername(username);
        PromPersonEntity mainPerson = modelMapper.map(promEnrollmentRequest.getMainPerson(), PromPersonEntity.class);

        PromEnrollmentEntity enrollment = PromEnrollmentEntity.builder()
                                                              .user(user)
                                                              .promEnrollmentCustomId(UUID.randomUUID().toString())
                                                              .mainPerson(mainPerson)
                                                              .message(promEnrollmentRequest.getMessage())
                                                              .build();
        if (promEnrollmentRequest.getType().equals("pair")) {
            PromPersonEntity partner = modelMapper.map(promEnrollmentRequest.getPartner(), PromPersonEntity.class);
            enrollment.setPartner(partner);
        }

        PromEnrollmentEntity save = promEnrollmentRepository.save(enrollment);
        return modelMapper.map(save, PromEnrollmentResponse.class);
    }

    @Override
    @Transactional
    public void update(PromEnrollmentPersonRequest promEnrollmentPersonRequest, String enrollmentId,
                                         String person, String username) {

        StudentEntity user = userRepository.findByUsername(username);
        PromEnrollmentEntity promEnrollmentEntity = getPromEnrollmentEntity(enrollmentId, user);

        PromPersonEntity promPersonEntityToUpdate = getPromPersonEntity(promEnrollmentEntity, person);

        PromPersonEntity promPersonEntityUpdated = modelMapper.map(promEnrollmentPersonRequest, PromPersonEntity.class);
        promPersonEntityUpdated.setPromPersonId(promPersonEntityToUpdate.getPromPersonId());

        promPersonRepository.save(promPersonEntityUpdated);
    }

    private PromEnrollmentEntity getPromEnrollmentEntity(String enrollmentId, StudentEntity user) {

        PromEnrollmentEntity promEnrollmentEntity = promEnrollmentRepository.findByPromEnrollmentCustomId(enrollmentId);

        if (promEnrollmentEntity == null) {
            throw new PromEnrollmentDoesNotExist("Prom enrollment with this Id does not exist");
        }

        if (!user.getUsername().equals(promEnrollmentEntity.getUser().getUsername())) {
            throw new RecordBelongsException("This record belong to another user");
        }

        return promEnrollmentEntity;
    }

    private PromPersonEntity getPromPersonEntity(PromEnrollmentEntity promEnrollmentEntity, String person) {

        PromPersonEntity promPersonEntity = null;

        if (person.equals("main")) {
            promPersonEntity = promEnrollmentEntity.getMainPerson();

        } else if (person.equals("partner")) {
            promPersonEntity = promEnrollmentEntity.getPartner();
        }

        if (promPersonEntity == null) {
            throw new PromPersonEntityNotExistException("Person to update does not exist");
        }

        return promPersonEntity;
    }
}