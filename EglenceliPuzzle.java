package odev;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

class MyButton extends JButton {
    private boolean isLastButton;
    public MyButton() {
        super();
        initUI();
    }
    public MyButton(Image image) {
        super(new ImageIcon(image));
        initUI();
    }
    private void initUI() {
        isLastButton = false;
        BorderFactory.createLineBorder(Color.blue);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.black));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.blue));
            }
        });
    }
    public void setLastButton() {
        isLastButton = true;
    }
    public boolean isLastButton() {
        return isLastButton;
    }
}
public class EglenceliPuzzle extends JFrame {
    private JPanel panel;
    private BufferedImage source;
    private BufferedImage resized;    
    private Image img;
    private MyButton lastButton;
    private int genislik , yukseklik;    
    private List<MyButton> buton;
    private List<Point> solution;
    private final int NUMBER_OF_BUTTONS = 12;
    private final int DESIRED_WIDTH = 300;
    
    public EglenceliPuzzle () {
        initUI();
    }
    private void initUI() {
        solution = new ArrayList<>();
        solution.add(new Point(0, 0));
        solution.add(new Point(0, 1));
        solution.add(new Point(0, 2));
        solution.add(new Point(1, 0));
        solution.add(new Point(1, 1));
        solution.add(new Point(1, 2));
        solution.add(new Point(2, 0));
        solution.add(new Point(2, 1));
        solution.add(new Point(2, 2));
        solution.add(new Point(3, 0));
        solution.add(new Point(3, 1));
        solution.add(new Point(3, 2));
        buton = new ArrayList<>();
        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 3, 0, 0));
        try {
            source = loadImage();
            int h = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, h,
                    BufferedImage.TYPE_INT_ARGB);
        } catch (IOException ex) {
            Logger.getLogger(EglenceliPuzzle .class.getName()).log(Level.SEVERE, null, ex);
        }
        genislik= resized.getWidth(null);
        yukseklik = resized.getHeight(null);
        add(panel, BorderLayout.CENTER);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                img = createImage(new FilteredImageSource(resized.getSource(),new CropImageFilter(j *genislik / 3, i *yukseklik/ 4, (genislik / 3),yukseklik/ 4)));
                MyButton button = new MyButton(img);
                button.putClientProperty("POZÝSYON", new Point(i, j));
                if (i == 3 && j == 2) {
                    lastButton = new MyButton();
                    lastButton.setBorderPainted(false);
                    lastButton.setContentAreaFilled(false);
                    lastButton.setLastButton();
                    lastButton.putClientProperty("POZÝSYON", new Point(i, j));
                } else {
                	 buton.add(button);
                }
            }
        }
        Collections.shuffle( buton);
        buton.add(lastButton);
        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
            MyButton btn =  buton.get(i);
            panel.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.black));
            btn.addActionListener(new ClickAction());
        }
        pack();
        setTitle("Eðlenceli Puzzle");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    private int getNewHeight(int genislik, int yukseklik) {
        double ratio = DESIRED_WIDTH / (double) genislik;
        int newHeight = (int) (yukseklik* ratio);
        return newHeight;
    }
    private BufferedImage loadImage() throws IOException {
        BufferedImage img = ImageIO.read(new File("C:\\Users\\GÜLCE\\Desktop\\ödev\\minniemouse.jpg"));
        return img;
    }
    private BufferedImage resizeImage(BufferedImage originalImage, int width,
            int height, int type) throws IOException {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }
    private class ClickAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            checkButton(e);
            checkSolution();
        }
        private void checkButton(ActionEvent e) {
            int lidx = 0;
            for (MyButton button :  buton) {
                if (button.isLastButton()) {
                    lidx =  buton.indexOf(button);
                }
            }
            JButton button = (JButton) e.getSource();
            int bidx =  buton.indexOf(button);
            if ((bidx - 1 == lidx) || (bidx + 1 == lidx) || (bidx - 3 == lidx) || (bidx + 3 == lidx)) {
                Collections.swap( buton, bidx, lidx);
                updateButtons();
            }
        }
        private void updateButtons() {
            panel.removeAll();
            for (JComponent btn : buton) {
               panel.add(btn);
            }
            panel.validate();
        }
    }
    private void checkSolution() {
        List<Point> current = new ArrayList<>();

        for (JComponent btn :  buton) {
            current.add((Point) btn.getClientProperty("POZÝSYON"));
        }
        if (compareList(solution, current)) {
            JOptionPane.showMessageDialog(panel, "BÝTTÝ",
                    "TEBRÝKLER,BAÞARDINIZ", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    public static boolean compareList(List ls1, List ls2) {
        return ls1.toString().contentEquals(ls2.toString());
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	EglenceliPuzzle puzzle = new EglenceliPuzzle ();
                puzzle.setVisible(true);
            }
        });
    }
}
