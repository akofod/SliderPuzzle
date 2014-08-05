import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;


public class PuzzleGame extends JApplet implements ActionListener, MouseListener {

    private static final long serialVersionUID = 1L;
    private int[][] grid = new int[4][4];
    private JButton importButton;
    private JButton shuffleButton;
    private JButton solveButton;
    private JFileChooser importImage;
    private BufferedImage img;
    private BufferedImage[] imgParts = new BufferedImage[15];
    private File f;
    private int h = 100;
    private int w = 100;
    private JPanel buttonPanel;
    private boolean clearBoard = false;
    
    public void init() {
        setSize(500, 500);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        //Create file chooser and filter
        importImage = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpeg", "jpg", "gif");
        importImage.setFileFilter(filter);
        importImage.setAcceptAllFileFilterUsed(true);
        
        //Add buttons
        buttonPanel = new JPanel(new FlowLayout());
        importButton = new JButton("Import Image");
        importButton.addActionListener(this);
        buttonPanel.add(importButton);
        shuffleButton = new JButton("Shuffle");
        shuffleButton.addActionListener(this);
        buttonPanel.add(shuffleButton);
        solveButton = new JButton("Solve Puzzle");
        solveButton.addActionListener(this);
        buttonPanel.add(solveButton);
        add(buttonPanel, BorderLayout.NORTH);

        //Add listeners
        addMouseListener(this);
        
        //Load initial images and set grid position numbers
        loadDefaultImage();
        initializeGrid();
    }
    
    // Set the initial grid numbers
    public void initializeGrid( ){
        int m = 0;
        for (int r = 0; r <= 3; r++) {
            for (int c = 0; c <= 3; c++) {
                m++;
                grid[r][c] = m;
            }
        }
        grid[3][3] = 0;
    }
    
    //Load the default images into the grid
    public void loadDefaultImage() {
        for (int i = 0; i < 15; i++) {
            f = new File((i + 1) + ".jpg");
        
            try {
                img = ImageIO.read(f);
            }
            catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
            w = img.getWidth();
            h = img.getHeight();
            imgParts[i] = img;
        }
    }
       
    //Import image from file
    public void importImage(File file) {
        f = file;
        try {
            img = ImageIO.read(f);
        }
        catch (Exception e){
            System.out.println(e);
            System.exit(0);
        }
        w = img.getWidth() / 4;
        h = img.getHeight() / 4;
        int m = 0;
        for (int r = 0; r <= 3; r++){
            for (int c = 0; c <=3 ; c++) {
                if (m == 15) {break;}
                imgParts[m] = img.getSubimage(c * w, r * h, w, h);
                m++;
            }
        }
        clearBoard = true;
        repaint();
    }
    
    
    // Checks if click is on game board
    public boolean isValidPoint(Point p){
        if (p.x < (w * 4) + 50 && p.x > 50 && p.y < (h * 4) + 50 && p.y > 50){
            return true;
        }
        return false;
    }
    
    // Shuffle the board
    public void shuffle() {
        int m;
        int n;
        for (int r = 0; r <= 3; r++){
            for (int c = 0; c <= 3; c++){
                if (grid[r][c] == 0){
                    grid[r][c] = grid[3][3];
                    grid[3][3] = 0;
                }
                do{
                    m = (int)(Math.random()*3);
                    n = (int)(Math.random()*3);
                }
                while(m == 3 && n == 3);
                if (r == 3 && c ==3)
                    break;
                int temp = grid[r][c];
                grid[r][c] = grid[m][n];
                grid[m][n] = temp;
            }
        }
        repaint();
    }
    
    //Sort the board back to original
    public void sort() {
        initializeGrid();
        repaint();
    }
    
    //Get the corner point of a square for any click inside
    private Point getSquareAt(Point p){
        System.out.println("Point Clicked: " + p.toString());
        Point sqPoint = new Point((p.x - 50) / w, (p.y - 50) / h);
        System.out.println("Point Changed to: " + sqPoint.toString());
        return sqPoint;
    }
    
    //Get the number for the square at a corner point 
    private int getNumAt(Point p){
        if (p.y == 0)
            return p.x;
        else if (p.y == 1)
            return 4 + p.x;
        else if (p.y == 2)
            return 8 + p.x;
        else if (p.y == 3)
            return 12 + p.x;
        else
            return 0;
    }
    
    // Get the column for the square clicked
    public int getCol(int n){
        return n % 4;
    }
    // Get the row for the square clicked
    public int getRow(int n){
        return n / 4;
    }
    
