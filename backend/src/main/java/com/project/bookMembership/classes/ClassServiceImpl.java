package com.project.bookMembership.classes;

import java.util.List;


import com.project.bookMembership.DTO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import com.project.bookMembership.config.JwtService;
import com.project.bookMembership.trainer.Trainer;
import com.project.bookMembership.trainer.TrainerRepo;
import com.project.bookMembership.user.User;
import com.project.bookMembership.user.UserRepo;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.Date;

@Service
public class ClassServiceImpl implements ClassService {

    private final TrainingClassRepo trainingClassRepo;
    private final ClassTrainerDetailRepo classTrainerRepo;
    private final TrainerRepo trainerRepo;
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final ClassDetailRepository classDetailRepository;
    private final ClassTrainerDetailRepo classTrainerDetailRepo;


    @Autowired
    public ClassServiceImpl(ClassTrainerDetailRepo classTrainerDetailRepo,TrainingClassRepo trainingClassRepo, ClassTrainerDetailRepo classTrainerRepo,
                                TrainerRepo trainerRepo, JwtService jwtService,UserRepo userRepo, ClassDetailRepository classDetailRepository) {
        this.trainingClassRepo = trainingClassRepo;
        this.classTrainerRepo = classTrainerRepo; 
        this.trainerRepo = trainerRepo; 
        this.jwtService = jwtService; 
        this.userRepo = userRepo; 
        this.classDetailRepository = classDetailRepository;
        this.classTrainerDetailRepo = classTrainerDetailRepo;
    }


    @Override
    public TrainingClass save(ClassRequest request) {
    
        var trainingClass = TrainingClass.builder()
            .classDate(request.getClassDate())
            .classTime(request.getClassTime())
            .classCapasity(request.getClassCapasity())
            .className(request.getClassName())
            .classRequirement(request.getClassRequirement())
            .build();

        TrainingClass savedClass = trainingClassRepo.save(trainingClass);

        Trainer trainer = trainerRepo.findById(request.getIdTrainer())
        .orElseThrow(() -> new RuntimeException("Trainer not found"));
        var classTrainerDetail = ClassTrainerDetail.builder()
            .idTrainer(trainer)  
            .idClass(savedClass)  
            .build();
     
            classTrainerRepo.save(classTrainerDetail);  

        return savedClass;
    }

    public List<GetClassResponse> getTrainingClass() {
        List<TrainingClass> classes = trainingClassRepo.findAll();
        List<GetClassResponse> responseList = new ArrayList<>();

        if (classes.isEmpty()) {
            return responseList;
        }

        for (TrainingClass trainingClass : classes) {
            // Fetch all class details for each class
            List<ClassDetail> classDetails = classDetailRepository.findByIdClass(trainingClass.getIdClass());

            // Map class details to ClassDetailResponse
            List<ClassDetailResponse> classMembers = classDetails.stream()
                    .map(classDetail -> {
                        // Ensure that classDetail and idUser are not null before accessing fields
                        Long idUser = classDetail.getIdUser() != null ? classDetail.getIdUser().getIdUser() : null;
                        String pNumber = classDetail.getIdUser() != null ? classDetail.getIdUser().getPNumber() : null;
                        String name = classDetail.getIdUser() != null ? classDetail.getIdUser().getName() : null;
                        String email = classDetail.getIdUser() != null ? classDetail.getIdUser().getEmail() : null;

                        // Return a new ClassDetailResponse with mapped values
                        return new ClassDetailResponse(idUser, pNumber, name, email);
                    })
                    .collect(Collectors.toList());

            ClassTrainerDetail classTrainerDetail = classTrainerDetailRepo.findByIdClass(trainingClass.getIdClass()).orElse(null);

            // Map trainer details to TrainerDetailResponse
            Trainer trainer = classTrainerDetail != null ? classTrainerDetail.getIdTrainer() : null;
            TrainerDetailResponse trainerDetail = null;
            if (trainer != null) {
                trainerDetail = new TrainerDetailResponse(
                        trainer.getIdTrainer(),
                        trainer.getTrainerName(),
                        trainer.getTrainerDescription()
                );
            }
            // Build the response for this class
            GetClassResponse classResponse = GetClassResponse.builder()
                    .idClass(trainingClass.getIdClass())
                    .className(trainingClass.getClassName())
                    .classRequirement(trainingClass.getClassRequirement())
                    .classDate(trainingClass.getClassDate())
                    .classTime(trainingClass.getClassTime())
                    .classCapasity(trainingClass.getClassCapasity())
                    .trainerDetail(trainerDetail)
                    .classMembers(classMembers)
                    .build();

            responseList.add(classResponse);
        }

        return responseList;
    }

