package wrss.wz.website.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import wrss.wz.website.entity.PromEnrollmentEntity;
import wrss.wz.website.model.request.PaidRequest;
import wrss.wz.website.model.request.PromEnrollmentPersonRequest;
import wrss.wz.website.model.response.PromEnrollmentPersonResponse;
import wrss.wz.website.model.response.PromEnrollmentResponse;
import wrss.wz.website.service.interfaces.PromAdminService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromAdminServiceImpl implements PromAdminService {

    private static final String ADMIN = "ADMIN";

    private final ModelMapper modelMapper;

    private final PromBase promBase;

    @Override
    public List<PromEnrollmentResponse> getAllEnrollments() {

        return promBase.getRepository().findAll()
                                        .stream()
                                        .map(enrollment -> modelMapper.map(enrollment, PromEnrollmentResponse.class))
                                        .collect(Collectors.toList());
    }

    @Override
    public PromEnrollmentResponse getEnrollment(UUID enrollmentId) {

        return promBase.getEnrollment(enrollmentId, ADMIN, true);
    }

    @Override
    public PromEnrollmentPersonResponse updateEnrollment(PromEnrollmentPersonRequest promEnrollmentPersonRequest,
                                                         UUID enrollmentId, String person) {

        return promBase.updateEnrollment(promEnrollmentPersonRequest, enrollmentId, person, ADMIN, true);
    }

    @Override
    public void deleteEnrollment(UUID enrollmentId) {

        PromEnrollmentEntity promEnrollmentEntity = promBase.getPromEnrollmentEntity(enrollmentId, ADMIN, true);
        promBase.getRepository().delete(promEnrollmentEntity);
    }

    @Override
    public PromEnrollmentResponse changeEnrollmentPaidStatus(UUID enrollmentId, PaidRequest paidRequest) {

        PromEnrollmentEntity promEnrollmentEntity = promBase.getPromEnrollmentEntity(enrollmentId, ADMIN, true);
        promEnrollmentEntity.setPaid(paidRequest.isPaid());

        PromEnrollmentEntity updatedPromEnrollmentEntity = promBase.getRepository().save(promEnrollmentEntity);

        return modelMapper.map(updatedPromEnrollmentEntity, PromEnrollmentResponse.class);
    }
}
