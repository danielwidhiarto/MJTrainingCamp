package com.project.bookMembership.classes;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.bookMembership.user.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ClassDetailRepository extends JpaRepository<ClassDetail,Long>{

    Optional<TrainingClass> findTrainingClassById(Long id);
    boolean existsByIdUserAndIdClass(User user, TrainingClass trainingClass);
    int countByIdClass(TrainingClass trainingClass);

    @Query(value = "SELECT * FROM class_detail WHERE class_id = :idClass", nativeQuery = true)
    List<ClassDetail> findByIdClass(@Param("idClass") Long idClass);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM class_detail WHERE class_id  = :idClass AND user_id = :iduser", nativeQuery = true)
    int deleteByUserAndClass(@Param("idClass") Long idClass, @Param("iduser") Long iduser);

} 