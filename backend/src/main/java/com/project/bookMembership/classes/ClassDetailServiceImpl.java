package com.project.bookMembership.classes;

import java.util.ArrayList;

import java.util.List;


import com.project.bookMembership.DTO.*;
import org.springframework.stereotype.Service;
import com.project.bookMembership.user.User;
import com.project.bookMembership.user.UserRepo;
import com.project.bookMembership.visitPackage.VisitPackage;
import com.project.bookMembership.visitPackage.VisitPackageRepo;
import com.project.bookMembership.config.JwtService;
import com.project.bookMembership.membership.Membership;
import com.project.bookMembership.membership.MembershipRepo;

@Service
public class ClassDetailServiceImpl implements ClassDetailService {

    private final ClassDetailRepository classDetailRepository;
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final TrainingClassRepo trainingClassRepo;
    private final MembershipRepo membershipRepo;
    private final VisitPackageRepo visitRepo;

    public ClassDetailServiceImpl(
            ClassDetailRepository classDetailRepository,
            UserRepo userRepo,
            JwtService jwtService,
            TrainingClassRepo trainingClassRepo,
            MembershipRepo membershipRepo,
            VisitPackageRepo visitRepo) {
        this.classDetailRepository = classDetailRepository;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.trainingClassRepo = trainingClassRepo;
        this.membershipRepo = membershipRepo;
        this.visitRepo = visitRepo;
    }

