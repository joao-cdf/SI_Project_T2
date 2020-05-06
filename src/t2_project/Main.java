package t2_project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import static t2_project.DB.*;
import static t2_project.Lib.*;
import static t2_project.Nova.funcaoNova;


public class Main {
    
       
    public static void mainMenu() throws NoSuchAlgorithmException, SQLException, ClassNotFoundException, IOException, Exception   {
        
        int option;
            
        while(true) {
            
            System.out.println("+--------------+");
            System.out.println("| Estou a ver! |");
            System.out.println("+--------------+");

            System.out.println ("1. Entrar");
            System.out.println ("2. Registar");
            System.out.println ("3. Sair");
            option = Ler.umInt();
    
            switch(option)  {
                case 1: {
                    Login();
                    break;
                }
                case 2: {
                    Registar();               
                    break;
                }
                case 3: {
                    System.out.println("Aplicação terminada");
                    System.exit(0);
                    break;
                }
                default:    {
                    System.out.println("Opção inválida!");
                    break;
                }
            }
        }
        

    }
    
    public static void Login() throws ClassNotFoundException, ClassNotFoundException, SQLException, NoSuchAlgorithmException, IOException, Exception  {
        
        Connection con = getConnUser();
        Statement st = null;       
        
        String name;
        String password;
        
        //user input
        do  {
            
            System.out.println ("\nUsername: ");
            name = Ler.umaString();
            
            password = Ler.readPassword("\nPassword:\n");
            if( password.length() < 4 || password.length() > 16 )
                System.out.println("Palavra-chave inválida!\nA palavra-chave deve conter entre 4 a 16 caracteres");
        
        } while( password.length() < 4 || password.length() > 16 );
            
        //getting values from db
        String sql ="SELECT * FROM User WHERE name = '" + name + "'";
        st = con.createStatement();        
        
        try {
                    
            ResultSet rs = st.executeQuery(sql);
                
            String salt = rs.getString(2);
            String rep = rs.getString(3);
        
            rs.close();
            st.close();
            con.close();
        
            //cifra password
            password = getSHA(password);       
            String input_rep = salt+password;        
            input_rep = getSHA(input_rep);

            //check if it's equal
            if(input_rep.equals(rep))   {
                System.out.println("Login efetuado com sucesso!\n");        
                funcaoNova(name);
            }
            else    {
                System.out.println("Palavra chave errada!\n");
            }
        } catch (SQLException e)    {
            System.out.println("Utilizador não registado!");
        }    
        
    }
    
    public static void Registar() throws NoSuchAlgorithmException, SQLException, ClassNotFoundException   {
        
        Connection con = getConnUser();
        Statement st = null;
        String name;
        String password;
        String pass;
        byte[] salt;
        String ssalt;
        
        //user input
        System.out.println ("Username: ");
        name = Ler.umaString();        
        
        do  {
            
            password = Ler.readPassword("\nPassword:\n");
            if( password.length() < 4 || password.length() > 16 )
                System.out.println("Palavra-chave inválida!\nA palavra-chave deve conter entre 4 a 16 caracteres");
        
        } while( password.length() < 4 || password.length() > 16 );
        
        //cifra password
        pass = getSHA(password);    

        salt = getSalt();
        ssalt = bytesToHex(salt);
        
        String rep = ssalt+pass;     
        rep = getSHA(rep);
                
        //insere na db       
        String sql = "INSERT INTO User VALUES ('"+name+"' , '"+ssalt+"' , '"+rep+"')";
        st = con.createStatement();
        try{
            
            st.executeUpdate(sql);
            System.out.println("Registado com sucesso!");
            
        } catch (SQLException e)    {
            System.out.println("Utilizador já existe!\n");
        }
        
        st.close();                
        con.close();
        
    }

    public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, SQLException, ClassNotFoundException, IOException, Exception {
        
        createTableUser();
        mainMenu();
        
    }




}
