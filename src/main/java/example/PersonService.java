package example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    EntityManager entityManager;

    @Transactional
    public Person insert(String name) {
        Person person = new Person(name);
        person.addNickNames(List.of(name));
        return personRepository.save(person);
    }

    @Transactional
    public Person getById(int id) {
        return personRepository.getOne(id);
    }
}
