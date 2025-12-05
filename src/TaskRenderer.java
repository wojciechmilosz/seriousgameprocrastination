import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class TaskRenderer {
    private Font fontName = new Font("Arial", Font.BOLD, 14);
    private Font fontInfo = new Font("Arial", Font.PLAIN, 11);
    private Color backgroundColor = new Color(253, 253, 215); // zoltawy

    public void drawTask(Graphics g, Task t, int currentDay) {
        Graphics2D g2d = (Graphics2D) g;
        int daysToDeadline = t.deadlineDay - currentDay;
        if (daysToDeadline <= 1) g2d.setColor(Color.RED);
        else g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(t.bounds.x-2, t.bounds.y-2, t.bounds.width+4, t.bounds.height+5, 10, 10);
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(t.bounds.x, t.bounds.y, t.bounds.width, t.bounds.height, 10, 10);
        g.setColor(Color.BLACK);
        g.setFont(fontName);
        g.drawString(t.name, t.bounds.x + 10, t.bounds.y + 20);
        g.setFont(fontInfo);
        g.drawString("Termin: Dzień " + t.deadlineDay, t.bounds.x + 10, t.bounds.y + 35);
        drawBar(g, "Trudność:", t.difficulty, Color.RED, t.bounds.x + 10, t.bounds.y + 50);
        drawBar(g, "Ciekawość:", t.curiosity, Color.BLUE, t.bounds.x + 10, t.bounds.y + 65);
        drawBar(g, "Praca:", t.workload, Color.ORANGE, t.bounds.x + 10, t.bounds.y + 80);
    }

    private void drawBar(Graphics g, String label, int value, Color color, int x, int y) {
        int barWidth = 75;
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x + 60, y - 8, barWidth, 10);
        g.setColor(color);
        g.fillRect(x + 60, y - 8, (int)(barWidth * (value / 5.0)), 10);
        g.setColor(Color.BLACK);
        g.drawString(label, x, y);
    }
}