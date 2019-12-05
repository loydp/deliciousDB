/*
 * Alisa Wallace, Peter Loyd, Doug Herstad
 * Seattle University
 * CPSC 5021 Fall 2019
 * Project Milestone 3
 * Based on code provided by Michael McKee of Seattle University
 */

package deliciousDB;
import java.sql.Connection;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages connection between application and database
 * @author mckeem
 */

public class QueryJDBC {


    public Connection m_conn = null; 
    static final String DB_DRV = "com.mysql.cj.jdbc.Driver";
    String m_error="";  
    String m_url;
    String m_user;
    String [] m_headers;
    String [][] m_allRows;
    int m_updateAmount = 0;
    
    /**
     * Constructor
     */
    QueryJDBC ()
    {
        m_updateAmount = 0;
    }
    
    /**
     * Returns the error from the database (set by other functions)
     * @return	the error that the database has
     */
    public String GetError()
    {
        return m_error;
    }

    /**
     * Gets the headers for the output table of a query
     * @return	a String array containing the table headers
     */
    public String [] GetHeaders()
    {
        return this.m_headers;
    }
    
    /**
     * Gets the data for the output table of a query
     * @return	a String double array with data
     */
    public String [][] GetData()
    {
        return this.m_allRows;
    }
    
    /**
     * Returns how many rows of the database table were affected by action query
     * @return
     */
    public int GetUpdateCount()
    {
        return m_updateAmount;
    }
    

    
    
    /**
     * Executes a query 
     * @param szQuery	the SQL query to execute
     * @param parms		an array of the parameters for a specific query
     * @param likeparms	whether each parameter is a LIKE parameter
     * @return	true if executed successful, false if not
     */
    public boolean ExecuteQuery(String szQuery, String [] parms, boolean [] likeparms)
    {
        PreparedStatement preparedStatement = null;        
        ResultSet resultSet = null;
        int nColAmt;
        boolean bOK = true;
        // Try to get the columns and the amount of columns
        try
        {
       
            preparedStatement=this.m_conn.prepareStatement(szQuery);            

            int nParamAmount = parms.length;

            for (int i=0; i < nParamAmount; i++)
            {
                String parm = parms[i];
                if (likeparms[i] == true)
                {
                    parm += "%";
                }
                preparedStatement.setString(i+1, parm);

            }

            resultSet=preparedStatement.executeQuery();

            ResultSetMetaData rsmd = resultSet.getMetaData(); 
            nColAmt = rsmd.getColumnCount();
            m_headers = new String [nColAmt];
            
            for (int i=0; i< nColAmt; i++)
            {
                m_headers[i] = rsmd.getColumnLabel(i+1);
            }
            int amtRow = 0;
            while(resultSet.next()){
                amtRow++;
            }
            if (amtRow > 0)
            {
                this.m_allRows= new String [amtRow][nColAmt];
                resultSet.beforeFirst();
                int nCurRow = 0;
                while(resultSet.next())
                {
                    for (int i=0; i < nColAmt; i++)
                    {
                       m_allRows[nCurRow][i] = resultSet.getString(i+1);
                    }
                    nCurRow++;
                }                                
            }
            else
            {
                this.m_allRows= new String [1][nColAmt];               
                for (int i=0; i < nColAmt; i++)
                {
                   m_allRows[0][i] = "";
                }               
            }
                  
            preparedStatement.close();
            resultSet.close();            
        }

        catch (SQLException ex) 
        {
            bOK = false;
            this.m_error = "SQLException: " + ex.getMessage();
            this.m_error += "SQLState: " + ex.getSQLState();
            this.m_error += "VendorError: " + ex.getErrorCode();
            
            
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;

        }          
                
        return true;
    }

    
     public boolean ExecuteUpdate(String szQuery, String [] parms)
    {
        PreparedStatement preparedStatement = null;        

        boolean bOK = true;
        m_updateAmount=0;
        
        // Try to get the columns and the amount of columns
        try
        {
       
            preparedStatement=this.m_conn.prepareStatement(szQuery);            

            int nParamAmount = parms.length;

            for (int i=0; i < nParamAmount; i++)
            {
                preparedStatement.setString(i+1, parms[i]);
            }
            
            m_updateAmount =preparedStatement.executeUpdate();  
            preparedStatement.close();          
        }
        
        catch (SQLIntegrityConstraintViolationException ex)
        {
        	bOK = false;
        	String errorMessage = "Sorry, " + parms[1] +
        			" cannot be added with the ID " + parms[0]
        			+ " because\nthat name is already "
        			+ "taken\n";
            this.m_error = errorMessage;
        	System.out.println(errorMessage);
        	return false;
        }

        catch (SQLException ex) 
        {
            bOK = false;
            this.m_error = "SQLException: " + ex.getMessage();
            this.m_error += "SQLState: " + ex.getSQLState();
            this.m_error += "VendorError: " + ex.getErrorCode();
            
            
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;

        }          
                
        return true;
    }
   
    
    /**
     * Connects application to the database          
     * @param host		hostname
     * @param user		username
     * @param pass		password
     * @param database	name of database on server
     * @return true if successful connection, false if not
     */
    public boolean ConnectToDatabase(String host, String user, String pass, String database)
    {        
        String url;
        
        url = "jdbc:mysql://";
        url += host;
        url +=":3306/";
        url += database;
        url +="?autoReconnect=true&useSSL=false&";
        url +="useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        try 
        {

            Class.forName(DB_DRV).newInstance();
            m_conn = DriverManager.getConnection(url,user,pass);
           
        } 
        catch (SQLException ex) 
        {
            m_error = "SQLException: " + ex.getMessage() +
                    ex.getSQLState() + 
                    ex.getErrorCode();
            return false;
        }          
        catch (Exception ex) 
        {
            // handle the error
            m_error = "SQLException: " + ex.getMessage();
            return false;
        }     
        
        return true;
    }
    

    /**
     * Disconnects from database.  Catches any errors with disconnection.
     * @return	true if successful, false if errors
     */
    public boolean CloseDatabase()
    {        
        try 
        {
            m_conn.close();
        } 
        catch (SQLException ex) 
        {
            
            m_error = "SQLException: " + ex.getMessage();
            m_error = "SQLState: " + ex.getSQLState();
            m_error = "VendorError: " + ex.getErrorCode();
            return false;
        }          
        catch (Exception ex) 
        {
            m_error = "Error was " + ex.toString();
            return false;
        }     
        
        return true;
    }
    
}
