package webapp;

import context.logic.EventGenerationException;
import context.logic.EventGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
@ComponentScan({"config"})
public class Application {

    private static ApplicationContext applicationContext;

    @Autowired
    private EventGenerator events;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(Application.class, args);
    }

    /**
     * Execute scheduled jobs every 5 seconds
     * @throws EventGenerationException if something goes wrong generating events
     */
    @Scheduled(fixedRate = 5000)
    private void scheduledJobs() throws EventGenerationException {
        events.generateEvents();
    }

}
