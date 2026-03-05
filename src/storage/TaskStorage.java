package src.storage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import src.model.Task;

public class TaskStorage {
    private final Path filePath;

    public TaskStorage(String path) {
        this.filePath = Paths.get(path);
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            if (!Files.exists(filePath.getParent())) Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) Files.createFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar arquivo de dados: " + filePath, e);
        }
    }

    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                tasks.add(Task.fromCsv(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo: " + filePath, e);
        }
        return tasks;
    }

    public void save(List<Task> tasks) {
        try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Task t : tasks) {
                bw.write(t.toCsv());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + filePath, e);
        }
    }
}