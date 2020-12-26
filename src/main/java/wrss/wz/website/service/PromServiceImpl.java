package wrss.wz.website.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import wrss.wz.website.entity.PromEnrollmentEntity;
import wrss.wz.website.entity.PromPersonEntity;
import wrss.wz.website.entity.StudentEntity;
import wrss.wz.website.exception.custom.UserDoesNotExistException;
import wrss.wz.website.model.request.PromEnrollmentPersonRequest;
import wrss.wz.website.model.request.PromEnrollmentRequest;
import wrss.wz.website.model.response.PromEnrollmentPersonResponse;
import wrss.wz.website.model.response.PromEnrollmentResponse;
import wrss.wz.website.repository.UserRepository;
import wrss.wz.website.service.interfaces.PromService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromServiceImpl implements PromService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PromBase promBase;

    @Override
    @Transactional
    public List<PromEnrollmentResponse> getOwnEnrollments(String username) {

        StudentEntity user = userRepository.findByUsername(username);

        return promBase.getRepository().findAllByUser(user)
                                       .stream()
                                       .map(enrollment -> modelMapper.map(enrollment, PromEnrollmentResponse.class))
                                       .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PromEnrollmentResponse getEnrollment(UUID enrollmentId, String username) {

        return promBase.getEnrollment(enrollmentId, username, false);
    }

    @Override
    @Transactional
    public PromEnrollmentResponse createEnrollment(PromEnrollmentRequest promEnrollmentRequest, String username) {

        StudentEntity user = userRepository.findByUsername(username);
        PromPersonEntity mainPerson = modelMapper.map(promEnrollmentRequest.getMainPerson(), PromPersonEntity.class);

        PromEnrollmentEntity enrollment = PromEnrollmentEntity.builder()
                                                              .user(user)
                                                              .mainPerson(mainPerson)
                                                              .message(promEnrollmentRequest.getMessage())
                                                              .paid(false)
                                                              .build();

        if (promEnrollmentRequest.getType().equals("pair")) {
            PromPersonEntity partner = modelMapper.map(promEnrollmentRequest.getPartner(), PromPersonEntity.class);
            enrollment.setPartner(partner);
        }

        PromEnrollmentEntity save = promBase.getRepository().save(enrollment);

        return modelMapper.map(save, PromEnrollmentResponse.class);
    }

    @Override
    @Transactional
    public PromEnrollmentPersonResponse updateEnrollment(PromEnrollmentPersonRequest promEnrollmentPersonRequest, UUID enrollmentId,
                                                         String person, String username) {

        return promBase.updateEnrollment(promEnrollmentPersonRequest, enrollmentId, person, username, false);
    }

    @Override
    @Transactional
    public void transferEnrollment(UUID enrollmentId, String newUsername, String username) {

        PromEnrollmentEntity promEnrollmentEntity = promBase.getPromEnrollmentEntity(enrollmentId, username, false);

        String errorMessage = "User to whom you try to transfer enrollment does not exist";
        StudentEntity newUser = Optional.ofNullable(userRepository.findByUsername(newUsername))
                                        .orElseThrow(() -> new UserDoesNotExistException(errorMessage));

        promEnrollmentEntity.setUser(newUser);
        promBase.getRepository().save(promEnrollmentEntity);
    }
}