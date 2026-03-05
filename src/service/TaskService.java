package src.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import src.app.model.Priority;
import src.model.Status;
import src.model.Task;
import src.storage.TaskStorage;

public class TaskService {
    private final TaskStorage storage;
    private final List<Task> tasks;

    public TaskService(TaskStorage storage) {
        this.storage = storage;
        this.tasks = new ArrayList<>(storage.load());
    }

    public Task add(String title, Priority priority) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Titulo nao pode ser vazio.");
        Task task = new Task(title.trim(), priority);
        tasks.add(task);
        persist();
        return task;
    }

    public List<Task> listAll() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Task> listByStatus(Status status) {
        return listAll().stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public boolean markDone(String idPrefix) {
        Task t = findByPrefix(idPrefix);
        if (t == null) return false;
        t.setStatus(Status.DONE);
        persist();
        return true;
    }

    public boolean remove(String idPrefix) {
        Task t = findByPrefix(idPrefix);
        if (t == null) return false;
        tasks.remove(t);
        persist();
        return true;
    }

    public boolean edit(String idPrefix, String newTitle, Priority newPriority) {
        Task t = findByPrefix(idPrefix);
        if (t == null) return false;
        if (newTitle != null && !newTitle.isBlank()) t.setTitle(newTitle.trim());
        if (newPriority != null) t.setPriority(newPriority);
        persist();
        return true;
    }

    private Task findByPrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) return null;
        String p = prefix.trim().toLowerCase();
        List<Task> matches = tasks.stream()
                .filter(t -> t.getId().toLowerCase().startsWith(p))
                .collect(Collectors.toList());
        if (matches.size() == 1) return matches.get(0);
        return null;
    }

    private void persist() {
        storage.save(tasks);
    }
}
