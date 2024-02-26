package com.example.batch_spring.configuration;

import com.example.batch_spring.entity.Person;
import org.springframework.batch.item.ItemProcessor;

// I - Input, O - Output
public class PersonProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(Person person) throws Exception {
        // Logic to process - we are not doing any transformations in the person object.
        return person;
    }
}
