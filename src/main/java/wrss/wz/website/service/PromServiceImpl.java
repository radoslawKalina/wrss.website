package wrss.wz.website.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import wrss.wz.website.entity.PromEnrollmentEntity;
import wrss.wz.website.entity.PromPersonEntity;
import wrss.wz.website.entity.StudentEntity;
import wrss.wz.website.model.request.PromEnrollmentRequest;
import wrss.wz.website.model.response.PromGetEnrollmentResponse;
import wrss.wz.website.model.response.PromSignUpResponse;
import wrss.wz.website.repository.PromEnrollmentRepository;
import wrss.wz.website.repository.UserRepository;
import wrss.wz.website.service.interfaces.PromService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromServiceImpl implements PromService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PromEnrollmentRepository promEnrollmentRepository;

    @Override
    public List<PromGetEnrollmentResponse> getAll(String username) {

        StudentEntity user = userRepository.findByUsername(username);
        List<PromEnrollmentEntity> allPersonEnrollments = promEnrollmentRepository.findAllByUser(user);

        List<PromGetEnrollmentResponse> response = new ArrayList<>();
        allPersonEnrollments.forEach(enrollment -> response.add(modelMapper.map(enrollment, PromGetEnrollmentResponse.class)));

        return response;
    }

    @Override
    @Transactional
    public PromSignUpResponse signUp(PromEnrollmentRequest promEnrollmentRequest, String username) {

        StudentEntity user = userRepository.findByUsername(username);
        PromPersonEntity mainPerson = modelMapper.map(promEnrollmentRequest.getMainPerson(), PromPersonEntity.class);

        PromEnrollmentEntity enrollment = PromEnrollmentEntity.builder()
                                                              .user(user)
                                                              .mainPerson(mainPerson)
                                                              .message(promEnrollmentRequest.getMessage())
                                                              .build();
        if (promEnrollmentRequest.getType().equals("pair")) {
            PromPersonEntity partner = modelMapper.map(promEnrollmentRequest.getPartner(), PromPersonEntity.class);
            enrollment.setPartner(partner);
        }

        PromEnrollmentEntity save =  promEnrollmentRepository.save(enrollment);
        return modelMapper.map(save, PromSignUpResponse.class);
    }
}