package clicktheball;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Thread.interrupted;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import static java.lang.Thread.sleep;

public class ClickTheBall
{
    public static void main(String[] args)
    {
        Move frame = new Move();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.show();
    }
}

class Move extends JFrame
{   
    public final int SIZE = 600;
    private int theLevel;
    private Draw panel;
    private JButton start;
    private JButton stop;
    private JButton quitCurr;
    private JComboBox level;
    private JButton quit;
    private JTextField scorefield;
    private JTextField levelfield;
    private boolean gameOnProgress = false;
   
    public Move()
    {
        setSize(SIZE, SIZE);
        setTitle("Click on The ball!!");
        scorefield = new JTextField(5);
        scorefield.setText("0");
        scorefield.setEditable(false);
        setResizable(false);      
        panel = new Draw(this);
        panel.setBorder(BorderFactory.createLineBorder(Color.yellow));
        getContentPane().add(panel);
        start = new JButton("Start");
        stop = new JButton("Pause");
        stop.setEnabled(false);
        quitCurr = new JButton("Quit current game");
        quitCurr.setEnabled(false);
        quit = new JButton("Quit");
        level = new JComboBox();
        level.setEditable(false);
        level.addItem("1");
        level.addItem("2");
        level.addItem("3");
        level.addItem("4");
        level.addItem("5");
        level.addItem("6");
        level.addItem("7");
        level.addItem("8");
        level.addItem("9");
        level.addItem("10");
        JPanel lowerPanel = new JPanel();
        lowerPanel.setBackground(Color.LIGHT_GRAY);
        lowerPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        JPanel upperPanel = new JPanel();
        upperPanel.setBackground(Color.LIGHT_GRAY);
        upperPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        JLabel label = new JLabel("Score");
        upperPanel.add(label);
        upperPanel.add(scorefield);
        JLabel label2 = new JLabel("Current Level");
        levelfield = new JTextField(5);
        levelfield.setEditable(false);
        levelfield.setText("1");
        upperPanel.add(label2);
        upperPanel.add(levelfield);
        music();
        start.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                panel.startSimulation();
                gameOnProgress = true;
                start.setEnabled(false);
                stop.setEnabled(true);
                quitCurr.setEnabled(true);
                panel.resetScore();
            }
        });
        
        stop.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                panel.stopSimulation();
                gameOnProgress = false;
                start.setEnabled(true);
                stop.setEnabled(false);
                quitCurr.setEnabled(false);
            }
        });
        quit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                panel.stopSimulation();
                gameOnProgress = false;
                System.exit(0);
            }
        });
        level.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String lev = (String) level.getSelectedItem();
                theLevel = Integer.parseInt(lev);
                levelfield.setText(lev);
                panel.updateLevel(theLevel);
            }
        });
        quitCurr.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                panel.stopSimulation();
                gameOnProgress = false;
                start.setEnabled(true);
                stop.setEnabled(false);
                quitCurr.setEnabled(false);
                panel.setLocation(0.0,0.0);
                panel.resetScore();
            }
        });
        lowerPanel.add(start);
        lowerPanel.add(stop);
        lowerPanel.add(quitCurr);
        lowerPanel.add(new JLabel("Level"));
        lowerPanel.add(level);
        lowerPanel.add(quit);
        getContentPane().add(upperPanel, BorderLayout.NORTH);
        getContentPane().add(lowerPanel, BorderLayout.SOUTH);
    }

    public void music() {
        try {
            File soundFile = new File("E:\\ClickTheBall\\ClickTheBall\\src\\clicktheball\\sound\\Roman_Cano_Melody_Loops.wav");
            AudioInputStream sound = AudioSystem.getAudioInputStream(soundFile);
 
            // load the sound into memory (a Clip)
            DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(sound);
 
            // due to bug in Java Sound, explicitly exit the VM when
            // the sound has stopped.
            clip.addLineListener(new LineListener() {
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        event.getLine().close();
                    }
                }
            });
 
            // play the sound clip
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch (IOException e) {
        }
        catch (LineUnavailableException e) {
        }
        catch (UnsupportedAudioFileException e) {
        }
    }


    
    public JTextField getMedia()
    {
        return scorefield;
    }

    public void enableStartButton()
    {
        start.setEnabled(true);
        gameOnProgress = true;
    }

    public void disablePauseButton()
    {
        stop.setEnabled(false);
    }

    public void disableQuitButton()
    {
        quitCurr.setEnabled(false);
    }

    public boolean isGameOnProgress()
    {
        return gameOnProgress;
    }

    public int getCurrentLevel()
    {
        return theLevel;
    }
}