    public boolean selectSquare(int square){
        //Checks all squares around the clicked square and returns true once found
        if (square == 0) {
            return swapPiece(0, 1) || swapPiece(0, 4);
        }
        else if(square == 1) {
            return swapPiece(1, 0) || swapPiece(1, 5) || swapPiece(1, 2);
        }
        else if(square == 2) {
            return swapPiece(2, 1) || swapPiece(2, 6) || swapPiece(2, 3);
        }
        else if(square == 3) {
            return swapPiece(3, 2) || swapPiece(3, 7);
        }
        else if(square == 4) {
            return swapPiece(4, 0) || swapPiece(4, 5) || swapPiece(4, 8);
        }
        else if(square == 5) {
            return swapPiece(5, 1) || swapPiece(5, 4) || swapPiece(5, 6) || swapPiece(5, 9);
        }
        else if(square == 6) {
            return swapPiece(6, 2) || swapPiece(6, 5) || swapPiece(6, 7) || swapPiece(6, 10);
        }
        else if(square == 7) {
            return swapPiece(7, 3) || swapPiece(7, 11) || swapPiece(7, 6);
        }
        else if(square == 8) {
            return swapPiece(8, 4) || swapPiece(8, 12) || swapPiece(8, 9);
        }
        else if(square == 9) {
            return swapPiece(9, 8) || swapPiece(9, 10) || swapPiece(9, 5) || swapPiece(9, 13);
        }
        else if(square == 10) {
            return swapPiece(10, 9) || swapPiece(10, 11) || swapPiece(10, 6) || swapPiece(10, 14);
        }
        else if(square == 11) {
            return swapPiece(11, 15) || swapPiece(11, 10) || swapPiece(11, 7);
        }
        else if(square == 12) {
            return swapPiece(12, 8) || swapPiece(12, 13);
        }
        else if(square == 13) {
            return swapPiece(13, 12) || swapPiece(13, 14) || swapPiece(13, 9);
        }
        else if(square == 14) {
            return swapPiece(14, 13) || swapPiece(14, 10) || swapPiece(14, 15);
        }
        else {
            return swapPiece(15, 14) || swapPiece(15, 11);
        }
    }
    
    //If the empty square is next to the target swap the pieces
    private boolean swapPiece(int target, int check){
        if (grid[getRow(check)][getCol(check)] == 0){
            grid[getRow(check)][getCol(check)] = grid[getRow(target)][getCol(target)];
            grid[getRow(target)][getCol(target)] = 0;
            checkForWin();
            return true;
        }
        return false;
    }
    
    //Checks to see if the puzzle is complete
    public boolean checkForWin() {
        int i = 1;
        for (int r = 0; r <= 3; r++){
            for (int c = 0; c <= 3; c++){
                if (r == 3 && c == 3) {
                    break;
                }
                if (grid[r][c] != i) {
                    return false;
                }
                i++;
            }
        }
        repaint();
        JOptionPane.showMessageDialog(this, "Congratulations, You Win!!!");
        return true;
    }
    
    //Draw the puzzle pieces on the board
    public void paint(Graphics g) {
        
        if (clearBoard) {
            int th = this.getHeight();
            int tw = this.getWidth();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, tw, th);
            clearBoard = false;
        }
        
        this.paintComponents(g);
        for (int r = 0; r <= 3; r++) {
            for (int c = 0; c <= 3; c++){
                if (grid[r][c]==0) {  //The square is empty.
                    g.setColor(Color.GRAY);
                    g.fillRect(w * c + 50, h * r + 50, w, h);
                }
                else 
                {
                    g.drawImage(imgParts[(grid[r][c]) - 1], w * c + 50, h * r + 50, null);
                }
            }
        }
        
        //Draw lines on the board
        for (int i = 0; i <= 4; i++){
            g.setColor(Color.black);
            g.drawLine(50, i * h + 50, w * 4 + 50, i * h + 50);
            g.drawLine(i * w + 50, 50, i * w + 50, (h * 4) + 50);
            
        }
        
    }

    //Button actions
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        System.out.println(cmd);
        if (cmd.equals("Import Image")) {
            int returnVal = importImage.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = importImage.getSelectedFile();
                importImage(file);
                System.out.println("Opening: " + file.getName() + ".");
            } else {
                System.out.println("Open command cancelled by user.");
            }
        }
        if (cmd.equals("Shuffle")) {
            shuffle();
        }
        if (cmd.equals("Solve Puzzle")) {
            sort();
        }
        
    }

    // Select piece where mouse is clicked
    @Override
    public void mouseClicked(MouseEvent e) {
        if (isValidPoint(e.getPoint())){
            if (selectSquare(getNumAt(getSquareAt(e.getPoint())))){
                repaint();
            }
        }
    }
    
    
    @Override
    public void mouseEntered(MouseEvent arg0) {}
    @Override
    public void mouseExited(MouseEvent arg0) {}
    @Override
    public void mousePressed(MouseEvent arg0) {}
    @Override
    public void mouseReleased(MouseEvent arg0) {}
    
    
}
