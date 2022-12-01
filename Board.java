import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.HashSet;



public class Board {

    private byte[] cells;
    private int width, height, length;

    private int count=0, max_count=0;

    private int gens = 0;

    private HashSet<Tupel> drawable;

    private String lastPatternName = "";


    //--------LOAD PATTERNS---------
    // example: Presets.stresstest(this) or loadPattern(Pattern)
    private void load(){

        Presets.stresstest(this);
        
    }





    public Board(){
        width = GameOfLife.WIDTH / GameOfLife.CELL_SIZE;
        height = GameOfLife.HEIGHT / GameOfLife.CELL_SIZE;
        length = width * height;

        drawable = new HashSet<>();

        cells = new byte[length];

        load();
       
    }

    public void newRandomPattern(){
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                cells[j*width+i] = 0x00;
            }
        }
        loadPattern(new Pattern(width / 2 , height / 2));
    }

    public void newPattern(Pattern p){
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                cells[j*width+i] = 0x00;
            }
        }
        loadPattern(p);
    }


    public void update(int deltaTime){
		nextGen();
	}
	

    public void render(byte[] pixels){

        for(int i = 0; i + 3 < pixels.length; i+= 4){
            pixels[i+1] = (byte) ((pixels[i] & 0x00) << 16);
        }

    }
	
	public void render2(Graphics2D g){

        //long before  = System.nanoTime();

        count = 0;

        if(GameOfLife.GRID && GameOfLife.CELL_SIZE >= 5){
            for(int x = 0; x < width; x++){
                g.setColor(Color.WHITE);
                g.drawLine(x * GameOfLife.CELL_SIZE, 0, x * GameOfLife.CELL_SIZE, GameOfLife.HEIGHT);
                
            }

            for(int y = 0; y < height; y++){
                g.setColor(Color.WHITE);
                g.drawLine(0, y * GameOfLife.CELL_SIZE, GameOfLife.WIDTH, y * GameOfLife.CELL_SIZE);
            }
        }

		for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                if(cellState(x, y)){
                    if(GameOfLife.COLORS){
                        if(GameOfLife.RAND_COLORS){
                            g.setColor(new Color(Color.HSBtoRGB((((float)y/(float)height)/2 + ((float)x/(float)width)/2) * (float)(Math.random() * 0.8f + 0.2) , 1.0f, GameOfLife.INVERTED_COLORS ? 0.8f : 1.0f)));
                        }else{
                            g.setColor(new Color(Color.HSBtoRGB((((float)y/(float)height)/2 + ((float)x/(float)width)/2) , 1.0f, (GameOfLife.INVERTED_COLORS ? 0.8f : 1.0f))));
                        }
                    }else{
                        if(GameOfLife.INVERTED_COLORS) g.setColor(Color.BLACK);
                        else g.setColor(Color.WHITE);
                    }
                        
                    g.fillRect(x * GameOfLife.CELL_SIZE,
                    y * GameOfLife.CELL_SIZE,
                    GameOfLife.CELL_SIZE,
                    GameOfLife.CELL_SIZE);
                    count++;
                }
            }
        }

        if(count > max_count) max_count = count;


        g.setFont(GameOfLife.arial);
        g.setColor(Color.ORANGE);
        g.drawString("Active: " + count + "", 10, 170);
        g.setColor(Color.GREEN);
        g.drawString("MaxActive: "+ max_count + "", 10, 200);
        g.setColor(Color.WHITE);
        g.drawString(lastPatternName, 10, 50);
        g.setColor(Color.WHITE);
		g.drawString("Gens: " + gens + "", 10, 140);


        // for (Tupel tupel : drawable) {
        
        //     g.setColor(Color.WHITE);
        //     g.fillRect(tupel.getX() * GameOfLife.CELL_SIZE, tupel.getY() * GameOfLife.CELL_SIZE, GameOfLife.CELL_SIZE, GameOfLife.CELL_SIZE);
        // }

        //System.out.println((System.nanoTime() - before) / 10000);

    
	}

    private void loadPattern(Pattern pattern){
        gens = 0;
        try {
            int centerx = width/2 - pattern.width/2;
            int centery = height/2 - pattern.height/2;
            for(int x = 0; x < pattern.width; x++){
                for(int y = 0; y < pattern.height; y++){
                    if(pattern.matrix[y*pattern.width + x]){
                        setCell(centerx + x, centery + y);
                        //drawCell(centerx + x, centery + y);
                    }
                        
                }

            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Out of bounds!");
        }
        lastPatternName = pattern.getName();
    }


    private void loadPattern(Pattern pattern, int offsX, int offsY){
        gens = 0;
        try {
            int centerx = offsX - pattern.width/2;
            int centery = offsY - pattern.height/2;
            for(int x = 0; x < pattern.width; x++){
                for(int y = 0; y < pattern.height; y++){
                    if(pattern.matrix[y*pattern.width + x]){
                        setCell(centerx + x, centery + y);
                        //drawCell(centerx + x, centery + y);
                    }
                        
                }

            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Out of bounds!");
        }
        lastPatternName = pattern.getName();
    }

    public void loadClipboardPatternRLE(String s){
        newPattern(Converter.fromRLE(s));
    }

    public void saveLoadClipboardPatternRLE(String s){
        Pattern loaded = Converter.fromRLE(s);
        Serializer.saveToFile(loaded);
        newPattern(loaded);
    }

    public void loadFromDrop(String s){
        newPattern(Serializer.loadFromFile(s));
    }

    private void nextGen(){

        byte[] prevCells = Arrays.copyOf(cells, cells.length);
        int live_neighbours = 0;

        for(int y = 0; y < height; y++){
            int x = 0;
            do {
                while(prevCells[y*width+x]==0){
                    if(++x >= width) break;
                }

                if(x >= width) break;

                live_neighbours = prevCells[y*width+x] >> 1;

                if((prevCells[y*width+x] & 0x01) == 1){
                        if((live_neighbours != 2) && (live_neighbours != 3)){
                       
                            clearCell(x, y);
                            //undrawCell(x, y);
                        }
                }else{
                        if(live_neighbours == 3){
                          
                            setCell(x, y);
                            //drawCell(x, y);
                            
                        }
                }

            } while (++x < width);
        }
        gens++;
    }

    private void drawCell(int x, int y){
        drawable.add(new Tupel(x, y));
    }

    private void undrawCell(int x, int y){
        drawable.removeIf(t -> (t.getX() == x && t.getY() == y));
    }


    private void setCell(int x, int y){
        
        int pos = y*width + x;
    
        this.cells[pos] |= 0x01;
        int xl = -1, xr = 1, ya = -width, yb = width;




        if(x <= 0){
            xl = width - 1;
        }else if(x >= width - 1){
            xr = -(width - 1);
        }

        if(y <= 0){
            ya = length - width;
        }else if(y >= height - 1){
            yb = -(length - width);
        }

     

        if(GameOfLife.BOARD_WRAP_AROUND){
            this.cells[pos + xr + yb] += 0x02;
            this.cells[pos + yb] += 0x02;
            this.cells[pos + xl + yb] += 0x02;
            this.cells[pos + xl] += 0x02;
            this.cells[pos + xl + ya] += 0x02;
            this.cells[pos + ya] += 0x02;
            this.cells[pos + xr + ya] += 0x02;
            this.cells[pos + xr] += 0x02;
        }else{
            if(x <= 0){
                this.cells[pos + ya] += 0x02;
                this.cells[pos + xr + ya] += 0x02;
                this.cells[pos + xr] += 0x02;
                this.cells[pos + xr + yb] += 0x02;
                this.cells[pos + yb] += 0x02;
            }else if(x >= width - 1){
                this.cells[pos + yb] += 0x02;
                this.cells[pos + xl + yb] += 0x02;
                this.cells[pos + xl] += 0x02;
                this.cells[pos + xl + ya] += 0x02;
                this.cells[pos + ya] += 0x02;
            }else if(y <= 0){
                this.cells[pos + xr] += 0x02;
                this.cells[pos + xr + yb] += 0x02;
                this.cells[pos + yb] += 0x02;
                this.cells[pos + xl + yb] += 0x02;
                this.cells[pos + xl] += 0x02;
            }else if(y >= height - 1){
                this.cells[pos + xl] += 0x02;
                this.cells[pos + xl + ya] += 0x02;
                this.cells[pos + ya] += 0x02;
                this.cells[pos + xr + ya] += 0x02;
                this.cells[pos + xr] += 0x02;
            }else{
                this.cells[pos + xr + yb] += 0x02;
                this.cells[pos + yb] += 0x02;
                this.cells[pos + xl + yb] += 0x02;
                this.cells[pos + xl] += 0x02;
                this.cells[pos + xl + ya] += 0x02;
                this.cells[pos + ya] += 0x02;
                this.cells[pos + xr + ya] += 0x02;
                this.cells[pos + xr] += 0x02;
            }
        }
        


    }

    private void clearCell(int x, int y){
        int pos = y*width + x;
        this.cells[pos] &= ~0x01;

        int xl = -1, xr = 1, ya = -width, yb = width;

        if(x <= 0){
            xl = width - 1;
        }

        if(x >= width -1){
            xr = -(width - 1);
        }

        if(y <= 0){
            ya = length - width;
        }

        if(y >= height- 1){
            yb = -(length - width);
        }


        if(GameOfLife.BOARD_WRAP_AROUND){
            this.cells[pos + xr + yb] -= 0x02;
            this.cells[pos + yb] -= 0x02;
            this.cells[pos + xl + yb] -= 0x02;
            this.cells[pos + xl] -= 0x02;
            this.cells[pos + xl + ya] -= 0x02;
            this.cells[pos + ya] -= 0x02;
            this.cells[pos + xr + ya] -= 0x02;
            this.cells[pos + xr] -= 0x02;
        }else{
            if(x <= 0){
                this.cells[pos + ya] -= 0x02;
                this.cells[pos + xr + ya] -= 0x02;
                this.cells[pos + xr] -= 0x02;
                this.cells[pos + xr + yb] -= 0x02;
                this.cells[pos + yb] -= 0x02;
            }else if(x >= width - 1){
                this.cells[pos + yb] -= 0x02;
                this.cells[pos + xl + yb] -= 0x02;
                this.cells[pos + xl] -= 0x02;
                this.cells[pos + xl + ya] -= 0x02;
                this.cells[pos + ya] -= 0x02;
            }else if(y <= 0){
                this.cells[pos + xr] -= 0x02;
                this.cells[pos + xr + yb] -= 0x02;
                this.cells[pos + yb] -= 0x02;
                this.cells[pos + xl + yb] -= 0x02;
                this.cells[pos + xl] -= 0x02;
            }else if(y >= height - 1){
                this.cells[pos + xl] -= 0x02;
                this.cells[pos + xl + ya] -= 0x02;
                this.cells[pos + ya] -= 0x02;
                this.cells[pos + xr + ya] -= 0x02;
                this.cells[pos + xr] -= 0x02;
            }else {
                this.cells[pos + xr + yb] -= 0x02;
                this.cells[pos + yb] -= 0x02;
                this.cells[pos + xl + yb] -= 0x02;
                this.cells[pos + xl] -= 0x02;
                this.cells[pos + xl + ya] -= 0x02;
                this.cells[pos + ya] -= 0x02;
                this.cells[pos + xr + ya] -= 0x02;
                this.cells[pos + xr] -= 0x02;
            }
        }
    }

    private boolean cellState(int x, int y){
        return (cells[y * width + x] & 0x01) == 1;
    }




    class Presets{
        static final void stresstest(Board b){
            b.loadPattern(PatternBank.HALFMAX.flip(Pattern.Dir.HORIZONTAL), b.width/4, 50);
            b.loadPattern(PatternBank.HALFMAX, b.width/4 * 3, b.height - 50);
        }

        static final void heptomino_swarm(Board b){
            for(int x = 0; x < 20; x++){
                for(int y = 0; y < 10; y++){
                    b.loadPattern(PatternBank.E_HEPTOMINO, (int)(b.width*((double)x/20)), (int)(b.height*((double)y/10)));
                }
            }
        }

        static final void rand(Board b, int x){
            b.loadPattern(new Pattern(x, x));
        }

        static final void pufferCrash(Board b){
            b.loadPattern(PatternBank.BOATSTRETCHER, b.width/3, b.height/2);

            b.loadPattern(PatternBank.BOATSTRETCHER.flip(Pattern.Dir.DIAGONAL), b.width/3 * 2, b.height/2 + 100);
    
            b.loadPattern(PatternBank.GLIDER_TRAIN);
        }
    
    
    }

    
}


class Tupel{

    private int x, y;

    public Tupel(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){return x;}
    public int getY(){return y;}




}