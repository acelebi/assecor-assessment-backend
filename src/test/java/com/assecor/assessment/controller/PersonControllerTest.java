package com.assecor.assessment.controller;

import com.assecor.assessment.model.Person;
import com.assecor.assessment.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllPersons() throws Exception {
        List<Person> persons = List.of(new Person(1L, "Hans", "Müller", "67742", "Lauterecken", "blau"));
        when(personService.findAll()).thenReturn(persons);

        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(persons)));
    }

    @Test
    void getPersonById() throws Exception {
        Person person = new Person(1L, "Hans", "Müller", "67742", "Lauterecken", "blau");
        when(personService.findById(1L)).thenReturn(Optional.of(person));

        mockMvc.perform(get("/persons/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(person)));
    }

    @Test
    void getPersonById_NotFound() throws Exception {
        when(personService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/persons/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPersonsByColor() throws Exception {
        List<Person> persons = List.of(new Person(1L, "Hans", "Müller", "67742", "Lauterecken", "blau"));
        when(personService.findByColor("blau")).thenReturn(persons);

        mockMvc.perform(get("/persons/color/blau"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(persons)));
    }

    @Test
    void addPerson() throws Exception {
        Person person = new Person(null, "New", "Person", "12345", "City", "rot");
        Person saved = new Person(11L, "New", "Person", "12345", "City", "rot");
        when(personService.save(any(Person.class))).thenReturn(saved);

        mockMvc.perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(saved)));
    }
}