    @Override
    public List<GetClassResponse> getTrainingClassById(Long id) {

        List<TrainingClass> classes = trainingClassRepo.findByClassId(id);
        List<GetClassResponse> responseList = new ArrayList<>();

        if (classes.isEmpty()) {
            return responseList;
        }

        for (TrainingClass trainingClass : classes) {

            List<ClassDetail> classDetails = classDetailRepository.findByIdClass(trainingClass.getIdClass());

            List<ClassDetailResponse> classMembers = classDetails.stream()
                    .map(classDetail -> {
                        // Ensure that classDetail and idUser are not null before accessing fields
                        Long idUser = classDetail.getIdUser() != null ? classDetail.getIdUser().getIdUser() : null;
                        String pNumber = classDetail.getIdUser() != null ? classDetail.getIdUser().getPNumber() : null;
                        String name = classDetail.getIdUser() != null ? classDetail.getIdUser().getName() : null;
                        String email = classDetail.getIdUser() != null ? classDetail.getIdUser().getEmail() : null;

                        // Return a new ClassDetailResponse with mapped values
                        return new ClassDetailResponse(idUser, pNumber, name, email);
                    })
                    .collect(Collectors.toList());

            ClassTrainerDetail classTrainerDetail = classTrainerDetailRepo.findByIdClass(trainingClass.getIdClass()).orElse(null);

            Trainer trainer = classTrainerDetail != null ? classTrainerDetail.getIdTrainer() : null;
            TrainerDetailResponse trainerDetail = null;
            if (trainer != null) {
                trainerDetail = new TrainerDetailResponse(
                        trainer.getIdTrainer(),
                        trainer.getTrainerName(),
                        trainer.getTrainerDescription()
                );
            }

            GetClassResponse classResponse = GetClassResponse.builder()
                    .idClass(trainingClass.getIdClass())
                    .className(trainingClass.getClassName())
                    .classRequirement(trainingClass.getClassRequirement())
                    .classDate(trainingClass.getClassDate())
                    .classTime(trainingClass.getClassTime())
                    .classCapasity(trainingClass.getClassCapasity())
                    .trainerDetail(trainerDetail)
                    .classMembers(classMembers)
                    .classNote(trainingClass.getClassNote())
                    .build();

            responseList.add(classResponse);
        }

        return responseList;

    }

    @Override
    public List<GetClassResponse> getTrainingClassByDate(Date date) {
        List<TrainingClass> classes = trainingClassRepo.findByClassDate(date);
        List<GetClassResponse> responseList = new ArrayList<>();

        if (classes.isEmpty()) {
            return responseList;
        }

        for (TrainingClass trainingClass : classes) {

            List<ClassDetail> classDetails = classDetailRepository.findByIdClass(trainingClass.getIdClass());


            List<ClassDetailResponse> classMembers = classDetails.stream()
                    .map(classDetail -> {
                        // Ensure that classDetail and idUser are not null before accessing fields
                        Long idUser = classDetail.getIdUser() != null ? classDetail.getIdUser().getIdUser() : null;
                        String pNumber = classDetail.getIdUser() != null ? classDetail.getIdUser().getPNumber() : null;
                        String name = classDetail.getIdUser() != null ? classDetail.getIdUser().getName() : null;
                        String email = classDetail.getIdUser() != null ? classDetail.getIdUser().getEmail() : null;

                        // Return a new ClassDetailResponse with mapped values
                        return new ClassDetailResponse(idUser, pNumber, name, email);
                    })
                    .collect(Collectors.toList());

            ClassTrainerDetail classTrainerDetail = classTrainerDetailRepo.findByIdClass(trainingClass.getIdClass()).orElse(null);

            Trainer trainer = classTrainerDetail != null ? classTrainerDetail.getIdTrainer() : null;
            TrainerDetailResponse trainerDetail = null;
            if (trainer != null) {
                trainerDetail = new TrainerDetailResponse(
                        trainer.getIdTrainer(),
                        trainer.getTrainerName(),
                        trainer.getTrainerDescription()
                );
            }

            GetClassResponse classResponse = GetClassResponse.builder()
                    .idClass(trainingClass.getIdClass())
                    .className(trainingClass.getClassName())
                    .classRequirement(trainingClass.getClassRequirement())
                    .classDate(trainingClass.getClassDate())
                    .classTime(trainingClass.getClassTime())
                    .classCapasity(trainingClass.getClassCapasity())
                    .trainerDetail(trainerDetail)
                    .classMembers(classMembers)
                    .build();

            responseList.add(classResponse);
        }

        return responseList;
    }

