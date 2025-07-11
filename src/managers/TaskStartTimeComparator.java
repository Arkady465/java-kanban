package managers;

import task.Task;

import java.time.LocalDateTime;
import java.util.Comparator;

public class TaskStartTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if (o1.getId() == o2.getId()) {
            return 0;
        }

        LocalDateTime start1 = o1.getStartTime();
        LocalDateTime start2 = o2.getStartTime();

        if (start1 == null && start2 == null) {
            return Integer.compare(o1.getId(), o2.getId());
        }
        if (start1 == null) return 1;
        if (start2 == null) return -1;

        int result = start1.compareTo(start2);
        if (result != 0) return result;

        return Integer.compare(o1.getId(), o2.getId());
    }
}
