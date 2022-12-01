import javax.swing.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.awt.datatransfer.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Arrays;
import java.util.Objects;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;

public class GameOfLife implements Runnable, KeyListener, DropTargetListener{
    
    public static final int WIDTH = 1300/2;
	public static final int HEIGHT = 650/2;
	public static final int CELL_SIZE = 1;

	public static final boolean BOARD_WRAP_AROUND = true;
	
	//grid only active when CELL_SIZE > 2
	public static final boolean GRID = false;
	public static final long DESIRED_FPS = 1000;

	//>=0 : limit frames
	public static final int FRAMES = -1;

	//false = black and white
	public static final boolean COLORS = true;
	public static final boolean RAND_COLORS = false;
	public static final boolean INVERTED_COLORS = false;


	private JFrame frame;
	private Canvas canvas;
	private BufferStrategy bufferStrategy;
	private boolean running = true;

	private Board board;
	private int count = 0;

	private int steps = 0;

	private int fps = 0;
	private int low_fps = 1000000;
	private long fps_last = System.nanoTime();

	public static final Font arial = new Font("Arial", Font.PLAIN, 20);

	private DropTarget dt;

	private long desiredDeltaLoop = (1000*1000*1000)/DESIRED_FPS;

	public GameOfLife(){
		frame = new JFrame("Game Of Life");
		frame.setFocusable(true);
		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(null);
		canvas = new Canvas();
		canvas.addKeyListener(this);
		canvas.setBounds(0, 0, WIDTH, HEIGHT);
		canvas.setIgnoreRepaint(true);
		panel.add(canvas);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();
		canvas.requestFocus();
		dt = new DropTarget(canvas, this);
		board = new Board();
	}

    
    

	
	public void run(){
		long beginLoopTime;
		long endLoopTime;
		long currentUpdateTime = System.nanoTime();
		long lastUpdateTime;
		long deltaLoop;

		while(running){
			beginLoopTime = System.nanoTime();
			
			render();
			
			lastUpdateTime = currentUpdateTime;
			currentUpdateTime = System.nanoTime();

			update((int) ((currentUpdateTime - lastUpdateTime)/(1000*1000)));
			
			endLoopTime = System.nanoTime();
			deltaLoop = endLoopTime - beginLoopTime;
			
	        if(deltaLoop <= desiredDeltaLoop){
	            try{
	                Thread.sleep((desiredDeltaLoop - deltaLoop)/(1000*1000));
	            }catch(InterruptedException e){
                    System.err.println(e);
	            }
	        }
			if(System.nanoTime() - fps_last > 50000000){
				fps = (1000*1000*1000)/(int)(System.nanoTime() - beginLoopTime);
				fps_last = System.nanoTime();
				if(fps < low_fps && steps > 2) low_fps = fps;
			}

			if(FRAMES == count) running = false;
			if(FRAMES >= 0) count++;
            
			steps++;
		}
	}
	
	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

		if(INVERTED_COLORS) g.setColor(Color.WHITE);
		else g.setColor(Color.BLACK);
		
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		render(g);

		g.setFont(arial);
		g.setColor(Color.CYAN);
		g.drawString("FPS: " + fps + "", 10, 80);
		g.setColor(Color.RED);
		g.drawString("LowesFps: " + low_fps + "", 10, 110);

		g.dispose();
		bufferStrategy.show();
	}
	

	protected void update(int deltaTime){
		board.update(deltaTime);
	}
	
	protected void render(Graphics2D g){
		board.render2(g);
	}
	
	public static void main(String [] args){
		GameOfLife ex = new GameOfLife();
		new Thread(ex).start();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_SPACE){
			board.newRandomPattern();
		}

		if(code == KeyEvent.VK_L){
			Clipboard c=Toolkit.getDefaultToolkit().getSystemClipboard();
			try {
			String k = (String) c.getData(DataFlavor.stringFlavor);

			board.loadClipboardPatternRLE(k);
				
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		}

		if(code == KeyEvent.VK_S){
			Clipboard c=Toolkit.getDefaultToolkit().getSystemClipboard();
			try {
			String k = (String) c.getData(DataFlavor.stringFlavor);

			board.saveLoadClipboardPatternRLE(k);
				
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		try {
			Transferable tr = dtde.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++) {

			  	if (flavors[i].isFlavorJavaFileListType()) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY);


					java.util.List<Object> ar =  Arrays.asList(tr.getTransferData(flavors[i]));

					board.loadFromDrop(Objects.toString(ar.get(0)).replaceAll("[\\[\\]]", ""));
					
					dtde.dropComplete(true);
					return;
				}
			}
			System.out.println("Drop failed: " + dtde);
			dtde.rejectDrop();
		  } catch (Exception e) {
			e.printStackTrace();
			dtde.rejectDrop();
		  }
		
		
	}

}

