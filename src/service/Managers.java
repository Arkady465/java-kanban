package service;

import java.io.File;

public class Managers {
    private static final String DEFAULT_FILE = "tasks.csv";

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File(DEFAULT_FILE));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
