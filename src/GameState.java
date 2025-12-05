import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.awt.Rectangle;

public class GameState {

    public static final int MAX_ENERGY = 100;
    public static final int MAX_MOTIVATION = 100;
    public static final int MAX_WORK_HOURS = 6;
    public static final int MAX_DAYS = 10;

    public int energy;
    public int motivation;
    public int workHours;
    public int day;
    public int tasksCompletedToday;
    public int tasksFailed;

    // Lists storing tasks in different zones
    public List<Task> taskList;   // "Lista Zadań"
    public List<Task> desktop;    // "Biurko"
    public LinkedList<Task> doneList;
    public List<Task> failedList;
    // all tasks
    private Queue<Task> taskQueue;

    public Zone taskZone;
    public Zone desktopZone;
    public Zone doneZone;

    public Rectangle endDayButton;

    public String message = "";
    public boolean gameOver = false;
    public String gameOverMessage = "";

    public GameState() {
        taskZone = new Zone("Lista Zadań", 1000, 150, 250, 800);
        desktopZone = new Zone("Biurko", 250, 150, 730, 800);
        doneZone = new Zone("Gotowe", 30, 150, 200, 800);

        endDayButton = new Rectangle(1050, 30, 200, 50);

        taskList = new ArrayList<>();
        desktop = new ArrayList<>();
        doneList = new LinkedList<>();
        failedList = new ArrayList<>();
        taskQueue = new LinkedList<>();

        initializeTaskPool();
        startGame();
    }

    private void startGame() {
        this.day = 1;
        this.energy = MAX_ENERGY;
        this.motivation = MAX_MOTIVATION;
        this.workHours = MAX_WORK_HOURS;
        this.tasksCompletedToday = 0;
        tasksFailed = 30;
        addNewTasks();
    }

    private void initializeTaskPool() {
        taskQueue.add(new Task("z1", 3, 1, 1, 2));
        taskQueue.add(new Task("z2", 2, 2, 4, 1));
        taskQueue.add(new Task("z3", 2, 3, 2, 1));

        taskQueue.add(new Task("z4", 3, 3, 3, 3));
        taskQueue.add(new Task("z5", 5, 2, 2, 4));
        taskQueue.add(new Task("z6", 3, 1, 1, 2));

        taskQueue.add(new Task("z7", 7, 4, 4, 3));
        taskQueue.add(new Task("z8", 4, 2, 2, 1));
        taskQueue.add(new Task("z9", 5, 2, 5, 1));

        taskQueue.add(new Task("z10", 7, 1, 1, 2));
        taskQueue.add(new Task("z11", 6, 1, 1, 1));
        taskQueue.add(new Task("z12", 6, 2, 4, 1));

        taskQueue.add(new Task("z13", 9, 5, 2, 3));
        taskQueue.add(new Task("z14", 7, 3, 3, 2));
        taskQueue.add(new Task("z15", 9, 2, 2, 3));

        taskQueue.add(new Task("z16", 7, 1, 1, 1));
        taskQueue.add(new Task("z17", 7, 4, 4, 1));
        taskQueue.add(new Task("z18", 8, 2, 2, 1));

        taskQueue.add(new Task("z19", 10, 2, 5, 4));
        taskQueue.add(new Task("z20", 9, 1, 1, 2));
        taskQueue.add(new Task("z21", 9, 1, 1, 1));

        taskQueue.add(new Task("z22", 10, 5, 2, 2));
        taskQueue.add(new Task("z23", 9, 3, 4, 1));
        taskQueue.add(new Task("z24", 9, 3, 3, 1));

        taskQueue.add(new Task("z25", 12, 2, 2, 3));
        taskQueue.add(new Task("z26", 11, 1, 1, 2));
        taskQueue.add(new Task("z27", 12, 4, 4, 1));

        taskQueue.add(new Task("z28", 12, 2, 2, 2));
        taskQueue.add(new Task("z29", 11, 2, 5, 1));
        taskQueue.add(new Task("z30", 12, 1, 1, 2));


    }

    private void addNewTasks() {
        for (int i = 0; i < 3; i++) {
            taskList.add(taskQueue.poll());
        }
    }
    public void nextDay() {
        if (gameOver) return;
        day++;
        if (day > MAX_DAYS) {
            gameOver = true;
            gameOverMessage = "Gratulacje! Przetrwałeś 10 dni!" + "_Pomieniete zadania: " + tasksFailed;
            return;
        }

        if (tasksCompletedToday > 2) {
            motivation = Math.min(MAX_MOTIVATION, motivation + 20); // Bonus
            message = "Dobra robota! +20 Motywacji za serię!";
        } else {
            message = "";
        }
        tasksFailed -= tasksCompletedToday;
        tasksCompletedToday = 0;

        workHours = MAX_WORK_HOURS;
        energy = Math.min(MAX_ENERGY, energy + 25);
        motivation = Math.min(MAX_MOTIVATION, motivation + 15);

        List<Task> toRemove = new ArrayList<>();
        for (Task t : taskList) {
            checkTaskStatus(t);
            if (t.deadlineDay < day) toRemove.add(t);
        }
        for (Task t : desktop) {
            checkTaskStatus(t);
            if (t.deadlineDay < day) toRemove.add(t);
        }

        for (Task t : toRemove) {
            taskList.remove(t);
            desktop.remove(t);
            failedList.add(t);
            motivation -= 20;
            message = "Zawalono termin! -20 Motywacji.";
        }
        addNewTasks();
        if (motivation <= 0) motivation = 0;
    }

    private void checkTaskStatus(Task t) {
        int daysToDeadline = t.deadlineDay - day;
        if (daysToDeadline == 0) {
            energy -= 10;
            message = "Termin goni! -10 Energii (stres).";
        }
    }

    public void completeTask(Task t) {
        this.doneList.addFirst(t);
        while (this.doneList.size() > 6) {
            this.doneList.removeLast();
        }
    }

}