    @Override
    public ClassDetail book(ClassDetailRequest classDetailRequest) {
        String emailz = jwtService.extractUsername(classDetailRequest.getToken());
        User user = userRepo.findByEmail(emailz)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long idTrainingClass = classDetailRequest.getIdClass();
        TrainingClass trainingClass = trainingClassRepo.findById(idTrainingClass)
                .orElseThrow(() -> new RuntimeException("Training class not found"));

        boolean isAlreadyBooked = classDetailRepository.existsByIdUserAndIdClass(user, trainingClass);
        if (isAlreadyBooked) {
            throw new RuntimeException("User has already booked this class");
        }

        int bookedCount = classDetailRepository.countByIdClass(trainingClass);
        if (bookedCount >= trainingClass.getClassCapasity()) {
            throw new RuntimeException("Class is full");
        }

        if (classDetailRequest.getType().equals("member")) {
            List<Membership> memberships = membershipRepo.findByUserId(user.getIdUser());

            boolean hasActiveMembership = memberships.stream()
                    .anyMatch(membership ->
                            !membership.getStartDate().after(trainingClass.getClassDate()) &&
                                    !membership.getEndDate().before(trainingClass.getClassDate()) &&
                                    "VERIFIED".equalsIgnoreCase(membership.getTransaction().getPaymentStatus()) // Check if payment status is PAID
                    );

            if (!hasActiveMembership) {
                throw new RuntimeException("User does not have an active membership or membership is not verified for this class date.");
            }
        } else if (classDetailRequest.getType().equals("visit")) {
            List<VisitPackage> visitPackages = visitRepo.findByUserId(user.getIdUser());

            boolean hasAvailableVisit = false;
            for (VisitPackage visitPackage : visitPackages) {
                if (visitPackage.getVisitNumber() > visitPackage.getVisitUsed()) {
                    hasAvailableVisit = true;
                    visitPackage.incrementUsedVisits();
                    visitRepo.save(visitPackage);
                    break;
                }
            }
            if (!hasAvailableVisit) {
                throw new RuntimeException("No visits available");
            }
        }
        var classDetail = ClassDetail.builder()
                .idUser(user)
                .idClass(trainingClass)
                .build();

        return classDetailRepository.save(classDetail);
    }
    public ClassBookingStatusResponse checkBookingEligibility(ClassDetailRequest classDetailRequest) {
        // Extract user information from the token
        String email = jwtService.extractUsername(classDetailRequest.getToken());
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));



        // Get the training class information
        Long idTrainingClass = classDetailRequest.getIdClass();
        TrainingClass trainingClass = trainingClassRepo.findById(idTrainingClass)
                .orElseThrow(() -> new RuntimeException("Training class not found"));

        // Check if the user has already booked the class
        boolean alreadyBooked = classDetailRepository.existsByIdUserAndIdClass(user, trainingClass);

        // Check if the class is full
        int bookedCount = classDetailRepository.countByIdClass(trainingClass);
        boolean isClassFull = bookedCount >= trainingClass.getClassCapasity();

        // Check if the user has an active membership for the class date
        List<Membership> memberships = membershipRepo.findByUserId(user.getIdUser());

        boolean validMember = memberships.stream()
                .anyMatch(membership ->
                        !membership.getStartDate().after(trainingClass.getClassDate()) &&
                                !membership.getEndDate().before(trainingClass.getClassDate())
                );

        // Check if the user has available visits
        List<VisitPackage> visitPackages = visitRepo.findByUserId(user.getIdUser());
        boolean validVisit = visitPackages.stream()
                .anyMatch(visitPackage -> visitPackage.getVisitNumber() > visitPackage.getVisitUsed());

        // Create and populate response
        ClassBookingStatusResponse response = new ClassBookingStatusResponse();
        response.setValidMember(validMember);
        response.setValidVisit(validVisit);
        response.setAlreadyBooked(alreadyBooked);

        // Populate Membership Details
        List<MembershipDetailResponse> membershipDetails = new ArrayList<>();
        for (Membership membership : memberships) {
            MembershipDetailResponse detail = MembershipDetailResponse.builder()
                    .idMember(membership.getIdMember())
                    .duration(membership.getDuration())
                    .startDate(membership.getStartDate().toString())
                    .endDate(membership.getEndDate().toString())
                    .price(membership.getPrice())
                    .transactionId(membership.getTransaction().getIdTransaction())
                    .userId(membership.getUser().getIdUser())
                    .build();
            membershipDetails.add(detail);
        }
        response.setMemberships(membershipDetails);

        // Populate Visit Details
        List<VisitDetailResponse> visitDetails = new ArrayList<>();
        for (VisitPackage visitPackage : visitPackages) {
            VisitDetailResponse detail = VisitDetailResponse.builder()
                    .idVisit(visitPackage.getIdVisit())
                    .price(visitPackage.getPrice())
                    .visitNumber(visitPackage.getVisitNumber())
                    .visitUsed(visitPackage.getVisitUsed())
                    .transactionId(visitPackage.getTransaction().getIdTransaction())
                    .userId(visitPackage.getUser().getIdUser())
                    .build();
            visitDetails.add(detail);
        }
        response.setVisitDetails(visitDetails); // Use the updated field name here

        return response;
    }

    @Override
    public MembershipStatusResponse checkMembershipStatus(MembershipStatusRequest membershipStatusRequest) {
        // Extract user information from the token
        String email = jwtService.extractUsername(membershipStatusRequest.getToken());
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user has an active membership for the class date
        List<Membership> memberships = membershipRepo.findByUserId(user.getIdUser());

        // Check if the user has available visits
        List<VisitPackage> visitPackages = visitRepo.findByUserId(user.getIdUser());

        // Create and populate response
        MembershipStatusResponse response = new MembershipStatusResponse();

        // Populate Membership Details
        List<MembershipDetailResponse> membershipDetails = new ArrayList<>();
        for (Membership membership : memberships) {
            MembershipDetailResponse detail = MembershipDetailResponse.builder()
                    .idMember(membership.getIdMember())
                    .duration(membership.getDuration())
                    .startDate(membership.getStartDate().toString())
                    .endDate(membership.getEndDate().toString())
                    .price(membership.getPrice())
                    .transactionId(membership.getTransaction().getIdTransaction())
                    .userId(membership.getUser().getIdUser())
                    .build();
            membershipDetails.add(detail);
        }
        response.setMemberships(membershipDetails);

        // Populate Visit Details
        List<VisitDetailResponse> visitDetails = new ArrayList<>();
        for (VisitPackage visitPackage : visitPackages) {
            VisitDetailResponse detail = VisitDetailResponse.builder()
                    .idVisit(visitPackage.getIdVisit())
                    .price(visitPackage.getPrice())
                    .visitNumber(visitPackage.getVisitNumber())
                    .visitUsed(visitPackage.getVisitUsed())
                    .transactionId(visitPackage.getTransaction().getIdTransaction())
                    .userId(visitPackage.getUser().getIdUser())
                    .build();
            visitDetails.add(detail);
        }
        response.setVisitDetails(visitDetails); // Use the updated field name here

        return response;
    }

}