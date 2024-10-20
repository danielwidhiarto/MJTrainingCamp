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
        // Fetch ClassTrainerDetail by idClass from the repository
        Optional<ClassTrainerDetail> optionalClassTrainerDetail = classTrainerDetailRepo.findByIdClass(idClass);

        // Return the ClassTrainerDetail if present, or null if not found
        return optionalClassTrainerDetail.orElse(null); // This will return ClassTrainerDetail or null
    }
}
