package api;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import taskManager.api.HttpTaskServer;
import taskManager.service.InMemoryTaskManager;
import taskManager.service.TaskManager;

import java.io.IOException;
import java.net.http.HttpClient;

public class HttpTaskManagerTest {
    protected TaskManager manager;
    protected HttpTaskServer server;
    protected HttpClient client;
    protected final Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }
}