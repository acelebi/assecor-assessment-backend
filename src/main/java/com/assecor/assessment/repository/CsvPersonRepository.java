package com.assecor.assessment.repository;

import com.assecor.assessment.model.Person;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Repository("csvPersonRepository")
public class CsvPersonRepository implements PersonRepository {

    @Value("${csv.file.path}")
    private String csvFilePath;

    private final Map<Integer, String> colorMap = Map.of(
            1, "blau", 2, "grün", 3, "violett", 4, "rot",
            5, "gelb", 6, "türkis", 7, "weiß"
    );

    @Override
    public List<Person> findAll() {
        return readCsv();
    }

    @Override
    public Optional<Person> findById(Long id) {
        List<Person> persons = readCsv();
        return id <= persons.size() ? Optional.of(persons.get(id.intValue() - 1)) : Optional.empty();
    }

    @Override
    public List<Person> findByColor(String color) {
        return readCsv().stream()
                .filter(p -> p.getColor().equalsIgnoreCase(color))
                .toList();
    }

    @Override
    public Person save(Person person) {
        List<Person> persons = readCsv();
        long newId = persons.size() + 1;
        person.setId(newId);
        persons.add(person);
        writeCsv(persons);
        return person;
    }

    private List<Person> readCsv() {
        List<Person> persons = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] line;
            long id = 1;
            while ((line = reader.readNext()) != null) {
                if (line.length < 4) continue;
                String lastname = line[0].trim();
                String name = line[1].trim();
                String[] zipCity = line[2].trim().split(" ", 2);
                String zipcode = zipCity[0];
                String city = zipCity.length > 1 ? zipCity[1] : "";
                int colorId = Integer.parseInt(line[3].trim());
                String color = colorMap.getOrDefault(colorId, "unknown");

                Person person = new Person(id++, name, lastname, zipcode, city, color);
                persons.add(person);
            }
        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException("Error reading CSV", e);
        }
        return persons;
    }

    private void writeCsv(List<Person> persons) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            for (Person p : persons) {
                // Inverse mapping for colorId
                Optional<Integer> colorId = colorMap.entrySet().stream()
                        .filter(e -> e.getValue().equals(p.getColor()))
                        .map(Map.Entry::getKey)
                        .findFirst();
                if (colorId.isEmpty()) throw new IllegalArgumentException("Invalid color");

                String[] line = {
                        p.getLastname(),
                        p.getName(),
                        p.getZipcode() + " " + p.getCity(),
                        colorId.get().toString()
                };
                writer.writeNext(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV", e);
        }
    }
}
