package py.com.solofutbol.elobot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class EloBotApp implements CommandLineRunner {


    public static void main(String[] args) {
        //Add this line to initialize bots context
        ApiContextInitializer.init();
        SpringApplication.run(EloBotApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
