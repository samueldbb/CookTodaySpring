package org.udg.pds.springtodo;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.udg.pds.springtodo.configuration.exceptions.ControllerException;
import org.udg.pds.springtodo.entity.Categoria;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Tag;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.service.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

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

    @SneakyThrows
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

            Categoria catOrient= categoriaService.addCategoria("Oriental");
            Categoria catVeg= categoriaService.addCategoria("Veggie");
            Categoria catPeix= categoriaService.addCategoria("Peix");
            Categoria catMediterrani= categoriaService.addCategoria("Mediterrani");
            Categoria catCarn= categoriaService.addCategoria("Carn");
            Categoria catItalia= categoriaService.addCategoria("Italia");
            Categoria catAltres= categoriaService.addCategoria("Altres");

            Collection<String> cats = new ArrayList<String>();
            cats.add("Carn");

            File file = new File("src/main/resources/images/bistec.jpg");
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
            String path = upload(multipartFile);
            receptaService.addRecepta("Bistec amb patates", user1.getId(), "espectacular", cats, path);

            file = new File("src/main/resources/images/pollobrasa.jpg");
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
            path = upload(multipartFile);
            receptaService.addRecepta("Pollastre", user1.getId(), "a la brasa, amb patates", cats, path);

            cats.clear();
            cats.add("Italia");
            file = new File("src/main/resources/images/pizza.jpg");
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
            path = upload(multipartFile);
            receptaService.addRecepta("Pizza", user2.getId(), "quatre formatges", cats, path);

            cats.add("Veggie");
            file = new File("src/main/resources/images/carbonara.jpg");
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
            path = upload(multipartFile);
            receptaService.addRecepta("Espaguetis a la carbonara veggies", user3.getId(), "Una versió de espaguetis a la carbonara pero vegetarians", cats, path);

            cats.clear();
            cats.add("Oriental");
            file = new File("src/main/resources/images/gyozas.jpg");
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
            path = upload(multipartFile);
            receptaService.addRecepta("Gyozas", user2.getId(), "amb carn i verdures", cats, path);

            cats.clear();
            cats.add("Veggie");
            file = new File("src/main/resources/images/berenjenas.jpg");
            input = new FileInputStream(file);
            multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
            path = upload(multipartFile);
            receptaService.addRecepta("Alberginies", user3.getId(), "farcides de verdures amb bexamel", cats, path);




        }

    }

    public String getBaseURL() {
        return BASE_URL;
    }

    public String upload(@RequestParam("file") MultipartFile file) {

        MinioClient minioClient = getMinioClient();
        if (minioClient == null)
            throw new ControllerException("Minio client not configured");

        try {
            // Handle the body of that part with an InputStream
            InputStream istream = file.getInputStream();
            String contentType = file.getContentType();
            UUID imgName = UUID.randomUUID();

            String objectName = imgName + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            // Upload the file to the bucket with putObject
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(getMinioBucket())
                    .object(objectName)
                    .stream(istream, -1, 10485760)
                    .build());

            return String.format("%s", "http://localhost:8080/images/" + objectName);
        } catch (Exception e) {
            throw new ControllerException("Error saving file: " + e.getMessage());
        }
    }
}
