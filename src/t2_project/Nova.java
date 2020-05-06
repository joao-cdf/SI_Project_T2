package t2_project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import static t2_project.DB.createTableDB;
import static t2_project.DB.createTableDir;
import static t2_project.DB.getConnDB;
import static t2_project.DB.getConnDir;
import static t2_project.DB.getConnUser;
import static t2_project.HMAC_SHA512_Decrypter.calculateHMAC;
import static t2_project.Lib.bytesToHex;
import static t2_project.Lib.fileHash;
import static t2_project.Lib.getSalt;
import static t2_project.Lib.hexToBytes;





public class Nova {
    
    public static String STRINGDIR = "";
    public static String user_key = "";
    public static String user_name;
    public static String dir_name;
    private static boolean err = false;
    

    
    public static void funcaoNova(String nome) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, IOException, Exception  {
        
        user_name = nome;
        int option;
        boolean flag_opt = true;
        
        while(flag_opt) {
            
            System.out.println("+--------------+");
            System.out.println("| Estou a ver! |");
            System.out.println("+--------------+");

            
            System.out.println("\nBem vindo " +nome+ "!\n");
        
            System.out.println("1. Ler diretoria");
            System.out.println("2. Verificar diretoria");
            System.out.println("3. Apagar registos do utilizador");
            System.out.println("4. Ajuda");
            System.out.println("5. Logout");
            System.out.println("6. Sair");
            
            option = Ler.umInt();
            
            switch(option)  {
                case 1: {
                    readDir(nome);                                        
                    break;
                }
                case 2: {
                    verifyDir(nome);
                    break;
                }
                case 3: {
                    if(removeUser(nome))
                        flag_opt = false;
                    break;
                }
                case 4: {
                    funcHelp();
                    break;
                }
                case 5: {
                    System.out.println("Logged out\n");
                    flag_opt = false;
                    break;
                }
                case 6: {
                    System.out.println("Aplicação terminada");
                    System.exit(0);
                    break;
                }
                default :   {
                    System.out.println("Opção inválida");
                    break;
                }
            }        
        }
    }

    public static void funcHelp()   {
        
        System.out.println("+----------------------------------------------------------------------------------------+");
        System.out.println("|                                        Ajuda                                           |");
        System.out.println("+----------------------------------------------------------------------------------------+");
        System.out.println("|                                                                                        |");
        System.out.println("|  1 -> Ler Diretoria                                                                    |");
        System.out.println("|      * Lê a diretoria que é introduzida pelo utilizador (caso a diretoria não esteja   |");
        System.out.println("|         junto do programa usar o caminho absoluto da diretoria.                        |");
        System.out.println("|      * Após a leitura, insere na base de dados de nome xxxxx.db os valores de SHA-256  |");
        System.out.println("|         e HMAC-SHA512 de cada ficheiro pertencente à diretoria.                        |");
        System.out.println("|      * A base de dados é cifrada de acordo com uma palavra-chave introduzida pelo      |");
        System.out.println("|         utilizador (não é guardada em memória) usando AES-128-CBC,                     |");
        System.out.println("|         usando PBKDF2 com um salt gerado aleatóriamente.                               |");
        System.out.println("|                                                                                        |");
        System.out.println("|  2 -> Verificar Diretoria                                                              |");
        System.out.println("|      * Verifica a integridade da diretoria introduzida pelo utilizador (note que a     |");
        System.out.println("|         mesma diretoria tem que ser lida anteriormente em 1 -> Ler Diretoria).         |");
        System.out.println("|      * Após comparação dos valores SHA-256 e HMAC-SHA512 na base de dados com a        |");
        System.out.println("|         diretoria, caso tenham detetadas alterações na diretoria.                      |");
        System.out.println("|         (ficheiros alterados, adicionados ou removidos) é criada uma nova base de      |");
        System.out.println("|         dados e a base de dados 'antiga' é lhe atribuído um nove nome seguindo         |");
        System.out.println("|         o seguinte esquema: xxxxx1.db, xxxxx2.db, etc.                                 |");
        System.out.println("|                                                                                        |");
        System.out.println("|  3 -> Apagar registos do utilizador                                                    |");
        System.out.println("|      * Apaga o registo na base de dados do utilizador que estiver looged in.           |");
        System.out.println("|      * Volta para o menu de registo/login.                                             |");
        System.out.println("|                                                                                        |");
        System.out.println("|  5 -> Logout                                                                           |");
        System.out.println("|      * Volta para o menu de registo/login.                                             |");
        System.out.println("|                                                                                        |");
        System.out.println("|  6 -> Sair                                                                             |");
        System.out.println("|      * Sai da aplicação.                                                               |");
        System.out.println("|                                                                                        |");
        System.out.println("+----------------------------------------------------------------------------------------+");
        
        
    }
    
