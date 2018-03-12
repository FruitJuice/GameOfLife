import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;


public class Game extends JFrame implements Runnable, MouseListener{

    private static final Dimension WindowSize = new Dimension(800,800);
    private BufferStrategy strategy;
    private Graphics offscreenBuffer;
    private boolean gameState[][] = new boolean[40][40];

   public Game(){
       //Make a display window centered on the screen
       Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
       int x = screensize.width/2 - WindowSize.width/2;
       int y = screensize.height/2 - WindowSize.height/2;
       setBounds(x, y, WindowSize.width, WindowSize.height);
       setVisible(true);
       this.setTitle("Conway's game of Life");

       Thread t = new Thread(this);
       t.start();

       createBufferStrategy(2);
       strategy = getBufferStrategy();
       offscreenBuffer = strategy.getDrawGraphics();


       addMouseListener(this);

       for(x=0;x<40;x++){
           for (y=0;y<40;y++){
               gameState[x][y]=false;
           }
       }


   }

    public void run(){
        while(1==1) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) { }

            this.repaint();
        }
    }

    public void mousePressed(MouseEvent e) {
       int x = e.getX()/20;
       int y = e.getY()/20;

       gameState[x][y] = !gameState[x][y];

       this.repaint();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void paint(Graphics g){
       g = strategy.getDrawGraphics();

       g.setColor(Color.BLACK);
       g.fillRect(0, 0, WindowSize.width, WindowSize.height);

       g.setColor(Color.white);
       for(int x=0;x<40;x++){
           for (int y=0;y<40;y++){
               if(gameState[x][y]){
                   g.fillRect(x*20,y*20,20,20);
               }
           }

        }

        //g.dispose();
        strategy.show();

    }

    public static void main(String[] args) {
        Game d = new Game();
    }

}
