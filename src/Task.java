import java.awt.Rectangle;

public class Task {
    String name;
    int deadlineDay;
    int difficulty; // Scale 1-5
    int curiosity;  // Scale 1-5
    int workload;   // Scale 1-5
    public Rectangle bounds;

    public Task(String name, int deadlineDay, int difficulty, int curiosity, int workload) {
        this.name = name;
        this.deadlineDay = deadlineDay;
        this.difficulty = difficulty;
        this.curiosity = curiosity;
        this.workload = workload;
        this.bounds = new Rectangle(0, 0, 150, 100);
    }

    public int getEnergyCost() {
        return (this.workload * 4) + (this.difficulty * 2);
    }

    public int getMotivationChange() {
        int change = (this.curiosity * 2) - (this.difficulty * 2 );
        if (this.curiosity < 3) { // kara wyjatkowego nudnego zadania
            change -= 5;
        }
        return change;
    }
    public int getHourCost() {
        return this.workload;
    }
}

