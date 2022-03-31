
package animacjakropelki;
    import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
    import javax.swing.*;

public class Main extends JFrame
{
public Main ()
{
    this.setTitle("Animacja kropelki");
    this.setBounds(250, 300, 300, 250);
    panelAnimacji.setBackground(Color.GRAY);
    JButton bStart =(JButton)panelButtonow.add(new JButton("Start"));
    
    bStart.addActionListener(new ActionListener() 
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
           startAnimation ();
        }
    });
    
    JButton bStop =(JButton)panelButtonow.add(new JButton("Stop"));
    
    bStop.addActionListener(new ActionListener() 
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
           stopAnimation ();
        }
    });
    
    JButton bDodaj =(JButton)panelButtonow.add(new JButton("Dodaj"));
    
    bDodaj.addActionListener(new ActionListener() 
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
           dodajAnimation ();
        }
    });
    
    this.getContentPane().add(panelAnimacji);
    this.getContentPane().add(panelButtonow, BorderLayout.SOUTH);
    
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
}

public void startAnimation ()
{
    panelAnimacji.startAnimation();
}
public void stopAnimation ()
{
    panelAnimacji.stop();
}

public void dodajAnimation ()
{
    panelAnimacji.addKropelka();
}


    private JPanel panelButtonow = new JPanel ();
    private PanelAnimacji panelAnimacji = new PanelAnimacji ();
    
    public static void main(String[] args) 
    {
       new Main().setVisible(true);
    }
    class PanelAnimacji extends JPanel 
    {
        private volatile boolean zatrzymany = false;
        private Object lock = new Object();
        public void addKropelka ()
        {
            listaKropelek.add(new Kropelka());
            watek = new Thread (grupaWatkow, new KropelkaRunnable((Kropelka)listaKropelek.get(listaKropelek.size()-1)));
            watek.start();
            
            grupaWatkow.list();
            
        }
         public void stop()
         {
             zatrzymany = true;
         }
         
         public void startAnimation()
        {
           // grupaWatkow.interrupt();
            
            if (zatrzymany)
            {
                zatrzymany = false;
                synchronized (lock)
                {
                    lock.notifyAll();
                }
            }
        }
        
        @Override
        public void paintComponent (Graphics g)
        {
            super.paintComponent(g);
            
            for (int i =0; i<listaKropelek.size(); i++)
            {
                g.drawImage(Kropelka.getImg(),((Kropelka)listaKropelek.get(i)).x, ((Kropelka)listaKropelek.get(i)).y, null);
            }
        }

        ArrayList listaKropelek = new ArrayList();
        JPanel ten = this;
        Thread watek;
        ThreadGroup grupaWatkow =  new ThreadGroup ("Grupa kropelek");
        
        public class KropelkaRunnable implements Runnable
        {

        public KropelkaRunnable (Kropelka kropelka)
        {
            this.kropelka = kropelka;
        }
        
        @Override
        public void run() 
        {
                /* try 
                {*/
                    while (true)//!Thread.currentThread().isInterrupted())
                    {
                        synchronized (lock)
                        {
                            while (zatrzymany)
                            {
                                try 
                                {
                                    lock.wait();
                                } 
                                catch (InterruptedException ex) 
                                {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    this.kropelka.ruszKropelka(ten); ;
                    repaint();
                        try 
                        { 
                            Thread.sleep(10);
                        }
                        catch (InterruptedException ex) 
                        {
                            ex.printStackTrace();
                        }
                        
                    } 
                
             //}
                 /*catch (InterruptedException ex) 
                {
                 System.out.println(ex.getMessage());
                 listaKropelek.clear();
                 repaint();
                 }*/
        }
        
           Kropelka kropelka; 
        }
    }
}

class Kropelka 
{
    public static Image getImg ()
            {
                return Kropelka.kropelka;
            }
    public void ruszKropelka (JPanel pojemnik)
    {
       Rectangle granicePanelu =  pojemnik.getBounds();
        x += dx;
        y += dy;
        
        if ( y + yKropelki >= granicePanelu.getMaxY())
        {
            y = (int)(granicePanelu.getMaxY()- yKropelki);
            dy = - dy;
        }
        if (x + xKropelki >= granicePanelu.getMaxX())
        {
            x = (int)(granicePanelu.getMaxX() - xKropelki);
            dx = -dx;
        }
        if (y < granicePanelu.getMinY())
        {
            y = (int)granicePanelu.getMinY();
            dy = -dy;
        }
        if (x < granicePanelu.getMinX())
        {
            x = (int)granicePanelu.getMinX();
            dx = -dx;
        }
    }
    public static Image kropelka = new ImageIcon("kropelka.gif").getImage();
    int x = 0;
    int y = 0;
    int dx = 1;
    int dy =1 ;
    int xKropelki = kropelka.getWidth(null);
    int yKropelki = kropelka.getHeight(null);
}