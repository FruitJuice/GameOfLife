import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.io.*;


public class Game extends JFrame implements Runnable, MouseListener, MouseMotionListener {

    private static final Dimension WindowSize = new Dimension(800, 800);
    private BufferStrategy strategy;
    private Graphics offscreenBuffer;
    private boolean gameState[][][] = new boolean[40][40][2];
    private boolean isGamePlaying;
    private boolean isInitialised = false;
    private int front;
    private int back;
    private String filename = "C:\\Users\\sndri\\desktop\\lifegame.txt";

    public Game() {
        //Make a display window centered on the screen
        Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = screensize.width / 2 - WindowSize.width / 2;
        int y = screensize.height / 2 - WindowSize.height / 2;
        setBounds(x, y, WindowSize.width, WindowSize.height);
        setVisible(true);
        this.setTitle("Conway's game of Life");

        isGamePlaying = false;
        front = 0;
        back = 1;

        createBufferStrategy(2);
        strategy = getBufferStrategy();
        offscreenBuffer = strategy.getDrawGraphics();


        addMouseListener(this);
        addMouseMotionListener(this);

        for (int z = 0; z < 2; z++) {
            for (x = 0; x < 40; x++) {
                for (y = 0; y < 40; y++) {
                    gameState[x][y][z] = false;
                }
            }
        }


        Thread t = new Thread(this);
        t.start();

        isInitialised = true;
    }

    public void run() {
        while (1 == 1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }

            if (isGamePlaying) {
                //loop through each cell
                for (int x = 0; x < 40; x++) {
                    for (int y = 0; y < 40; y++) {
                        //count how many alive neighbours does each cell have
                        int noAlive = 0;
                        //loop through each cells neighbours to check their state
                        for (int xx=-1;xx<=1;xx++) {
                            for (int yy = -1; yy <= 1; yy++) {
                                //make sure we're not checking the cell itself
                                if (!(xx==0 && yy==0)) {
                                    //if off-screen we loop it back
                                    int tempX = (x+xx);
                                    int tempY = (y+yy);
                                    if(tempX < 0) tempX = 39;
                                    if(tempX > 39) tempX = 0;

                                    if(tempY < 0) tempY = 39;
                                    if(tempY > 39) tempY = 0;

                                    //if the nighbour is alive we add to the alive count
                                    if (gameState[tempX][tempY][front]) noAlive++;
                                    }
                                }
                            }
                        //checking for the rules of the game
                        if (noAlive < 2) gameState[x][y][back] = false;
                        if (noAlive == 3) gameState[x][y][back] = true;
                        if (gameState[x][y][front]){
                            if(noAlive == 2){
                                gameState[x][y][back]=true;
                            }
                        }
                        if (noAlive > 3) gameState[x][y][back] = false;
                    }
                }

                //copying the changes we made in the back to the front, which will be rendered
                for(int x  = 0; x<40;x++){
                    for(int y = 0; y <40; y++){
                        gameState[x][y][front] = gameState[x][y][back];
                    }
                }
            }

            //repainting the game
            this.repaint();
        }
    }

    public void mousePressed(MouseEvent e) {

        if(!isGamePlaying) {

            int x = e.getX() / 20;
            int y = e.getY() / 20;

            //toggle between alive/dead when mouse pressed
            gameState[x][y][front] = !gameState[x][y][front];
            if ((e.getX() > 50 && e.getX() < 120) && (e.getY() > 50 && e.getY() < 80)) {
                isGamePlaying = true;
            }

            if ((e.getX() > 150 && e.getX() < 250) && (e.getY() > 50 && e.getY() < 80)) {
                randomise();
            }

            if ((e.getX() > 290 && e.getX() < 350) && (e.getY() > 50 && e.getY() < 80)) {
                save();
            }

            if ((e.getX() > 390 && e.getX() < 450) && (e.getY() > 50 && e.getY() < 80)) {
                load();
            }
        }
    }

    public void randomise(){
        //randomise if each cell is alive or dead
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                if(Math.random() < 0.45) {
                    gameState[x][y][front] = true;
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }


    public void mouseMoved(MouseEvent e){

    }

    public void mouseDragged(MouseEvent e){
        if(!isGamePlaying) {

            int x = e.getX() / 20;
            int y = e.getY() / 20;

            //toggle between alive/dead when mouse pressed
            gameState[x][y][front] = true;
        }
    }

    //method for writing the buttons to START/RANDOM game
    public void writeString(Graphics g, int x, int y, int fontSize, String message) {
        Font f = new Font("Arabic", Font.BOLD, fontSize);
        g.setFont(f);
        FontMetrics fm = getFontMetrics(f);
        int width = fm.stringWidth(message);
        g.drawString(message, x - width / 2, y);
    }

    public void load(){
        String line = null;
        int lineNum = 0;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            do{
                try {
                    line = reader.readLine();
                    if(Integer.parseInt(line) == 1){
                        int xpos = lineNum/40;
                        int ypos = (xpos*40)-lineNum;
                        if(ypos <= 0){
                            ypos = ypos * -1;
                        }

                        gameState[xpos][ypos][front] = true;
                    }
                }catch(IOException e){}

                lineNum++;
            }
            while(line!=null);

            reader.close();
        }
        catch (IOException e){}

        this.repaint();
    }

    public void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            String data = "";

            for(int x=0;x<40;x++) {
                for(int y=0;y<40;y++) {
                    if(gameState[x][y][front])
                    {
                        data+="1\r";
                    }
                    else
                        data+="0\r";
                }
            }
            writer.write(data);
            writer.close();
        }
        catch (IOException e) { }
    }

    public void paint(Graphics g) {
        if(isInitialised) {
            g = strategy.getDrawGraphics();

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WindowSize.width, WindowSize.height);

            if(!isGamePlaying) {
                g.setColor(Color.GREEN);
                g.fillRect(50, 50, 70, 30);
                g.fillRect(150, 50, 100, 30);
                g.fillRect(290, 50, 60, 30);
                g.fillRect(390, 50, 60, 30);
                g.setColor(Color.black);
                writeString(g, 80, 70, 20, "Start");
                writeString(g, 200, 70, 20, "Random");
                writeString(g, 320, 70, 20, "Save");
                writeString(g, 420, 70, 20, "Load");
            }

            g.setColor(Color.white);
            for (int x = 0; x < 40; x++) {
                for (int y = 0; y < 40; y++) {
                    if (gameState[x][y][front]) {
                        g.setColor(Color.white);
                        g.fillRect(x * 20, y * 20, 20, 20);
                    }
                }

            }

            strategy.show();
        }

    }




    public static void main(String[] args) {
        Game d = new Game();
    }

}