class Draw extends JPanel
{
    public static final double RADIUS = 30.0;
    public static final int GAMEPOINT = 15;
    private Ellipse2D.Double ellipse;
    private Move m;
    private JTextField field;
    private Graphics2D g2;
    private static int score;
    private static int level;
    private double topEX;
    private double topEY;
    private Runner simulator;
    private int red;
    private int green;
    private int blue;  
    private BufferedImage image;
               
    public Draw(Move m)
    {
        topEX = 0.0;
        topEY = 0.0;
        this.m = m;
        score = 0;
        field = m.getMedia();
        level = 1;
        addMouseListener(new MouseHandler());
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        //background image setup
        try {
            image = ImageIO.read(new File("E:\\ClickTheBall\\ClickTheBall\\src\\clicktheball\\image\\Background3.gif"));
        } catch (IOException e) {} 
        g.drawImage(image, 0, 0, null);
        g2 = (Graphics2D) g;
        red = (int)(Math.random() * 255);
        green = (int)(Math.random() * 255);
        blue = (int)(Math.random() * 255);
        g2.setPaint(new Color(red, green, blue));
        g2.fill(ellipse = new Ellipse2D.Double(topEX, topEY, RADIUS, RADIUS)); 
   }

    public void setLocation(double x, double y)
    {
        topEX = x;
        topEY = y;
    }

    public void startSimulation()
    {
        simulator = new Runner(this, RADIUS, level);
        simulator.start();
    }

    public void stopSimulation()
    {
        if(simulator != null)
            simulator.interrupt();
    }

    public double getXP()
    {
        return topEX;
    }

    public double getYP()
    {
        return topEY;
    }

    public void updateLevel(int s)
    {
        if(simulator != null){
            simulator.update(s);
            level=s+1;
            score=0;}
    }

    public void resetScore()
    {
        score = 0;
        field.setText("0");
    }

    private class MouseHandler extends MouseAdapter
    {
        public void mousePressed(MouseEvent e)
        {
            Point p = e.getPoint();
            double x = p.getX();
            double y = p.getY();
            if(m.isGameOnProgress())
            {
                if(ellipse.contains(x, y)){
                    field.setText("" + ++score);
                }
                if(score >= GAMEPOINT)
                {
                    stopSimulation();
                    m.enableStartButton();
                    m.disablePauseButton();
                    m.disableQuitButton();
                    setLocation(0.0, 0.0);
                    JOptionPane.showMessageDialog(null, "You have won!!!", "Level Completed!!!",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
}

class Runner extends Thread
{
    public static final double BAR =500.0;
    public static final double OFFSET = 75.0;
    private int speed;
    private double rad;
    private Draw panel;
    private int fact;
    private double topEX;
    private double topEY;
    private double dx = 40.0;
    private double dy = 40.0;
    public Runner(Draw panel, double rad, int fact)
    {
        this.panel = panel;
        this.rad = rad;
        this.fact = fact;
        speed = 1200 / fact;
        topEX = panel.getXP();
        topEY = panel.getYP();
    }

    public void run()
    {
        try
        {
            while(!interrupted())
            {
                topEX += dx;
                topEY += dy;
                if(topEX < 0)
                {
                    topEY += dy;
                    dx = Math.abs(dx);
                }
                if(topEX + rad >= BAR + OFFSET)
                {
                    topEY += dy;
                    dx = -dx;
                }
                if(topEY < 0)
                {
                    topEY = 0;
                    dy = -dy;
                }
                if(topEY + rad >= BAR)
                {
                    topEY = BAR - rad;
                    dy = -dy;
                }
                panel.setLocation(topEX, topEY);
                panel.repaint();
                sleep(speed);
            }
        }
        catch(InterruptedException e){}
   }

   public void update(int s)
   {
        fact = s;
        speed = 1200 / fact;
   }
}