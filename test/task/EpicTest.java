package task;

import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.TaskManagerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void initTest() {
        super.manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void testEqualsId(){
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        Epic epic2 = new Epic("Задача 3", "Описание задачи 3");
        epic.setId(1);
        epic2.setId(1);

        assertEquals(epic, epic2);
    }

    @Test
    void testNotEqualsId(){
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        Epic epic2 = new Epic("Задача 3", "Описание задачи 3");
        epic.setId(1);
        epic2.setId(2);

        assertNotEquals(epic, epic2);
    }

    @Test
    void epicStatusNew() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);

        manager.addSubtask(new Subtask("Задача 3", "Описание задачи 3", Status.NEW, epic.getId()));
        manager.addSubtask(new Subtask("Задача 4", "Описание задачи 4", Status.NEW, epic.getId()));

        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatusDone() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);

        manager.addSubtask(new Subtask("Задача 3", "Описание задачи 3", Status.DONE, epic.getId()));
        manager.addSubtask(new Subtask("Задача 4", "Описание задачи 4", Status.DONE, epic.getId()));

        assertEquals(Status.DONE,
                manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatusProgress() {
        Epic epic = new Epic("Epic", "desc");
        manager.addEpic(epic);

        manager.addSubtask(new Subtask("S1", "d", Status.IN_PROGRESS, epic.getId()));
        manager.addSubtask(new Subtask("S2", "d", Status.IN_PROGRESS, epic.getId()));

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatusAddProgress() {
        Epic epic = new Epic("Epic", "desc");
        manager.addEpic(epic);

        manager.addSubtask(new Subtask("S1", "d", Status.NEW,  epic.getId()));
        manager.addSubtask(new Subtask("S2", "d", Status.DONE, epic.getId()));

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }


    @Test
    void getSubTaskListForEpic() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        List<Subtask> subtaskList = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            Subtask subtask = new Subtask("Задача " + i, "Описание " + i, epic.id);
            subtaskList.add(subtask);
        }
        for (int i = 0; i < 3; i++) {
            manager.addSubtask(subtaskList.get(i));
        }

        assertNotNull(epic.getSubTaskListForEpic());
    }

    @Test
    void setSubTaskListOfEpic() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        List<Subtask> subtaskList = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            Subtask subtask = new Subtask("Задача " + i, "Описание " + i, epic.id);
            subtaskList.add(subtask);
        }
        epic.setSubTaskListOfEpic(subtaskList);

        assertNotNull(epic.getSubTaskListForEpic());
    }

    @Test
    void saveSubTaskOfEpic() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);

        List<Subtask> listEmpty = manager.getAllSubTasks();

        List<Subtask> listAddSubtask = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            Subtask subtask = new Subtask("Задача " + i, "Описание " + i, epic.id);
            listAddSubtask.add(subtask);
        }
        for (int i = 0; i < 3; i++) {
            manager.addSubtask(listAddSubtask.get(i));
        }

        assertNotEquals(listEmpty, epic.getSubTaskListForEpic());
    }

    @Test
    void removeSubTaskOfEpic() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", epic.getId());
        manager.addSubtask(subTask);
        Subtask subTask2 = new Subtask("Задача 4", "Описание задачи 4", epic.getId());
        manager.addSubtask(subTask2);
        Subtask subTask3 = new Subtask("Задача 5", "Описание задачи 5", epic.getId());
        manager.addSubtask(subTask3);

        List<Subtask> subtaskList = manager.getSubTasksForEpic(epic.id);
        epic.removeSubTaskOfEpic(subTask2);
        List<Subtask> subtaskList2 = manager.getSubTasksForEpic(epic.id);

        assertNotEquals(subtaskList.size(), subtaskList2.size());

    }

    @Test
    void getEndTime() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(5), epic.id);
        manager.addSubtask(subTask);
        Subtask subTask2 = new Subtask("Задача 4", "Описание задачи 4", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(5), epic.id);
        manager.addSubtask(subTask2);
        Subtask subTask3 = new Subtask("Задача 5", "Описание задачи 5", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5), epic.id);
        manager.addSubtask(subTask3);

        assertNotNull(epic.getEndTime());
    }

    @Test
    void calculationTimeFields() {
        Epic epic = new Epic("Задача 2", "Описание задачи 2");
        manager.addEpic(epic);
        LocalDateTime startTimeOrigin = epic.getStartTime();
        LocalDateTime endTimeOrigin = epic.getEndTime();

        Subtask subTask = new Subtask("Задача 3", "Описание задачи 3", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(5), epic.id);
        manager.addSubtask(subTask);
        Subtask subTask2 = new Subtask("Задача 4", "Описание задачи 4", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(5), epic.id);
        manager.addSubtask(subTask2);
        Subtask subTask3 = new Subtask("Задача 5", "Описание задачи 5", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5), epic.id);
        manager.addSubtask(subTask3);

        LocalDateTime startTimeNew = epic.getStartTime();
        LocalDateTime endTimeNew = epic.getEndTime();

        assertNull(startTimeOrigin);
        assertNull(endTimeOrigin);
        assertNotEquals(null, startTimeNew);
        assertNotEquals(null, endTimeNew);
    }
}