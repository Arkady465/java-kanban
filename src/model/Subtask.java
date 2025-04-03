package model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, Status status, String description, int epicId) {
        super(id, name, status, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    // Метод, используемый менеджером для получения id эпика
    public int getEpicID() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return id + "," + getType() + "," + name + "," + status + "," + description + "," + epicId;
    }
}