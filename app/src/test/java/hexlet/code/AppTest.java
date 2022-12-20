package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AppTest {

    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

    private static Javalin app;
    private static String baseUrl;
    private static Url existingUrl;
    private static Database database;

    private final int status200 = 200;
    private final int status500 = 500;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start();
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();
    }

    @AfterAll
    public static void afterAll() {
        database.script().run("/truncate.sql");
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
        database.script().run("/truncate.sql");
        existingUrl = new Url("https://vk.com");
        existingUrl.save();
    }

    @Test
    void testUrls() {
        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls")
                .asString();
        String content = response.getBody();

        assertThat(response.getStatus()).isEqualTo(status200);
        assertThat(response.getBody()).contains("https://vk.com");
    }

    @Test
    void testAddUrl() {
        HttpResponse<String> responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("name", "https://vk.com")
                .asString();

        assertThat(responsePost.getStatus()).isEqualTo(status500);

        Url actualUrl = new QUrl()
                .name.equalTo("https://vk.com")
                .findOne();
        assertThat(actualUrl).isNotNull();
        assertThat(actualUrl.getName()).isEqualTo("https://vk.com");
    }

    @Test
    void testAddBadUrl() {
        HttpResponse<String> responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("name", "httpppps://vk.com")
                .asString();

        Url actualUrl = new QUrl()
                .name.equalTo("httpppps://vk.com")
                .findOne();
        assertThat(actualUrl).isNull();
    }

}
