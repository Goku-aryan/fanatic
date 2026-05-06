package com.fanatic.repository;

import com.fanatic.entity.Content;
import com.fanatic.entity.ContentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<Content, UUID> {

    // ========== FIND BY TYPE ==========
    Page<Content> findByContentType(ContentType contentType, Pageable pageable);

    Page<Content> findByContentTypeAndIsEarlyAccess(
            ContentType contentType, Boolean isEarlyAccess, Pageable pageable);

    // ========== EARLY ACCESS ==========
    Page<Content> findByIsEarlyAccess(Boolean isEarlyAccess, Pageable pageable);

    // ========== SEARCH ==========
    @Query("SELECT c FROM Content c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Content> searchContent(@Param("query") String query, Pageable pageable);

    @Query("SELECT c FROM Content c WHERE c.contentType = :type AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Content> searchContentByType(
            @Param("query") String query,
            @Param("type") ContentType type,
            Pageable pageable);

    // ========== SEARCH BY GENRE ==========
    @Query("SELECT c FROM Content c WHERE c.contentType = :type AND LOWER(c.genre) = LOWER(:genre)")
    Page<Content> findByContentTypeAndGenre(
            @Param("type") ContentType type,
            @Param("genre") String genre,
            Pageable pageable);

    // ========== TOP RATED ==========
    @Query("SELECT c FROM Content c ORDER BY c.avgRating DESC")
    Page<Content> findTopRated(Pageable pageable);

    @Query("SELECT c FROM Content c WHERE c.contentType = :type ORDER BY c.avgRating DESC")
    Page<Content> findTopRatedByType(@Param("type") ContentType type, Pageable pageable);

    // ========== MOST REVIEWED ==========
    @Query("SELECT c FROM Content c ORDER BY c.totalRatings DESC")
    Page<Content> findMostReviewed(Pageable pageable);

    @Query("SELECT c FROM Content c WHERE c.contentType = :type ORDER BY c.totalRatings DESC")
    Page<Content> findMostReviewedByType(@Param("type") ContentType type, Pageable pageable);

    // ========== RECENTLY ADDED ==========
    @Query("SELECT c FROM Content c ORDER BY c.createdAt DESC")
    Page<Content> findRecentlyAdded(Pageable pageable);

    @Query("SELECT c FROM Content c WHERE c.contentType = :type ORDER BY c.createdAt DESC")
    Page<Content> findRecentlyAddedByType(@Param("type") ContentType type, Pageable pageable);

    // ========== COUNT ==========
    long countByContentType(ContentType contentType);

    long countByIsEarlyAccess(boolean isEarlyAccess);

    // ========== DISTRIBUTION FOR ADMIN STATS ==========
    @Query("SELECT c.contentType, COUNT(c) FROM Content c GROUP BY c.contentType")
    List<Object[]> getContentDistribution();

    // ========== CHECK EXISTS ==========
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Content c " +
           "WHERE LOWER(c.title) = LOWER(:title) AND c.contentType = :type")
    boolean existsByTitleAndType(@Param("title") String title, @Param("type") ContentType type);
}