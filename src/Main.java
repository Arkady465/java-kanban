import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import managers.Manager;
import managers.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Manager.getDefault();

        //Загружаем содержимое файла
        System.out.println();
        System.out.println("Содержимое списка истории из файла сохранения:");
        taskManager.getHistory().forEach(System.out::println);

        // Создаем обычные задачи
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        System.out.println();
        System.out.println("Список задач:");
        taskManager.getAllTasks().forEach(task -> {
            System.out.println(taskManager.getTaskById(task.getId()).getName() + ": " + taskManager.getTaskById(task.getId()).getDescription());
        });

        System.out.println();
        System.out.println("История вызовов после создания обычных задач:");
        taskManager.getHistory().forEach(System.out::println);

        //Вывод списка приоритета задач
        System.out.println();
        System.out.println("Список приоритета задач:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

        //Обновляем обычную задача
        long durationTask = 10;
        Task task3 = new Task("Новая Задача 2", "Новое описание задачи 2", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(durationTask));
        taskManager.updateTask(task2, task3);
        System.out.println("\nОбновленный список задач:");
        taskManager.getAllTasks().forEach(task -> {
            System.out.println(
                    taskManager.getTaskById(task.getId()).getName() + ": " +
                            taskManager.getTaskById(task.getId()).getDescription() + ". " +
                            taskManager.getTaskById(task.getId()).getStatus() + ". " +
                            taskManager.getTaskById(task.getId()).getStartTime() + ". " +
                            taskManager.getTaskById(task.getId()).getDuration()
            );
        });

        System.out.println();
        System.out.println("История вызовов после создания обычных задач:");
        taskManager.getHistory().forEach(System.out::println);

        //Вывод списка приоритета задач
        System.out.println();
        System.out.println("Список приоритета задач:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

        //Создаем эпики
        Epic epic1 = new Epic("Эпик 1", "Пустой 1");
        Epic epic2 = new Epic("Эпик 2", "Пустой 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        System.out.println("\nСписок эпиков:");
        taskManager.getAllEpics().forEach(epic -> {
            System.out.println(taskManager.getEpicById(epic.getId()).getName()  + ": " + taskManager.getEpicById(epic.getId()).getDescription());
        });

        System.out.println();
        System.out.println("История вызовов после создания обычных задач:");
        taskManager.getHistory().forEach(System.out::println);

        //Вывод списка приоритета задач
        System.out.println();
        System.out.println("Список приоритета задач:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

        //Обновляем статус эпика вручную
        Epic epic3 = new Epic("Эпик 1", "С подзадачами 1", Status.IN_PROGRESS, epic1.getStartTime(), epic1.getDuration());
        Epic epic4 = new Epic("Эпик 2", "С подзадачами 2", Status.IN_PROGRESS, epic2.getStartTime(), epic2.getDuration());
        taskManager.updateEpic(epic1, epic3);
        taskManager.updateEpic(epic2, epic4);
        System.out.println("\nОбновленный статус эпиков:");
        taskManager.getAllEpics().forEach(epic -> {
            System.out.println(
                    taskManager.getEpicById(epic.getId()).getName()  + ": " +
                            taskManager.getEpicById(epic.getId()).getDescription() + ". " +
                            taskManager.getEpicById(epic.getId()).getStatus()
            );
        });

        System.out.println();
        System.out.println("История вызовов после создания обычных задач:");
        taskManager.getHistory().forEach(System.out::println);

        //Вывод списка приоритета задач
        System.out.println();
        System.out.println("Список приоритета задач:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

        //Создаем подзадачи
        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание 1.1", epic3.getId());
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание 1.2", epic3.getId());
        Subtask subtask3 = new Subtask("Подзадача 2.1", "Описание 2.1", epic4.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        System.out.println("\nСписок подзадач:");
        taskManager.getAllSubTasks().forEach(subtask -> {
            System.out.println(taskManager.getSubtaskById(subtask.getId()).getName() + ": " + taskManager.getSubtaskById(subtask.getId()).getDescription());
        });

        System.out.println();
        System.out.println("История вызовов после создания обычных задач:");
        taskManager.getHistory().forEach(System.out::println);

        //Вывод списка приоритета задач
        System.out.println();
        System.out.println("Список приоритета задач:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

        //Обновление статуса эпиков с началом выполнения подзадач
        Subtask subtask12 = new Subtask("Новая Подзадача 1.1", "Новое Описание 1.1", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10), epic3.getId());
        Subtask subtask22 = new Subtask("Новая Подзадача 1.2", "Новое Описание 1.2", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10), epic3.getId());
        Subtask subtask32 = new Subtask("Новая Подзадача 2.1", "Новое Описание 2.1", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(80), Duration.ofMinutes(10), epic4.getId());
        taskManager.updateSubTask(subtask1, subtask12);
        taskManager.updateSubTask(subtask2, subtask22);
        taskManager.updateSubTask(subtask3, subtask32);
        System.out.println("\nОбновленный список подзадач:");
        taskManager.getAllSubTasks().forEach(subtask -> {
            System.out.println(
                    taskManager.getSubtaskById(subtask.getId()).getName() + ": " +
                            taskManager.getSubtaskById(subtask.getId()).getDescription() + ". " +
                            taskManager.getSubtaskById(subtask.getId()).getStatus() + ". " +
                            taskManager.getSubtaskById(subtask.getId()).getStartTime() + ". " +
                            taskManager.getSubtaskById(subtask.getId()).getDuration()
            );
        });

        System.out.println();
        System.out.println("История вызовов после создания обычных задач:");
        taskManager.getHistory().forEach(System.out::println);

        //Вывод списка приоритета задач
        System.out.println();
        System.out.println("Список приоритета задач:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

        //Обновление статуса подзадач в ручную
        Subtask subtask13 = new Subtask("Новая Подзадача 1.1", "Новое Описание 1.1", Status.DONE, subtask12.getStartTime(), subtask12.getDuration(), epic3.getId());
        Subtask subtask23 = new Subtask("Новая Подзадача 1.2", "Новое Описание 1.2", Status.IN_PROGRESS, subtask22.getStartTime(), subtask22.getDuration(),epic3.getId());
        Subtask subtask33 = new Subtask("Новая Подзадача 2.1", "Новое Описание 2.1", Status.DONE, subtask32.getStartTime(), subtask32.getDuration(), epic4.getId());
        taskManager.updateSubTask(subtask12, subtask13);
        taskManager.updateSubTask(subtask22, subtask23);
        taskManager.updateSubTask(subtask32, subtask33);
        System.out.println("\nОбновленный список подзадач:");
        taskManager.getAllSubTasks().forEach(subtask -> {
            System.out.println(taskManager.getSubtaskById(subtask.getId()).getName() + ": " + taskManager.getSubtaskById(subtask.getId()).getDescription());
        });

        System.out.println();
        System.out.println("История вызовов после создания обычных задач:");
        taskManager.getHistory().forEach(System.out::println);

        //Вывод списка приоритета задач
        System.out.println();
        System.out.println("Список приоритета задач:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

        //Вывод обновленных статусов
        System.out.println();
        System.out.println("Обновленный статус эпиков:");
        System.out.println("Эпик 1 статус: " + taskManager.getEpicById(epic3.getId()).getStatus());
        System.out.println("Эпик 2 статус: " + taskManager.getEpicById(epic4.getId()).getStatus());

        System.out.println();
        System.out.println("История вызовов после создания обычных задач:");
        taskManager.getHistory().forEach(System.out::println);

        //Удаление
        taskManager.deleteTaskById(task3);

        System.out.println();
        System.out.println("\nОбновленный список задач после удаления задачи:");
        taskManager.getAllTasks().forEach(task -> {
            System.out.println(taskManager.getTaskById(task.getId()).getName() + ": " + taskManager.getTaskById(task.getId()).getDescription());
        });

        System.out.println();
        System.out.println("История вызовов после создания обычных задач:");
        taskManager.getHistory().forEach(System.out::println);

        //Вывод списка приоритета задач
        System.out.println();
        System.out.println("Список приоритета задач:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

        taskManager.deleteEpicById(epic3);

        System.out.println("\nОбновленный список эпиков после удаления эпика:");
        taskManager.getAllEpics().forEach(epic -> {
            System.out.println(taskManager.getEpicById(epic.getId()).getName()  + ": " + taskManager.getEpicById(epic.getId()).getDescription());
        });

        System.out.println();
        System.out.println("История вызовов после создания обычных задач:");
        taskManager.getHistory().forEach(System.out::println);

        //Вывод списка приоритета задач
        System.out.println();
        System.out.println("Список приоритета задач:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);

        taskManager.deleteSubtaskById(subtask33);

        System.out.println("\nОбновленный список подзадач после удаления подзадач:");
        taskManager.getAllSubTasks().forEach(subtask -> {
            System.out.println(taskManager.getSubtaskById(subtask.getId()).getName() + ": " + taskManager.getSubtaskById(subtask.getId()).getDescription());
        });

        System.out.println();
        System.out.println("История вызовов после создания обычных задач:");
        taskManager.getHistory().forEach(System.out::println);

        //Вывод списка приоритета задач
        System.out.println();
        System.out.println("Список приоритета задач:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);
    }
}
