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
import java.util.List;
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

            Categoria catOrient = categoriaService.addCategoria("Oriental");
            Categoria catVeg = categoriaService.addCategoria("Veggie");
            Categoria catPeix = categoriaService.addCategoria("Peix");
            Categoria catMediterrani = categoriaService.addCategoria("Mediterrani");
            Categoria catCarn = categoriaService.addCategoria("Carn");
            Categoria catItalia = categoriaService.addCategoria("Italia");
            Categoria catAltres = categoriaService.addCategoria("Altres");

            // Crear receptes utilitzant la funció createRecepta
            createRecepta(
                "Bistec amb patates",
                user.getId(),
                "Facil de preparar i exquisit",
                List.of("Carn"),  // Utilitzem List.of per crear una col·lecció immutable
                "src/main/resources/images/bistec.jpg",
                "1.Tallar les patates \n2.Posar al forn 15 minuts (150ºC) \n3.Cuinar el bistec",
                "1 bistec \n2 patates \nOli i sal"
            );

            createRecepta(
                "Paella",
                user1.getId(),
                "Recepta tradicional de paella valenciana",
                List.of("Carn", "Mediterrani"),
                "src/main/resources/images/paella2.jpg",
                "1. Sofregir el pollastre, conill i verdures a la paella.\n2. Afegir el tomàquet, safrà i pebre vermell.\n3. Incorporar l'arròs i remenar bé.\n4. Afegir el brou i cuinar a foc lent fins que l'arròs estigui fet.\n5. Deixar reposar uns minuts abans de servir.",
                "Arròs, pollastre, conill, marisc, safrà, oli d'oliva"
            );

            createRecepta(
                "Pollastre",
                user.getId(),
                "a la brasa, amb patates",
                List.of("Carn"),
                "src/main/resources/images/pollobrasa.jpg",
                "1.Rentar pollastre \n2.Cuinarlo",
                "pollastre, patates"
            );

            createRecepta(
                "Pizza",
                user.getId(),
                "quatre formatges",
                List.of("Italia"),
                "src/main/resources/images/pizza.jpg",
                "1. Tomaquet a la base \n2. Afegir formatge \n3. Posar al forn 210 graus durant 15 minuts",
                "Base per pizza, tomaquet, formatge"
            );

            createRecepta(
                "Espaguetis a la carbonara veggies",
                user3.getId(),
                "Una versió de espaguetis a la carbonara pero vegetarians",
                List.of("Italia", "Veggie"),
                "src/main/resources/images/carbonara.jpg",
                "1. Bullir espaguetis \n2. cuinar bolets \n3. Afegir la salsa",
                "espaguetis, bolets, nata"
            );

            createRecepta(
                "Gyozas",
                user2.getId(),
                "amb carn i verdures",
                List.of("Oriental"),
                "src/main/resources/images/gyozas.jpg",
                "1. omplir la pasta amb carn i verdures \n2. Cuinar amb oli a una paella",
                "pasta, carn, verdures, especies"
            );

            createRecepta(
                "Alberginies",
                user3.getId(),
                "farcides de verdures amb bexamel",
                List.of("Veggie"),
                "src/main/resources/images/berenjenas.jpg",
                "1.Tallar alberginies \n2. omplirles de verdures i bexamel \n3. Fornejar durant 20 minuts a 180 graus",
                "alberginies, llet, verdures"
            );

            createRecepta("Sushi", user1.getId(), "Sushi clàssic amb salmó", List.of("Oriental"), "src/main/resources/images/sushi_2.jpg",
                "1. Preparar l'arròs per sushi.\n2. Tallar el salmó en tires.\n3. Enrotllar l'arròs amb alga nori i salmó.", "Arròs, salmó, alga nori, vinagre");

            createRecepta("Arros 3 delicies", user2.getId(), "Original arròs 3 delicies amb truita, pastanaga i pèsols", List.of("Oriental"), "src/main/resources/images/arros_3.jpg",
                "1. Cuinar truita i pastanaga.\n2. Afegir arròs i brou.\n3. Cuinar fins que l'arròs estigui fet.", "Arròs, pastanaga, verdures, ou, brou");

            // Veggie
            createRecepta("Quiche de verdures", user.getId(), "Quiche saludable amb verdures", List.of("Veggie", "Mediterrani"), "src/main/resources/images/quiche.jpg",
                "1. Preparar la base de quiche.\n2. Omplir amb una barreja de verdures i ous.\n3. Fornejar a 180 graus durant 30 minuts.", "Massa de quiche, verdures, ous");

            createRecepta("Llenties veggies", user3.getId(), "Llenties amb verdures estofades", List.of("Veggie"), "src/main/resources/images/lentejas_.jpg",
                "1. Sofregir verdures.\n2. Afegir les llenties i brou.\n3. Cuinar a foc lent fins que les llenties estiguin tendres.", "Lentejas, verdures, brou");

            // Peix
            createRecepta("Salmó al forn", user1.getId(), "Salmó cuit al forn amb espècies", List.of("Peix", "Mediterrani"), "src/main/resources/images/salmon.jpg",
                "1. Condimentar el salmó.\n2. Fornejar a 180 graus durant 20 minuts.\n3. Servir amb llimona.", "Salmó, espècies, llimona");

            createRecepta("Bacallà a la llauna", user2.getId(), "Bacallà cuinat a la llauna", List.of("Peix"), "src/main/resources/images/bacalla.jpg",
                "1. Preparar el bacallà amb oli i espècies.\n2. Cuinar al forn a 200 graus durant 25 minuts.\n3. Servir calent. \n4. Guarnició al gust", "Bacallà, oli, espècies");

            // Mediterrani
            createRecepta("Hummus", user3.getId(), "Hummus clàssic de cigrons", List.of("Mediterrani"), "src/main/resources/images/hummus.jpg",
                "1. Triturar cigrons amb tahini.\n2. Afegir all, llimona i oli d'oliva.\n3. Servir amb pa de pita.", "Cigrons, tahini, all, llimona, oli d'oliva");

            createRecepta("Truita de patates", user1.getId(), "Clàssica truita amb patates", List.of("Mediterrani", "Veggie"), "src/main/resources/images/tortilla.jpg",
                "1. Pelar i tallar les patates en rodanxes fines.\n2. Fregir les patates amb oli fins que estiguin tendres.\n3. Escórrer oli i barrejar les patates amb ous batuts. \n4. Coure la mescla en una paella a foc mitjà, girant la tortilla.",
                "Patates, ous, oli d'oliva, sal");

            // Italia
            createRecepta("Pasta Carbonara", user2.getId(), "Pasta carbonara tradicional", List.of("Italia"), "src/main/resources/images/carbonara_o.jpg",
                "1. Cuinar la pasta.\n2. Preparar la salsa amb ou i formatge.\n3. Barrejar amb pasta i afegir bacon.", "Pasta, ou, formatge, bacon");

            createRecepta("Pizza Margherita", user2.getId(), "Pizza Margherita amb tomàquet i mozzarella", List.of("Italia"), "src/main/resources/images/pizza_marga.jpg",
                "1. Preparar la massa de pizza.\n2. Afegir salsa de tomàquet i mozzarella.\n3. Fornejar a 220 graus durant 15 minuts.", "Massa per pizza, salsa de tomàquet, mozzarella");

            // Altres
            createRecepta("Tarta de xocolata", user3.getId(), "Tarta de xocolata amb crema", List.of("Altres"), "src/main/resources/images/tarta.jpg",
                "1. Preparar la massa de la tarta.\n2. Cuir al forn i deixar refredar.\n3. Afegir crema de xocolata a sobre.", "Xocolata, farina, sucre, crema");

            createRecepta("Crepes", user1.getId(), "Crepes dolces amb fruites", List.of("Altres"), "src/main/resources/images/crepe.jpg",
                "1. Preparar la massa de crepes.\n2. Cuinar en una paella.\n3. Servir amb fruites i mel.", "Farina, ou, llet, fruites, mel");

        }
    }

    @SneakyThrows
    private void createRecepta(String nom, Long userId, String descripcio, Collection<String> categories, String imatgePath,
                               String instruccions, String ingredients) {
        Collection<String> cats = new ArrayList<>();
        cats.addAll(categories);

        File file = new File(imatgePath);
        InputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        String path = upload(multipartFile);

        receptaService.addRecepta(nom, userId, descripcio, cats, path, instruccions, ingredients);
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
