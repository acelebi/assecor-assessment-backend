package com.assecor.assessment.controller;

import com.assecor.assessment.model.Person;
import com.assecor.assessment.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public List<Person> getAllPersons() {
        return personService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        return personService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/color/{color}")
    public List<Person> getPersonsByColor(@PathVariable String color) {
        return personService.findByColor(color);
    }

    @PostMapping
    public Person addPerson(@RequestBody Person person) {
        return personService.save(person);
    }
}
