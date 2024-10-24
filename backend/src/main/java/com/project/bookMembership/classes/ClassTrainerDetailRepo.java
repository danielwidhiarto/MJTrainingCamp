package com.project.bookMembership.classes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassTrainerDetailRepo  extends JpaRepository<ClassTrainerDetail,Long>{

    @Query("SELECT c FROM ClassTrainerDetail c WHERE c.idClass.id = :idClass")
    Optional<ClassTrainerDetail> findByIdClass(Long idClass);

}