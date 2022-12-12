import Board.ChessBoard;
import GUI.ChessBoardJPanel;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

public class ChessApp extends JFrame implements ActionListener{
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    JMenuItem fileMenu_new,fileMenu_open, fileMenu_save, fileMenu_exit, optionsMenu_toggleTiles;
    ChessBoardJPanel chessBoard;
    private JFileChooser fc;

    public static void main(String[] args) {
        ChessApp chess = new ChessApp();
    }

    public ChessApp(){
        super("Chess");
        setMinimumSize(new Dimension(300, 300));
        setLayout(new GridBagLayout());
        try
        {
            setIconImage(ImageIO.read(new File("chess.png")));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        chessBoard = new ChessBoardJPanel();
        add(chessBoard);
        addMenuBar();

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack();
        setVisible(true);
    }

    public void addMenuBar()
    {
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu_new = new JMenuItem("New");
        fileMenu_new.addActionListener(this);
        fileMenu_new.setMnemonic(KeyEvent.VK_N);

        fileMenu_open = new JMenuItem("Open");
        fileMenu_open.addActionListener(this);
        fileMenu_open.setMnemonic(KeyEvent.VK_O);

        fileMenu_save = new JMenuItem("Save");
        fileMenu_save.addActionListener(this);
        fileMenu_save.setMnemonic(KeyEvent.VK_S);

        fileMenu_exit = new JMenuItem("Exit");
        fileMenu_exit.addActionListener(this);
        fileMenu_exit.setMnemonic(KeyEvent.VK_X);

        fileMenu.add(fileMenu_new);
        fileMenu.add(fileMenu_open);
        fileMenu.add(fileMenu_save);
        fileMenu.addSeparator();
        fileMenu.add(fileMenu_exit);

        bar.add(fileMenu);

        JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic(KeyEvent.VK_P);

        optionsMenu_toggleTiles = new JMenuItem("Toggle Tile Colors");
        optionsMenu_toggleTiles.addActionListener(this);
        optionsMenu_toggleTiles.setMnemonic(KeyEvent.VK_T);

        optionsMenu.add(optionsMenu_toggleTiles);

        bar.add(optionsMenu);
        setJMenuBar(bar);
    }

    public void exit() {
        dispose();
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource() == fileMenu_new ){
            remove(chessBoard);
            chessBoard = new ChessBoardJPanel(new ChessBoard());
            add(chessBoard);
            validate();
        }
        else if ( e.getSource() == fileMenu_open ){
            ChessBoard chess = openBoard();
            if (chess != null)
            {
                remove(chessBoard);
                chessBoard = new ChessBoardJPanel(chess);
                add(chessBoard);
                validate();
            }
        }
        else if ( e.getSource() == fileMenu_save ){
            saveChessBoardToFile();
        }
        else if ( e.getSource() == fileMenu_exit ){
            exit();
        }

        if ( e.getSource() == optionsMenu_toggleTiles ){
            toggleTiles();
        }
    }

    public void toggleTiles()
    {
        chessBoard.toggleLegalTiles();
    }

    public ChessBoard openBoard()
    {
        ChessBoard chess = new ChessBoard();
        fc = new JFileChooser(Paths.get("").toAbsolutePath().toFile()); //the directory of the app
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String fileName = fc.getSelectedFile().getAbsolutePath();

            try{
                FileInputStream fin = new FileInputStream(fileName);
                ObjectInputStream oin = new ObjectInputStream(fin);
                Object o;

                o = oin.readObject();
                if (o.getClass().equals(ChessBoard.class)){
                    chess = (ChessBoard) o;
                }
            }
            catch(Exception e){
                System.out.println(e.toString());
            }

            return chess;
        }
        else
        {
            //player exited the file choose and didn't open a file
            return null;
        }
    }

    private void saveChessBoardToFile()
    {
        ChessBoard board = chessBoard.getBoard();
        //saves the chessboard as the current date/time
        String fileName = String.format("[%s] Chess.ser",
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss")));

        try{
            FileOutputStream fout = new FileOutputStream(fileName);
            ObjectOutputStream oout = new ObjectOutputStream(fout);

            oout.writeObject(board);
            JOptionPane.showMessageDialog(this, "Game Saved.");
        }
        catch(IOException e){
            System.out.println(e.toString());

            JOptionPane.showMessageDialog(this, e.toString());
        }
    }

}
