package managers;

import java.io.File;

public class Manager {
    public static TaskManager getDefault() {
        File directory = new File("./data");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, "tasks.csv");
        return FileBackedTaskManager.loadFromFile(getDefaultHistory(), file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
