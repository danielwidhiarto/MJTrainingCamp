package com.project.bookMembership.trainer;

import java.util.Optional;

import com.project.bookMembership.DTO.*;
import com.project.bookMembership.classes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.bookMembership.config.JwtService;
import com.project.bookMembership.user.User;
import com.project.bookMembership.user.UserRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService{

    private final TrainerRepo trainerRepo;
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final TrainingClassRepo trainingClassRepo;
    private final ClassTrainerDetailRepo classTrainerDetailRepo;
    private final ClassDetailRepository classDetailRepository;
    @Autowired
    public TrainerServiceImpl(TrainerRepo trainerRepo,JwtService jwtService,UserRepo userRepo, TrainingClassRepo trainingClassRepo,ClassTrainerDetailRepo classTrainerDetailRepo,ClassDetailRepository classDetailRepository) {
        this.trainerRepo = trainerRepo;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.trainingClassRepo = trainingClassRepo;
        this.classTrainerDetailRepo = classTrainerDetailRepo;
        this.classDetailRepository = classDetailRepository;
    }


    @Override
    public Trainer save(TrainerRequest request) {
     
        
        Trainer  trainer = Trainer.builder()
            .trainerName(request.getTrainerName())
            .trainerDescription(request.getTrainerDescription())
            .build();
    
            
            return trainerRepo.save(trainer); 
    }


    @Override
    public List<GetClassResponse> getTrainerSchedule(TrainerScheduleRequest trainerScheduleRequest) {
        String email = jwtService.extractUsername(trainerScheduleRequest.getToken());
        
        Optional<User> optionalUser = userRepo.findByEmail(email);
    
        if (optionalUser.isPresent()) {
            User user = optionalUser.get(); 
            
            Trainer trainer = user.getIdTrainer(); 
            if (trainer != null) {
               
                Long trainerId = trainer.getIdTrainer();
                List<TrainingClass> classes = trainingClassRepo.findByTrainerId(trainerId);

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
                                String emails = classDetail.getIdUser() != null ? classDetail.getIdUser().getEmail() : null;

                                // Return a new ClassDetailResponse with mapped values
                                return new ClassDetailResponse(idUser, pNumber, name, emails);
                            })
                            .collect(Collectors.toList());

                    ClassTrainerDetail classTrainerDetail = classTrainerDetailRepo.findByIdClass(trainingClass.getIdClass()).orElse(null);

                    // Map trainer details to TrainerDetailResponse
                    Trainer trainerr = classTrainerDetail != null ? classTrainerDetail.getIdTrainer() : null;
                    TrainerDetailResponse trainerDetail = null;
                    if (trainerr != null) {
                        trainerDetail = new TrainerDetailResponse(
                                trainerr.getIdTrainer(),
                                trainerr.getTrainerName(),
                                trainerr.getTrainerDescription()
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
            } else {
               
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }


    @Override
    public Trainer findTrainerById(Long idTrainer) {
        return trainerRepo.findById(idTrainer)
                .orElseThrow(() -> new RuntimeException("Trainer not found with id: " + idTrainer));
   
    }


    public List<Trainer> getAll() {
        List<Trainer> trainer = trainerRepo.findAll(); 

  
        if (trainer.isEmpty()) {
            throw new RuntimeException("No trainers available at the moment.");
        }
        
        return trainer;
    }
    
}
