package modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.DefaultListModel;

public class ModeloRespaldo extends Conexion {

    private ResultSet respuesta;
    private Runtime runTime;
    private Process p;
    private ProcessBuilder pb;


    public boolean generarBackup(String source, String parcial, boolean formatoSql) {
        try {            

            //Obetnemos la versión del Servidor
            String versionPostgres = versionPostgreSQL()[1].substring(0, 3);
            String tempCod = "";
            String url = "";

            String so = System.getProperty("os.name");

            if( so.equals("Linux") ){
                //Ruta Linux
                url = "/usr/lib/postgresql/10/bin/pg_dump";

            }else

            if( so.equals("Windows 7")){
                //Ruta Windows, la cual debera modificarse segun sea el caso
                url = "C:\\Archivos de programa\\PostgreSQL\\"+10+"\\bin\\pg_dump.exe";
                System.err.println("Windows :( -> " + url);
            }
            
            if(formatoSql)
                tempCod = url+",--verbose,--inserts,--column-inserts,-f,"+source+","+database;
            else
                tempCod = url+",-i,-F,c,-b,-v,-f,"+source+","+database;
            
            tempCod += parcial;

            //atato = a un arreglo obtenido de la división de palabras de tempCod por cada coma (,) conseguida
            String[] atato = tempCod.split(",");

            pb = new ProcessBuilder(atato);            

            runTime = Runtime.getRuntime();

            //Se asignan valores a las variables de PostgreSQL
            pb.environment().put("PGHOST", ip);
            pb.environment().put("PGPORT", puerto);
            pb.environment().put("PGUSER", usuario);
            pb.environment().put("PGPASSWORD", clave);
            pb.redirectErrorStream(true);
    
            p = pb.start();

            return true;
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }

    public boolean realizarRestore(String source){
        try {

            //Obetnemos la versión del Servidor
            String versionPostgres = versionPostgreSQL()[1].substring(0, 3);
            String tempCod = "";
            String url = "";
            String so = System.getProperty("os.name");
            String rutaServidor = "";
            if( so.equals("Linux") ){
                rutaServidor = "/usr/lib/postgresql/10/bin/pg_restore";

            }else

            if( so.equals("Windows 7")){
                rutaServidor = "C:\\Archivos de programa\\PostgreSQL\\"+10+"\\bin\\pg_restore.exe";
                System.err.println("Windows :( -> " + url);
            }

            String[] atato = {rutaServidor, "-d", database, "-v", source};

            pb = new ProcessBuilder(atato);

                runTime = Runtime.getRuntime();

            //Se asignan valores a las variables de PostgreSQL
            pb.environment().put("PGHOST", ip);
            pb.environment().put("PGPORT", puerto);
            pb.environment().put("PGUSER", usuario);
            pb.environment().put("PGPASSWORD", clave);
            pb.redirectErrorStream(true);
    
            p = pb.start();

            return true;
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }
    
    }
    /**
    * Metodo Encargado consultar por orden alfabetico todas las tablas que se encuentren en nuestra base de datos
    * en el esquema <b>public</b>
    */
    public DefaultListModel ConsultarTablas() {

        DefaultListModel tabla = new DefaultListModel();
        String sql = "SELECT  tablename FROM pg_tables WHERE schemaname ='public' ORDER BY tablename ASC";

        respuesta = consultarDatos(crearConexion(), sql);

        System.err.println("Ejecutando SQL: " + sql);

        try {
            while (respuesta.next()) {
                tabla.addElement(respuesta.getString(1));
           
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }

        cerrarConexion( crearConexion() );

        return tabla;
    }

    /**
    * Metodo Encargado obtener la versión de nuestro servidor PostgreSQL
    */
    private String[] versionPostgreSQL() {
        
        String sql = "SELECT  version();";

        respuesta = consultarDatos(crearConexion(), sql);

        try {
            while (respuesta.next()) {
                return respuesta.getString(1).split(" ", 3);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }

        cerrarConexion( crearConexion() );
        return null;
    }
}
