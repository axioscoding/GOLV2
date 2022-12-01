import java.io.Serializable;
import java.util.Arrays;

public class Pattern implements Serializable {
    
    public final int width, height;
    public final boolean[] matrix;

    public final String name;

    public enum Dir{
        HORIZONTAL,
        VERTICAL,
        DIAGONAL,
        DIAGONAL2
    }

    public Pattern(int width, int height, String name, boolean[] matrix){
        this.width = width;
        this.height = height;
        this.matrix = matrix;
        this.name = name;
    }

    public Pattern(int width, int height){
        this.width = width;
        this.height = height;
        this.name = "RANDOM";
        matrix = new boolean[width*height];
        for(int i = 0; i < width*height; i++){
            if(Math.random() > 0.5){
                matrix[i] = true;
            }
        }
    }

    public Pattern flip(Dir d){
        boolean[] temp = Arrays.copyOf(matrix, matrix.length);
        switch(d){
            case HORIZONTAL:
            for(int i = 0; i < matrix.length; i++){
                temp[i] = matrix[matrix.length - 1 - i];
            }
            return new Pattern(width, height, name, temp);
            case VERTICAL:
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        temp[y*width+x] = matrix[y*width+(width-1-x)];
                    }
                }
                return new Pattern(width, height, name, temp);

            case DIAGONAL:
                boolean[][] temp2 = new boolean[height][width];
                boolean[][] temp3 = new boolean[width][height];
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        temp2[y][x] = temp[y*width+x];
                    }
                }
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        temp3[x][y] = temp2[y][width - 1 - x];
                    }
                }
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        temp[x*height+y] = temp3[x][y];
                    }
                }
                return new Pattern(height, width, name, temp);

            case DIAGONAL2:
                boolean[][] temp4 = new boolean[height][width];
                boolean[][] temp5 = new boolean[width][height];
                boolean[] temp6;
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        temp4[y][x] = temp[y*width+x];
                    }
                }
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        temp5[x][y] = temp4[y][width - 1 - x];
                    }
                }
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        temp[x*height+y] = temp5[x][y];
                    }
                }
                temp6 = Arrays.copyOf(temp, temp.length);
                for(int i= 0; i < temp.length; i++){
                    temp[i] = temp6[temp6.length - 1- i];
                }
                return new Pattern(height, width, name, temp);


            default:
                return this;
        }
    }


    public String getName(){return this.name;}
    
    
}

