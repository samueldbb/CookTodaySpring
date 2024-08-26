package org.udg.pds.springtodo;

import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.entity.Categoria;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Tag;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.service.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class Global {
    public static final DateTimeFormatter AppDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH:mm:ss z");

    @Getter
    private MinioClient minioClient;

    private final Logger logger = LoggerFactory.getLogger(Global.class);

    @Autowired
    private
    UserService userService;

    @Autowired
    private
    ReceptaService receptaService;

    @Autowired
    private
    CategoriaService categoriaService;

    @Autowired
    private
    TaskService taskService;

    @Autowired
    private
    TagService tagService;

    @Autowired
    private Environment environment;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${todospring.minio.url:}")
    private String minioURL;

    @Value("${todospring.minio.access-key:}")
    private String minioAccessKey;

    @Value("${todospring.minio.secret-key:}")
    private String minioSecretKey;

    @Getter
    @Value("${todospring.minio.bucket:}")
    private String minioBucket;

    @Value("${todospring.base-url:#{null}}")
    private String BASE_URL;

    @Value("${todospring.base-port:8080}")
    private String BASE_PORT;


    @PostConstruct
    void init() {

        logger.info(String.format("Starting Minio connection to URL: %s", minioURL));
        try {
            minioClient = MinioClient.builder()
                                     .endpoint(minioURL)
                                     .credentials(minioAccessKey, minioSecretKey)
                                     .build();
        } catch (Exception e) {
            logger.warn("Cannot initialize minio service with url:" + minioURL + ", access-key:" + minioAccessKey + ", secret-key:" + minioSecretKey);
        }

        if (minioBucket.equals("")) {
            logger.warn("Cannot initialize minio bucket: " + minioBucket);
            minioClient = null;
        }

        if (BASE_URL == null) BASE_URL = "http://localhost";
        BASE_URL += ":" + BASE_PORT;

        initData();
    }

    private void initData() {

        if (activeProfile.equals("dev")) {
            logger.info("Starting populating database ...");

            User user = userService.register("usuari", "usuari@hotmail.com", "123456",
                "Soc l'usuari principal, m'agrada cuinar receptes de tot tipus: pizzes, pasta, menjar oriental, etc. Soc un expert");
            userService.register("user", "user@hotmail.com", "0000", "Especialitats: Postres i gelats");
            User user1 = userService.register("Sam", "sam@hotmail.com", "123456" ,
                "Expert en cuina tradicional espanyola. La paella es la meva especialitat");
            User user2 = userService.register("Arnau", "arnau@hotmail.com", "123456",
                "Acabo de començar en aixo de la cuina");
            User user3 = userService.register("Vane", "vane@hotmail.com", "123456",
                "Experta en cuinars vegetarians, faig els millors");

            Categoria catVeg= categoriaService.addCategoria("Veggie");
            Categoria catCarn= categoriaService.addCategoria("Carn");
            Categoria catOrient= categoriaService.addCategoria("Oriental");
            Categoria catItalia= categoriaService.addCategoria("Italia");


            Collection<String> cats = new ArrayList<String>();
            cats.add("Carn");

            receptaService.addRecepta("Pollastre", user1.getId(), "arrebossat, bonissim", cats);
            receptaService.addRecepta("Pollastre", user1.getId(), "a la brasa, amb patates", cats);

            cats.clear();
            cats.add("Italia");
            receptaService.addRecepta("Pizza", user2.getId(), "quatre formatges", cats);
            cats.add("Veggie");
            receptaService.addRecepta("Espaguetis", user3.getId(), "a la carbonara", cats);

            cats.clear();
            cats.add("Oriental");
            receptaService.addRecepta("Gyozas", user2.getId(), "amb carn i verdures", cats);

            cats.clear();
            cats.add("Veggie");
            receptaService.addRecepta("Alberginies", user3.getId(), "farcides de bexamel", cats);




        }

    }

    public String getBaseURL() {
        return BASE_URL;
    }
}
