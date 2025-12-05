import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel {

    private GameState state;
    private TaskRenderer taskRenderer;
    private Font fontLarge = new Font("Arial", Font.BOLD, 24);
    private Font fontSmall = new Font("Arial", Font.BOLD, 16);
    private Font fontMessage = new Font("Arial", Font.PLAIN, 20);

    public GamePanel() {
        setPreferredSize(new Dimension(1280, 1024));
        setBackground(new Color(60, 60, 60));

        this.state = new GameState();
        this.taskRenderer = new TaskRenderer();

        Timer messageTimer = new Timer(5000, e -> {
            state.message = "";
            repaint();
        });
        messageTimer.setRepeats(false);


        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (state.gameOver) return;

                Point p = e.getPoint();

                if (state.endDayButton.contains(p)) {
                    state.nextDay();
                    if (!state.message.isEmpty()) messageTimer.restart();
                    repositionTasks();
                    repaint();
                    return;
                }

                Task clickedOnDesktop = findClickedTask(state.desktop, p);
                if (clickedOnDesktop != null) {
                    tryCompleteTask(clickedOnDesktop, messageTimer);
                    repositionTasks();
                    repaint();
                    return;
                }

                Task clickedOnList = findClickedTask(state.taskList, p);
                if (clickedOnList != null) {
                    state.taskList.remove(clickedOnList);
                    state.desktop.add(clickedOnList);
                    repositionTasks();
                    repaint();
                    return;
                }
            }
        });
    }

    private Task findClickedTask(java.util.List<Task> list, Point p) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).bounds.contains(p)) {
                return list.get(i);
            }
        }
        return null;
    }

    private void tryCompleteTask(Task t, Timer timer) {
        int hourCost = t.getHourCost();
        int energyCost = t.getEnergyCost();
        int motivationChange = t.getMotivationChange();
        if (state.workHours < hourCost) {
            state.message = "Brak godzin pracy na to zadanie!";
        } else if (state.energy < energyCost) {
            state.message = "Za mało energii! (Wymagane: " + energyCost + ")";
        } else if (state.motivation + motivationChange < 0) {
            state.message = "Brak motywacji! (Koszt: " + (-motivationChange) + ")";
        } else {
            state.workHours -= hourCost;
            state.energy -= energyCost;
            state.motivation += motivationChange;
            if (state.motivation >= 100) state.motivation = GameState.MAX_MOTIVATION;
            state.desktop.remove(t);
            //state.doneList.add(t);
            state.completeTask(t);
            state.tasksCompletedToday++;
        }
    }

    private void repositionTasks() {
        layoutInZone(state.taskList, state.taskZone);
        layoutInZone(state.desktop, state.desktopZone);
        layoutInZone(state.doneList, state.doneZone);
    }

    private void layoutInZone(java.util.List<Task> list, Zone z) {
        int x = z.bounds.x + 10;
        int y = z.bounds.y + 40;
        int padding = 10;
        int cols = (z.bounds.width - 20) / (150 + padding);
        if (cols == 0) cols = 1;

        for (int i = 0; i < list.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int posX = x + col * (150 + padding);
            int posY = y + row * (100 + padding);
            list.get(i).bounds.setLocation(posX, posY);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (state.day == 1 && state.desktop.isEmpty() && state.doneList.isEmpty()) {
            repositionTasks();
        }

        drawZones(g2d);
        drawHUD(g2d);
        drawEndDayButton(g2d);
        for (Task t : state.taskList) taskRenderer.drawTask(g, t, state.day);
        for (Task t : state.desktop) taskRenderer.drawTask(g, t, state.day);
        for (Task t : state.doneList) taskRenderer.drawTask(g, t, state.day);
        if (!state.message.isEmpty()) {
            g.setFont(fontMessage);
            g.setColor(Color.WHITE);
            g.drawString(state.message, 20, 1010);
        }

        if (state.gameOver) {
            drawGameOver(g2d);
        }
    }

    private void drawZones(Graphics2D g) {
        g.setColor(new Color(80, 80, 80));
        g.fill(state.taskZone.bounds);
        g.fill(state.desktopZone.bounds);
        g.fill(state.doneZone.bounds);
        g.setColor(Color.WHITE);
        g.setFont(fontSmall);
        g.drawString(state.taskZone.name, state.taskZone.bounds.x + 10, state.taskZone.bounds.y + 25);
        g.drawString(state.desktopZone.name, state.desktopZone.bounds.x + 10, state.desktopZone.bounds.y + 25);
        g.drawString(state.doneZone.name, state.doneZone.bounds.x + 10, state.doneZone.bounds.y + 25);
    }

    private void drawHUD(Graphics2D g) {
        g.setColor(new Color(117, 146, 158));
        g.fillRect(0, 0, 1280, 100);
        g.setFont(fontLarge);
        g.setColor(Color.BLACK);
        g.drawString("ENERGIA", 50, 40);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(220, 20, 300, 25);
        g.setColor(Color.GREEN);
        g.fillRect(220, 20, (int)(300 * (state.energy / (double)GameState.MAX_ENERGY)), 25);
        g.setColor(Color.BLACK);
        g.drawString("MOTYWACJA", 50, 80);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(220, 60, 300, 25);
        g.setColor(Color.RED);
        g.fillRect(220, 60, (int)(300 * (state.motivation / (double)GameState.MAX_MOTIVATION)), 25);
        g.setFont(fontLarge);
        g.setColor(Color.DARK_GRAY);
        g.drawString(String.format("Dzień: %d / %d", state.day, GameState.MAX_DAYS), 600, 60);
        g.drawString(String.format("Pozostało: %d h", state.workHours), 850, 60);
    }

    private void drawEndDayButton(Graphics2D g) {
        g.setColor(new Color(0, 100, 0)); // ciemne zielone
        g.fill(state.endDayButton);
        g.setFont(fontSmall);
        g.setColor(Color.WHITE);
        g.drawString("ZAKOŃCZ DZIEŃ", state.endDayButton.x + 40, state.endDayButton.y + 30);
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200)); // przeźroczyste tlo
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.setColor(Color.WHITE);
        String[] display = state.gameOverMessage.split("_");
        g.drawString(display[0],300,450);
        g.setFont(new Font("Arial", Font.BOLD, 35));
        g.drawString(display[1],300,500);
    }
}