    public static void readDir(String nome) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, IOException, Exception {
        
        
        
        System.out.println("Insira o nome da diretoria: ");
        STRINGDIR = Ler.umaString();        
        do {
            System.out.println("Insira a sua chave: ");
            user_key = Ler.umaString();
        } while (user_key.isEmpty());
        
        
        
        File folder = new File(STRINGDIR);
        dir_name = folder.getName();
        listFilesForFolder(folder);
        if(!err)
            System.out.println("Base de dados criada!\n");
              
        File db = new File(dir_name+".db");
        byte[] content = Files.readAllBytes(db.toPath());   
        
        createTableDB();
        try {
            Connection aux = getConnDB();
            Statement st;
            st = aux.createStatement();
            String sql = "SELECT salt, iv FROM DB WHERE name = '"+dir_name+".db'";
            ResultSet rs = st.executeQuery(sql);            
            byte[] salt = hexToBytes(rs.getString(1));
            byte[] iv = hexToBytes(rs.getString(2));
            
            try {

                AES_Decrypter oDecrypter = new AES_Decrypter(user_key, salt, iv);
                byte[] encrypted = oDecrypter.encrypt(content);            
                new PrintWriter(dir_name+".db").close();
                try (FileOutputStream fos = new FileOutputStream(dir_name+".db")) {
                    fos.write(encrypted);
                }
            } catch (Exception e)    {
                System.out.println(e);
            } 
            
        } catch (SQLException e)  {
            AES_Decrypter oDecrypter = new AES_Decrypter(user_key);
            byte[] salt = oDecrypter.getSaltVal();
            byte[] iv = oDecrypter.getIv();

            byte[] encrypted = oDecrypter.encrypt(content);        
            new PrintWriter(dir_name+".db").close();
            try (FileOutputStream fos = new FileOutputStream(dir_name+".db")) {
                fos.write(encrypted);
            }           
            try {
                Connection aux = getConnDB();
                Statement st;
                st = aux.createStatement();
                String sql = "INSERT INTO DB VALUES('"+dir_name+".db', '"+bytesToHex(salt)+"', '"+bytesToHex(iv)+"')";
                st.executeUpdate(sql);
                st.close();
            } catch (SQLException d)  {
                System.out.println(d);
            }
        }
        
        
        
		
        	                
    }
    
    public static void listFilesForFolder(final File folder) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, IOException, Exception {
        
        
        try{
            createTableDir(dir_name); 
            Connection conn = getConnDir(dir_name);
        
            
        
            String hash_val;
            String hmac_val;
            byte[] salt;
            String salt_val;
            String sql;        

            try {
                for (final File fileEntry : folder.listFiles()) {
                    if (fileEntry.isDirectory()) {
                        listFilesForFolder(fileEntry);
                    } else {                    
                        //Calcular Salt
                        salt = getSalt();
                        salt_val = bytesToHex(salt);

                        //Calcular HMAC-SHA512
                        File db = new File(fileEntry.getAbsolutePath());                    
                        byte[] content = Files.readAllBytes(db.toPath()); 
                        hmac_val = bytesToHex(calculateHMAC(content, salt_val+user_key));

                        //Calcular o valor de hash do ficheiro SHA256
                        hash_val = fileHash(folder.getAbsolutePath()+"/"+fileEntry.getName());            

                        sql = "INSERT INTO Dir VALUES ('"+fileEntry.getAbsolutePath()+"' , '"+hash_val+"' , '"+salt_val+"' , '"+hmac_val+"')";                                     
                        Statement st;
                        st = conn.createStatement();
           
                        st.executeUpdate(sql);      
                        st.close();     
                        
                    }
                }
                conn.close();            
            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | SQLException e)  {
                err = true;
            }  
            
        } catch (SQLException e)    {
           
        }


    }  

