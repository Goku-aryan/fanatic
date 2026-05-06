package com.fanatic.repository;

import com.fanatic.entity.LevelThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LevelThresholdRepository extends JpaRepository<LevelThreshold, Integer> {

    // ========== FIND LEVEL FOR POINTS ==========
    @Query("SELECT lt FROM LevelThreshold lt WHERE lt.pointsRequired <= :points " +
           "ORDER BY lt.level DESC LIMIT 1")
    Optional<LevelThreshold> findLevelForPoints(@Param("points") int points);

    // ========== FIND NEXT LEVEL ==========
    @Query("SELECT lt FROM LevelThreshold lt WHERE lt.pointsRequired > :points " +
           "ORDER BY lt.level ASC LIMIT 1")
    Optional<LevelThreshold> findNextLevel(@Param("points") int points);

    // ========== FIND ALL ORDERED ==========
    List<LevelThreshold> findAllByOrderByLevelAsc();
}