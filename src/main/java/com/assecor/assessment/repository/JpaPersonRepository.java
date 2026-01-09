package com.assecor.assessment.repository;

import com.assecor.assessment.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("jpaPersonRepository")
public interface JpaPersonRepository extends PersonRepository, JpaRepository<Person, Long> {

    @Query("SELECT p FROM Person p WHERE LOWER(p.color) = LOWER(?1)")
    List<Person> findByColor(String color);
}
