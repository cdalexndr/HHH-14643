package example;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
public class HHHTest extends AbstractTestNGSpringContextTests {
    private static final Logger log = LoggerFactory.getLogger(HHHTest.class);
    @Autowired
    PersonService personService;
    @Autowired
    EntityManager entityManager;


    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void test() throws InterruptedException {
        Person person = personService.insert("person"); //insert in a new transaction
        person = personService.getById(person.getId()); //get lazy rel owner
        AtomicBoolean error = new AtomicBoolean(false);
        Person finalPerson = person;
        List<Thread> threads = IntStream.range(0, 10)
                .mapToObj(i -> new Thread(() -> {
                    try {
                        finalPerson.getNicknames().toString();
                    } catch (Exception ex) {
                        log.error("Test error", ex);
                        error.set(true);
                    }
                }))
                .collect(Collectors.toList());
        threads.forEach(t -> t.start());
        for (Thread thread : threads) {
            thread.join();
        }
        assertFalse(error.get());
    }
}
