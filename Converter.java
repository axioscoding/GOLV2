import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Converter {

    
    public static final Pattern fromRLE(String rle, int dimX, int dimY, String name){

        rle = rle.replace("b", ".");
        rle = rle.replace("o", "O");

        String[] lines = rle.split("\\$");
        ArrayList<String> finalList = new ArrayList<>();
        for(int i = 0; i < lines.length; i++){
            
            String[] s = decode(lines[i], dimX);
            for(int j = 0; j < s.length; j++){
                if(s[j].length() != dimX){
                    while(s[j].length() < dimX){
                        s[j] += ".";
                    }
                }
                finalList.add(s[j]);
            }
            
        }


        String out = "";
        for(int i = 0; i < finalList.size(); i++){
            out += finalList.get(i);
        }
        return fromPlainText(out, dimX, dimY, name);
    }




    /////// FULL FILE CONVERTER
    public static final Pattern fromRLE(String input){

        String name = "";
        int dimX = 0;
        int dimY = 0;

        input = input.trim();
        String[] parts = input.split("\n");

        if(!parts[0].startsWith("#")) throw new IllegalArgumentException("Wrong Format 1!");

        int iterator = 0;
        while(parts[iterator].startsWith("#")){
            if(parts[iterator].startsWith("#N")){
                String[] nameString = parts[iterator].split(" ");
                StringBuilder sb = new StringBuilder();
                for(int i = 1; i < nameString.length; i++){
                    
                    if(i != nameString.length - 1){
                        sb.append(nameString[i].toUpperCase());
                        sb.append("_");
                    }else{
                        sb.append(nameString[i].toUpperCase());
                    }
                }
                name  = sb.toString();
            }
            iterator++;
        }

        if(!(parts[iterator].contains("x") && parts[iterator].contains("y"))) throw new IllegalArgumentException("Wrong Format 2!");

        //if(!parts[iterator].contains("B3/S23")) throw new IllegalArgumentException("Wrong Rule!");

        String[] dimString = parts[iterator].replaceAll("\\s","").split("=");

        try {
            for(int i = 0; i < dimString.length; i++){
                if(dimString[i].contains("x")){
                    dimX = Integer.valueOf(dimString[i+1].split(",")[0]);
                }
    
                if(dimString[i].contains("y")){
                    dimY = Integer.valueOf(dimString[i+1].split(",")[0]);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Wrong dimension format!");
        }

        if((dimX >= GameOfLife.WIDTH / GameOfLife.CELL_SIZE - 20) || dimY >= GameOfLife.HEIGHT / GameOfLife.CELL_SIZE - 20)
            throw new IllegalArgumentException("Too Big!");


        iterator++;

        String rle = "";

        StringBuilder sb2 = new StringBuilder();

        for(int i = iterator; i < parts.length; i++){
            sb2.append(parts[i]);
        }

        rle = sb2.toString();
        rle = rle.replace("!", "");
        rle = rle.replaceAll("\\s","");
        rle = rle.replace("b", ".");
        rle = rle.replace("o", "O");

        String[] lines = rle.split("\\$");
        String[] last = lines[lines.length - 1].split("!");
        lines[lines.length - 1] = last[0];
        ArrayList<String> finalList = new ArrayList<>();
        for(int i = 0; i < lines.length; i++){
            
            String[] s = decode(lines[i], dimX);
            for(int j = 0; j < s.length; j++){
                if(s[j].length() != dimX){
                    while(s[j].length() < dimX){
                        s[j] += ".";
                    }
                }
                finalList.add(s[j]);
            }
            
        }


        String out = "";
        for(int i = 0; i < finalList.size(); i++){
            out += finalList.get(i);
        }
        return fromPlainText(out, dimX, dimY, name);
    }



    public static final Pattern fromPlainText(String text, int dimX, int dimY, String name){
        text  = text.trim();
        boolean[] matrix = new boolean[text.length()];
        for(int y = 0; y < dimY; y++){
            for(int x = 0; x < dimX; x++){
                if(text.charAt(y * dimX + x) == 46)
                    matrix[y * dimX + x] = false;
                
                if(text.charAt(y * dimX + x) == 79)
                    matrix[y * dimX + x] = true;
                
            }
        }


        

        // System.out.println(
        //     "public static final Pattern name = new Pattern(" + dimX + ", " + 
        //     dimY + ", new boolean[] {" + Arrays.toString(matrix).replaceAll("[\\[\\]]", "") + "});"
        // );

        return new Pattern(dimX, dimY, name, matrix);
    }

    public static String[] decode(final String st, int w) {
        final StringBuilder sb = new StringBuilder();
        ArrayList<String> items = new ArrayList<>();
    
        final char[] chars = st.toCharArray();
    
        int i = 0;
        //System.out.println("---------");
        while (i < chars.length) {
            int repeat = 0;
            while ((i < chars.length) && Character.isDigit(chars[i])) {
                repeat = repeat * 10 + chars[i++] - '0';
            }
            //if(repeat > 0 && i == chars.length) System.out.println("HELP!");
            final StringBuilder s = new StringBuilder();
            boolean next = false;
            while ((i < chars.length) && !Character.isDigit(chars[i]) && !next) {
                next = true;
                s.append(chars[i++]);
            }

            
    
            if (repeat > 0) {
                for (int j = 0; j < repeat; j++) {
                    sb.append(s.toString());
                }
            } else {
                sb.append(s.toString());
            }
            

            if(i == chars.length && repeat > 0 && s.isEmpty()){
                System.out.println(repeat);
                
                for(int j = 0; j < repeat - 1; j++){
                    StringBuilder sb2 = new StringBuilder();
                    for(int k = 0; k < w; k++){
                        sb2.append(".");
                    }
                    items.add(sb2.toString());
                }
            }

        }

        items.add(0, sb.toString());

        String[] result = new String[items.size()];
        for(int d = 0; d < items.size(); d++){
            result[d] = items.get(d);
        }
    
        return result;
    }


}


