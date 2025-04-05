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

    // Альтернативный метод для получения id эпика
    public int getEpicID() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    // Сериализация: id, тип, имя, статус, описание, startTime, duration, epicId
    @Override
    public String toString() {
        String start = (startTime != null) ? startTime.toString() : "null";
        String dur = (duration != null) ? String.valueOf(duration.toMinutes()) : "null";
        return id + "," + getType() + "," + name + "," + status + "," + description + "," + start + "," + dur + "," + epicId;
    }
}
