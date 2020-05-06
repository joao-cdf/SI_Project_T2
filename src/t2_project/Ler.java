package t2_project;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Ler {
    public static String umaString() {
        String s= "";
        try{
            BufferedReader in = new BufferedReader ( new InputStreamReader (System.in));
            s=in.readLine();
        }
        catch (IOException e) {
            System.out.println("Erro ao ler fluxo de entrada.");
        }
        return s;
    }

    public static int umInt() {
        while(true) {
            try {
                return Integer.valueOf (umaString().trim()).intValue();
            }
            catch(NumberFormatException e){
                System.out.println("Não é um inteiro válido!!!");
            }
        }
    }

        public static Double umDouble() {
        while(true) {
            try {
                return Double.valueOf (umaString().trim()).doubleValue();
            }
            catch(NumberFormatException e){
                System.out.println("Não é um double válido!!!");
            }
        }
        }

        public static Float umFloat() {
        while(true) {
            try {
                return Float.valueOf (umaString().trim()).floatValue();
            }
            catch(NumberFormatException e){
                System.out.println("Não é um float válido!!!");
            }
        }
        }
        public static boolean umBoolean() {
        while(true) {
            try {
                return Boolean.valueOf (umaString().trim()).booleanValue();
            }
            catch(Exception e){
                System.out.println("Não é um boolean válido!!!");
            }
        }
    }
        public static char umChar() {
        while(true) {
            try {
                return umaString().trim().charAt(0);
            }
            catch(Exception e){
                System.out.println("Não é um char válido!!!");
            }
        }
    }
        public static long umLong() {
        while(true) {
            try {
                return Long.valueOf (umaString().trim()).longValue();
            }
            catch(NumberFormatException e){
                System.out.println("Não é um long válido!!!");
            }
        }
    }
        
    public static String readPassword (String prompt) {
        
      EraserThread et = new EraserThread(prompt);
      Thread mask = new Thread(et);
      mask.start();

      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      String password = "";

      try {
         password = in.readLine();
      } catch (IOException ioe) {
      }
      et.stopMasking();
      return password;
   }    
}



