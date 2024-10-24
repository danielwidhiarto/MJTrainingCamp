package com.project.bookMembership.classes;

import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ClassTrainerDetailServiceImpl implements ClassTrainerDetailService {
    private final ClassTrainerDetailRepo classTrainerDetailRepo;

    public ClassTrainerDetailServiceImpl(ClassTrainerDetailRepo classTrainerDetailRepo) {
        this.classTrainerDetailRepo = classTrainerDetailRepo;
    }

    @Override
    public ClassTrainerDetail getbyClassiD(Long idClass) {

        Optional<ClassTrainerDetail> optionalClassTrainerDetail = classTrainerDetailRepo.findByIdClass(idClass);

        return optionalClassTrainerDetail.orElse(null);
    }
}
