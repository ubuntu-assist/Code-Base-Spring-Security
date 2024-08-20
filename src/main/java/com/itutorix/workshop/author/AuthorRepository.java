package com.itutorix.workshop.author;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    List<Author> findByNamedQuery(@Param("age") int age);

    @Modifying
    @Transactional
    @Query("update Author a set a.age = :age where a.id = :id")
    void updateAuthor(int age, int id);

    @Modifying
    @Transactional
    @Query("update Author a set a.age = :age")
    void updateAllAuthorsAges(int age);

    List<Author> findAllByFirstName(String firstName);
    List<Author> findAllByFirstNameIgnoreCase(String firstName);
    List<Author> findAllByFirstNameContainingIgnoreCase(String firstName);
    List<Author> findAllByFirstNameStartingWithIgnoreCase(String pattern);
    List<Author> findAllByFirstNameEndingWithIgnoreCase(String pattern);
    List<Author> findAllByFirstNameInIgnoreCase(List<String> firstNames);
}
