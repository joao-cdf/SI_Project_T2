package t2_project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;




public class DB {
    
         
    public static Connection getConnUser() throws ClassNotFoundException, SQLException  {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:User.db");
            
        }catch(Exception e)    {
            System.err.println(e.getClass().getName() + ": " + e.getMessage() );
        }
        return con;
    }
    
    public static Connection getConnDir(String nome) throws ClassNotFoundException, SQLException, FileNotFoundException, IOException, Exception  {                                
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:"+ nome +".db");            
        }catch(ClassNotFoundException | SQLException e)    {
              System.out.println(e.getClass().getName() + ": " + e.getMessage() );
        }
        return con;
    }
        
    public static Connection getConnDB() throws ClassNotFoundException, SQLException  {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:DB");
            
        }catch(ClassNotFoundException | SQLException e)    {
            System.err.println(e.getClass().getName() + ": " + e.getMessage() );
        }
        return con;
    }
    
     public static void createTableDB() throws ClassNotFoundException, SQLException    {
        
        try    {
            Connection con = getConnDB();
            Statement stmt;        
            stmt = con.createStatement();               
            String query = "CREATE TABLE IF NOT EXISTS DB " + 
                "(name VARCHAR(50) PRIMARY KEY " +            
                ", salt CHAR(32) " + 
                ", iv CHAR(16));";
        
            stmt.executeUpdate(query);
            stmt.close();
        } catch(SQLException e) {
            System.out.println("Diretoria já adicionada!");
        }    
    }
    
    public static void createTableUser() throws ClassNotFoundException, SQLException    {
        Connection con = getConnUser();
        Statement stmt;
        
        stmt = con.createStatement();
        
        
        String query = "CREATE TABLE IF NOT EXISTS User " + 
            "(name VARCHAR(50) PRIMARY KEY " +
            ", salt CHAR(32) " + 
            ", rep CHAR(64));";
        
        stmt.executeUpdate(query);
        stmt.close();
    }
    
    public static void createTableDir(String nome) throws ClassNotFoundException, SQLException, Exception    {
        
        Connection con = getConnDir(nome);
        Statement stmt;
        stmt = con.createStatement();
        try {
            
            String query = "CREATE TABLE IF NOT EXISTS Dir " +
                "(name VARCHAR(100) PRIMARY KEY " +
                ", hash CHAR(64) " +
                ", salt CHAR(32) " +
                ", hmac CHAR (64));";
            
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e)   {
            System.out.println("\n\nA diretoria já foi adicionada à base de dados!\nPrima 4 para ajuda");        
        }
            
            
        
          
    }    
}
