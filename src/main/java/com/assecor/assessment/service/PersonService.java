package com.assecor.assessment.service;

import com.assecor.assessment.model.Person;
import com.assecor.assessment.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository csvRepository;
    private final PersonRepository jpaRepository;
    private PersonRepository activeRepository;

    @Value("${data.source:csv}")
    private String dataSource;

    public PersonService(@Qualifier("csvPersonRepository") PersonRepository csvRepository,
                         @Qualifier("jpaPersonRepository") PersonRepository jpaRepository) {
        this.csvRepository = csvRepository;
        this.jpaRepository = jpaRepository;
    }

    @PostConstruct
    public void init() {
        if ("db".equalsIgnoreCase(dataSource)) {
            activeRepository = jpaRepository;
            // Import CSV to DB on startup (Bonus: Second source)
            List<Person> persons = csvRepository.findAll();
            jpaRepository.saveAll(persons);
        } else {
            activeRepository = csvRepository;
        }
    }

    public List<Person> findAll() {
        return activeRepository.findAll();
    }

    public Optional<Person> findById(Long id) {
        return activeRepository.findById(id);
    }

    public List<Person> findByColor(String color) {
        return activeRepository.findByColor(color);
    }

    public Person save(Person person) {
        return activeRepository.save(person);
    }
}
