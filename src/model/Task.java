package src.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import src.app.model.Priority;

public class Task {
    private final String id;
    private String title;
    private Priority priority;
    private Status status;
    private final LocalDateTime createdAt;

    public Task(String title, Priority priority) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.priority = priority;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Task(String id, String title, Priority priority, Status status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setTitle(String title) { this.title = title; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setStatus(Status status) { this.status = status; }

    public String toCsv() {
        // id;title;priority;status;createdAt
        return String.join(";",
                id,
                escape(title),
                priority.name(),
                status.name(),
                createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    public static Task fromCsv(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length < 5) throw new IllegalArgumentException("Linha CSV inválida: " + line);

        String id = parts[0];
        String title = unescape(parts[1]);
        Priority priority = Priority.valueOf(parts[2]);
        Status status = Status.valueOf(parts[3]);
        LocalDateTime createdAt = LocalDateTime.parse(parts[4]);

        return new Task(id, title, priority, status, createdAt);
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\n", "\\n").replace(";", "\\;");
    }

    private static String unescape(String text) {
        return text.replace("\\;", ";").replace("\\n", "\n").replace("\\\\", "\\");
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | %s",
                id.substring(0, 8),
                title,
                priority,
                status,
                createdAt.toLocalDate()
        );
    }
}