    private static void verifyDir(String nome) throws FileNotFoundException, IOException, Exception {
        
        System.out.println("Insira o nome da diretoria: ");
        STRINGDIR = Ler.umaString();
        do {
            System.out.println("Insira a sua chave: ");
            user_key = Ler.umaString();
        } while (user_key.isEmpty());
        
        boolean notFound = false;
        File folder = new File(STRINGDIR);
        dir_name = folder.getName();
          
        byte[] salt;
        byte[] iv;
        Connection aux = getConnDB();
        Statement st;
        String sql = "SELECT salt , iv FROM DB WHERE name= '"+dir_name+".db'";
        st = aux.createStatement();
        try {
            ResultSet rs = st.executeQuery(sql);            
            salt = hexToBytes(rs.getString(1));
            iv = hexToBytes(rs.getString(2));
            
            st.close();                      
            try {
                //tenta abrir ficheiro da base de dados
                FileInputStream temp = new FileInputStream(dir_name+".db");
                temp.close();
                File db = new File(dir_name+".db");
                byte[] content = Files.readAllBytes(db.toPath()); 
                AES_Decrypter oDecrypter = new AES_Decrypter(user_key, salt, iv);
                byte[] decrypted = oDecrypter.decrypt(content);            
                new PrintWriter(dir_name+".db").close();
                try (FileOutputStream fos = new FileOutputStream(dir_name+".db")) {
                    fos.write(decrypted);
                }
                
            } catch (FileNotFoundException e) {
                System.out.println("File "+dir_name+".db not found...");
                notFound = true;
            } catch (Exception e)   {
                System.out.println("base de dados já decifrada ou a chave que introduziu está errada!");
            }
        }   catch (SQLException e)    {
            System.out.println(e);
        }              
        
        if(!notFound){
            
            String name_db, hash_db, salt_db, hmac_db;
            boolean changes = true;
            Connection conn = getConnDir(dir_name);
            sql = "SELECT * FROM Dir";
            Statement st1 = conn.createStatement();
            ResultSet oRs;
            File folder_aux = new File(STRINGDIR);
            List<String> fileList = getList(folder_aux);
            List<String> list = new ArrayList<>();
            try {
                oRs = st1.executeQuery(sql);                            
                while(oRs.next())   {
                    name_db = oRs.getString(1);
                    hash_db = oRs.getString(2);
                    salt_db = oRs.getString(3);
                    hmac_db = oRs.getString(4);                   
                    File db = new File(name_db);  
                    list.add(db.getName());
                    try {
                        byte[] content = Files.readAllBytes(db.toPath());   
                    
                        if(((!hash_db.equals(fileHash(name_db)))
                            || (!hmac_db.equals(bytesToHex(calculateHMAC(content, salt_db+user_key))))))  {
                            System.out.println("Verificaram-se alterações no ficheiro "+name_db);
                            changes = false;
                        }   
                        
                    } catch (IOException | InvalidKeyException | NoSuchAlgorithmException e)   {
                        System.out.println("O ficheiro "+name_db+" já não existe na diretoria!");
                        changes = false;
                    }                                                           
                }
                if(!notIn(fileList, list))   {
                    System.out.println("Existem novos ficheiros na diretoria!");
                    changes = false;
                }    
                oRs.close();
                if(changes)    {
                    
                    System.out.println("\n\nTEST PASSED OK -- A tua diretoria está segura!");                    
                                     
                    Connection aux2 = getConnDB();
                    Statement st2;
                    sql = "SELECT salt , iv FROM DB WHERE name= '"+dir_name+".db'";
                    st2 = aux2.createStatement();
                        ResultSet rs2 = st2.executeQuery(sql);            
                        salt = hexToBytes(rs2.getString(1));
                        iv = hexToBytes(rs2.getString(2));                        
                        st2.close();
                        aux2.close();

                        File db = new File(dir_name+".db");               
                        byte[] content = Files.readAllBytes(db.toPath());   

                        try {

                            AES_Decrypter oDecrypter = new AES_Decrypter(user_key, salt, iv);
                            byte[] encrypted = oDecrypter.encrypt(content);            
                            new PrintWriter(dir_name+".db").close();
                            try (FileOutputStream fos = new FileOutputStream(dir_name+".db")) {
                                fos.write(encrypted);
                            }
                        } catch (Exception e)    {
                            System.out.println(e);
                        }       
                }
                if(!changes)   {
                    
                    System.out.println("\nA atualizar a base de dados...");
                    
                    int i = 1;
                    // Ficheiro com antigo nome
                    File file = new File(dir_name+".db");

                    // Ficheiro com novo nome
                    File file2;
                    do {
                        file2 = new File(dir_name+i+".db");
                        if(file2.exists())
                            i++;
                    } while (file2.exists());

                    // Mudar o nome
                    boolean success = file.renameTo(file2);
                                                                                                                      
                    if(success) {
                        // Mudar o nome na base de dados
                        Connection temp = getConnDB();
                        PreparedStatement Stemp;
                        sql = "UPDATE DB SET name = ? WHERE name = '"+dir_name+"'";
                        try{
                            Stemp = temp.prepareStatement(sql);
                            Stemp.setString(1, dir_name+i);
                            Stemp.executeUpdate();  
                            Stemp.close();
                        } catch (SQLException e)    {
                            System.out.println(e);
                        }
                        
                        
                        listFilesForFolder(folder);
                        
                        File db = new File(dir_name+i+".db");
                        byte[] content = Files.readAllBytes(db.toPath());
                        AES_Decrypter oDecrypter = new AES_Decrypter(user_key);
                        salt = oDecrypter.getSaltVal();
                        iv = oDecrypter.getIv();

                        byte[] encrypted = oDecrypter.encrypt(content);        
                        new PrintWriter(dir_name+i+".db").close();
                        try (FileOutputStream fos = new FileOutputStream(dir_name+i+".db")) {
                            fos.write(encrypted);
                        }                        
                        try (Connection aux2 = getConnDB()) {
                            Statement st3;
                            st3 = aux2.createStatement();
                            sql = "INSERT INTO DB VALUES('"+dir_name+i+".db', '"+bytesToHex(salt)+"', '"+bytesToHex(iv)+"')";
                            st3.executeUpdate(sql);
                            st3.close();
                        } 
                    }
                    
                    File db = new File(dir_name+".db");
                    byte[] content = Files.readAllBytes(db.toPath());
                    AES_Decrypter oDecrypter = new AES_Decrypter(user_key);                    
                    salt = oDecrypter.getSaltVal();
                    iv = oDecrypter.getIv();
                    byte[] encrypted = oDecrypter.encrypt(content);        
                    new PrintWriter(dir_name+".db").close();
                    try (FileOutputStream fos = new FileOutputStream(dir_name+".db")) {
                        fos.write(encrypted);
                    }
                    try (Connection temp2 = getConnDB()) {
                            PreparedStatement Stemp2;
                            sql = "UPDATE DB SET salt = ? , iv = ? WHERE name = '"+dir_name+".db'";
                            try{
                                Stemp2 = temp2.prepareStatement(sql);
                                Stemp2.setString(1, bytesToHex(salt));
                                Stemp2.setString(2, bytesToHex(iv));
                                Stemp2.executeUpdate();  
                                Stemp2.close();
                                temp2.close();
                        } catch (SQLException e)    {
                            System.out.println(e);
                        }
                        } 
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        }    
    }
    
    private static List<String> getList(final File folder)   {
        List <String> temp = new ArrayList<>();
        
        for(final File fileEntry : folder.listFiles())
            if(fileEntry.isDirectory())
                temp.addAll(getList(fileEntry));
            else    {
                temp.add(fileEntry.getName());
            }
        return temp;
    }
    
    private static boolean notIn(List<String> list, List<String> name) {
        boolean flag = false;
        for (String list1 : list) {
            flag = false;
            for(String list2 : name)  {
                if(list1.equals(list2))   
                    flag = true;
            }
            if(!flag)
                break;
        }        
        return flag;
    }

    private static boolean removeUser(String nome) throws ClassNotFoundException, SQLException {
        
        int option;
        System.out.println("Quer mesmo remover este utilizador? ("+nome+")");
        
        do {
            System.out.println("Use 1 para remover e 0 para voltar");
            option = Ler.umInt();
            if(option != 1 && option != 0)
                System.out.println("Opção inválida!");
            if(option == 1 || option == 0)
                break;
        } while (true);
        
        if(option == 1)  {
            
            try (Connection conn = getConnUser()) {
                PreparedStatement st;
                
                String sql = "DELETE FROM User WHERE name = ?";
                st = conn.prepareStatement(sql);
                
                st.setString(1, nome);
                st.executeUpdate();
                st.close();
                System.out.println("Utilizador eliminado com sucesso!");
                return true;
            }
        } 
        return false;
    }
}

