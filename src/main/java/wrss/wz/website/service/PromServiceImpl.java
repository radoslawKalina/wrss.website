package wrss.wz.website.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import wrss.wz.website.entity.PromEnrollmentEntity;
import wrss.wz.website.entity.PromPersonEntity;
import wrss.wz.website.entity.StudentEntity;
import wrss.wz.website.model.request.PromSignUpRequest;
import wrss.wz.website.model.response.PromSignUpResponse;
import wrss.wz.website.repository.PromEnrollmentRepository;
import wrss.wz.website.repository.UserRepository;
import wrss.wz.website.service.interfaces.PromService;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class PromServiceImpl implements PromService {

    private final ModelMapper modelMapper = new ModelMapper();

    private final UserRepository userRepository;
    private final PromEnrollmentRepository promEnrollmentRepository;

    @Override
    @Transactional
    public PromSignUpResponse signUp(PromSignUpRequest promSignUpRequest, String username) {

        StudentEntity user = userRepository.findByUsername(username);
        PromPersonEntity mainPerson = modelMapper.map(promSignUpRequest.getMainPerson(), PromPersonEntity.class);

        PromEnrollmentEntity enrollment = PromEnrollmentEntity.builder()
                                                              .user(user)
                                                              .mainPerson(mainPerson)
                                                              .message(promSignUpRequest.getMessage())
                                                              .build();
        if (promSignUpRequest.isTogether()) {
            PromPersonEntity partner = modelMapper.map(promSignUpRequest.getPartner(), PromPersonEntity.class);
            enrollment.setPartner(partner);
        }

        PromEnrollmentEntity save =  promEnrollmentRepository.save(enrollment);
        return modelMapper.map(save, PromSignUpResponse.class);
    }
}