package src.app;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import src.app.model.Priority;
import src.model.Status;
import src.model.Task;
import src.service.TaskService;
import src.storage.TaskStorage;

public class Main {

    public static void main(String[] args) {
        Locale.setDefault(new Locale("pt", "BR"));

        TaskService service = new TaskService(new TaskStorage("src/data/tasks.csv"));
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Task Manager (Java Console) ===");
            System.out.println("1) Adicionar tarefa");
            System.out.println("2) Listar todas");
            System.out.println("3) Listar pendentes");
            System.out.println("4) Listar concluidas");
            System.out.println("5) Concluir tarefa");
            System.out.println("6) Editar tarefa");
            System.out.println("7) Remover tarefa");
            System.out.println("0) Sair");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();

            try {
                switch (op) {
                    case "1" -> add(sc, service);
                    case "2" -> print(service.listAll());
                    case "3" -> print(service.listByStatus(Status.PENDING));
                    case "4" -> print(service.listByStatus(Status.DONE));
                    case "5" -> done(sc, service);
                    case "6" -> edit(sc, service);
                    case "7" -> remove(sc, service);
                    case "0" -> {
                        System.out.println("Falou!");
                        return;
                    }
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void print(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Nenhuma tarefa encontrada.");
            return;
        }
        tasks.forEach(System.out::println);
    }

    private static void add(Scanner sc, TaskService service) {
        System.out.print("Titulo: ");
        String title = sc.nextLine();

        Priority pr = askPriority(sc);
        Task t = service.add(title, pr);
        System.out.println("Criada: " + t);
    }

    private static void done(Scanner sc, TaskService service) {
        System.out.print("Digite o ID (ou prefixo de 8 chars) da tarefa: ");
        String id = sc.nextLine();
        boolean ok = service.markDone(id);
        System.out.println(ok ? "Tarefa concluida." : "Nao encontrei (ou ID ambiguo).");
    }

    private static void edit(Scanner sc, TaskService service) {
        System.out.print("ID/prefixo: ");
        String id = sc.nextLine();

        System.out.print("Novo titulo (enter pra manter): ");
        String title = sc.nextLine();

        System.out.print("Alterar prioridade? (s/n): ");
        String yn = sc.nextLine().trim().toLowerCase();
        Priority pr = null;
        if (yn.equals("s")) pr = askPriority(sc);

        boolean ok = service.edit(id, title, pr);
        System.out.println(ok ? "Editada." : "Nao encontrei (ou ID ambiguo).");
    }

    private static void remove(Scanner sc, TaskService service) {
        System.out.print("ID/prefixo: ");
        String id = sc.nextLine();
        boolean ok = service.remove(id);
        System.out.println(ok ? "Removida." : "Nao encontrei (ou ID ambiguo).");
    }

    private static Priority askPriority(Scanner sc) {
        while (true) {
            System.out.print("Prioridade (1=LOW, 2=MEDIUM, 3=HIGH): ");
            String p = sc.nextLine().trim();
            switch (p) {
                case "1": return Priority.LOW;
                case "2": return Priority.MEDIUM;
                case "3": return Priority.HIGH;
                default: System.out.println("Valor invalido.");
            }
        }
    }
}