    @Override
    public List<GetClassResponse> getClassHistory(ClassHistoryRequest classHistoryRequest) {
        String email = jwtService.extractUsername(classHistoryRequest.getToken());

        Optional<User> optionalUser = userRepo.findByEmail(email);
        List<GetClassResponse> responseList = new ArrayList<>();
        if (optionalUser.isPresent()) {
            User user = optionalUser.get(); 
            
            List<TrainingClass> trainingClasses = trainingClassRepo.findByUserId(user.getIdUser());

            if (trainingClasses.isEmpty()) {
                return responseList;
            }

            for (TrainingClass trainingClass : trainingClasses) {

                List<ClassDetail> classDetails = classDetailRepository.findByIdClass(trainingClass.getIdClass());
                List<ClassDetailResponse> classMembers = classDetails.stream()
                        .map(classDetail -> {
                            // Ensure that classDetail and idUser are not null before accessing fields
                            Long idUser = classDetail.getIdUser() != null ? classDetail.getIdUser().getIdUser() : null;
                            String pNumber = classDetail.getIdUser() != null ? classDetail.getIdUser().getPNumber() : null;
                            String name = classDetail.getIdUser() != null ? classDetail.getIdUser().getName() : null;
                            String emaill = classDetail.getIdUser() != null ? classDetail.getIdUser().getEmail() : null;

                            // Return a new ClassDetailResponse with mapped values
                            return new ClassDetailResponse(idUser, pNumber, name, emaill);
                        })
                        .collect(Collectors.toList());

                ClassTrainerDetail classTrainerDetail = classTrainerDetailRepo.findByIdClass(trainingClass.getIdClass()).orElse(null);

                Trainer trainer = classTrainerDetail != null ? classTrainerDetail.getIdTrainer() : null;
                TrainerDetailResponse trainerDetail = null;
                if (trainer != null) {
                    trainerDetail = new TrainerDetailResponse(
                            trainer.getIdTrainer(),
                            trainer.getTrainerName(),
                            trainer.getTrainerDescription()
                    );
                }

                GetClassResponse classResponse = GetClassResponse.builder()
                        .idClass(trainingClass.getIdClass())
                        .className(trainingClass.getClassName())
                        .classRequirement(trainingClass.getClassRequirement())
                        .classDate(trainingClass.getClassDate())
                        .classTime(trainingClass.getClassTime())
                        .classCapasity(trainingClass.getClassCapasity())
                        .trainerDetail(trainerDetail)
                        .classMembers(classMembers)
                        .build();

                responseList.add(classResponse);
            }

            return responseList;
        } else {

            return new ArrayList<>(); 
        }
    }

    @Override
    public String addNotes(AddNotesRequest request) {
        try {
            // Cari objek TrainingClass berdasarkan ID
            TrainingClass classes = trainingClassRepo.findOneClassbyId(request.getIdClass());

            // Jika kelas tidak ditemukan, kembalikan pesan kesalahan
            if (classes == null) {
                return "class with ID " + request.getIdClass() + " not found .";
            }

            // Set catatan ke objek TrainingClass
            classes.setClassNote(request.getNotes());

            // Simpan perubahan ke database
            trainingClassRepo.save(classes);

            return "add notes succesfull.";
        } catch (Exception e) {
            // Tangani kesalahan dan log jika diperlukan
            e.printStackTrace();
            return "error =  " + e.getMessage();
        }
    }
}
