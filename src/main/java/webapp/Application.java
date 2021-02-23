package webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@SpringBootApplication
@ComponentScan({"config"})
public class Application {

    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(Application.class, args);

        // For debugging purposes
        /*
        String[] beans = applicationContext.getBeanDefinitionNames();
        Arrays.sort(beans);
        for (String s : beans) {
            System.out.println(s);
        }*/
    }

}
