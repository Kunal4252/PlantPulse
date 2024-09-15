package com.kunal.gardengenius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kunal.gardengenius.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByTitleContainingIgnoreCase(String title);

	@Query("SELECT p FROM Post p ORDER BY p.createdDate DESC")
	List<Post> findAllOrderedByDateDesc();

	@Query("SELECT p FROM Post p WHERE p.user.id = :userId ORDER BY p.createdDate DESC")
	List<Post> findByUserIdOrderedByDateDesc(@Param("userId") Long userId);

	@Query(value = "SELECT p.* FROM posts p " + "LEFT JOIN ( " + "    SELECT post_id, COUNT(*) as like_count "
			+ "    FROM post_likes " + "    GROUP BY post_id " + ") pl ON p.id = pl.post_id "
			+ "WHERE REPLACE(LOWER(p.title), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:query, ' ', ''), '%')) "
			+ "ORDER BY " + "    CASE "
			+ "        WHEN REPLACE(LOWER(p.title), ' ', '') = LOWER(REPLACE(:query, ' ', '')) THEN 1 "
			+ "        WHEN REPLACE(LOWER(p.title), ' ', '') LIKE LOWER(CONCAT(REPLACE(:query, ' ', ''), '%')) THEN 2 "
			+ "        WHEN REPLACE(LOWER(p.title), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:query, ' ', ''), '%')) THEN 3 "
			+ "        ELSE 4 " + "    END, " + "    COALESCE(pl.like_count, 0) DESC, "
			+ "    p.created_date DESC", nativeQuery = true)
	List<Post> searchByTitleAndOrderByLikes(@Param("query") String query);

	@Query(value = "SELECT * FROM posts p WHERE REPLACE(LOWER(p.title), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:query, ' ', ''), '%'))", nativeQuery = true)
	List<Post> searchByTitleIgnoringSpaces(@Param("query") String title);
}
