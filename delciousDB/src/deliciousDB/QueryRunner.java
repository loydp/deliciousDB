/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deliciousDB;

// Test

import java.util.ArrayList;
import java.util.Scanner;

// TODO I think the Scanner here is for the console version.

/**
 * 
 * QueryRunner takes a list of Queries that are initialized in it's constructor
 * and provides functions that will call the various functions in the QueryJDBC class 
 * which will enable MYSQL queries to be executed. It also has functions to provide the
 * returned data from the Queries. Currently the eventHandlers in QueryFrame call these
 * functions in order to run the Queries.
 */
public class QueryRunner {

    
    public QueryRunner()
    {
        this.m_jdbcData = new QueryJDBC();
        m_updateAmount = 0;
        m_queryArray = new ArrayList<>();
        m_error="";
    
        
        // TODO - You will need to change the queries below to match your queries.
        
     
        // You will need to put your Project Application in the below variable
        
        this.m_projectTeamApplication="Delicious Business";    // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        
        // Each row that is added to m_queryArray is a separate query. It does not work on Stored procedure calls.
        // The 'new' Java keyword is a way of initializing the data that will be added to QueryArray. Please do not change
        // Format for each row of m_queryArray is: (QueryText, ParamaterLabelArray[], LikeParameterArray[], IsItActionQuery, IsItParameterQuery)
        
        //    QueryText is a String that represents your query. It can be anything but Stored Procedure
        //    Parameter Label Array  (e.g. Put in null if there is no Parameters in your query, otherwise put in the Parameter Names)
        //    LikeParameter Array  is an array I regret having to add, but it is necessary to tell QueryRunner which parameter has a LIKE Clause. If you have no parameters, put in null. Otherwise put in false for parameters that don't use 'like' and true for ones that do.
        //    IsItActionQuery (e.g. Mark it true if it is, otherwise false)
        //    IsItParameterQuery (e.g.Mark it true if it is, otherwise false)
        
        /* Argument format: 
         * String query (the entire query)
         * String[] parms  (null if no params OR list of parameter names)
         * boolean [] likeparms (if parameter has "LIKE" clause; null if no params OR true/false for each param in array
         * boolean isAction (Action query = insert, update, delete)
         * boolean isParm (True if query takes params, false if not)
        */
        m_queryArray.add(new QueryData(
        		"Show all ingredients in database", 
        		"Select * from ingredient", 
        		null, 
        		null, 
        		false, 
        		false));
        m_queryArray.add(new QueryData(
        		"Show ingredient inventory for dish at location", 
        		"SELECT location_name, menu_item_name, ingredient_name, \r\n" + 
        		"	RHI.ingredient_quant AS 'Quantity Needed', \r\n" + 
        		"	RHI.ingredient_quant_unit AS 'Needed Units', \r\n" + 
        		"	sum(INV.ingredient_quant) AS 'Quantity Available', \r\n" + 
        		"	INV.ingredient_quant_unit AS 'Available Units'\r\n" + 
        		"FROM menu_item as MI\r\n" + 
        		"	JOIN menu_item_has_recipe as MIHR\r\n" + 
        		"	ON MI.menu_item_ID = MIHR.menu_item_ID\r\n" + 
        		"	JOIN recipe as R\r\n" + 
        		"	ON MIHR.recipe_ID = R.recipe_ID\r\n" + 
        		"	JOIN recipe_has_ingr as RHI\r\n" + 
        		"	ON R.recipe_ID = RHI.recipe_ID\r\n" + 
        		"	JOIN ingredient as I\r\n" + 
        		"	ON RHI.ingredient_ID = I.ingredient_ID\r\n" + 
        		"	JOIN vendor_product as VP\r\n" + 
        		"    	ON I.ingredient_ID = VP.ingredient_ID\r\n" + 
        		"    	JOIN inventory as INV\r\n" + 
        		"    	ON VP.product_ID = INV.product_ID\r\n" + 
        		"    	JOIN restaurant_location as L\r\n" + 
        		"    	ON INV.location_ID = L.location_ID\r\n" + 
        		"WHERE menu_item_name = ? AND location_name = ?\r\n" + 
        		"GROUP BY ingredient_name;", 
        		new String [] {"menu item name", "location name"}, 
        		new boolean [] {false, false},  
        		false, 
        		true));        
        m_queryArray.add(new QueryData(
        		"Name", 
        		"Select * from contact where contact_name like ?", 
        		new String [] {"CONTACT_NAME"}, 
        		new boolean [] {true}, 
        		false, 
        		true));        
        m_queryArray.add(new QueryData(
        		"Name", 
        		"insert into contact (contact_id, contact_name, contact_salary) values (?,?,?)",
        		new String [] {"CONTACT_ID", "CONTACT_NAME", "CONTACT_SALARY"}, 
        		new boolean [] {false, false, false}, 
        		true, true));
                       
    }
       

    public int GetTotalQueries()
    {
        return m_queryArray.size();
    }
    
    public int GetParameterAmtForQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetParmAmount();
    }
              
    public String GetParamText(int queryChoice, int parmnum )
    {
       QueryData e=m_queryArray.get(queryChoice);        
       return e.GetParamText(parmnum); 
    }   

    public String GetQueryText(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetQueryString();        
    }
    
    public String[] GetQueryNames() {
    	String[] queryNames = new String[m_queryArray.size()];
    	for (int i = 0; i < m_queryArray.size(); i++) {
    		queryNames[i] = (m_queryArray.get(i)).GetQueryName();
    	}
    	return queryNames;
    }
    
    /**
     * Function will return how many rows were updated as a result
     * of the update query
     * @return Returns how many rows were updated
     */
    
    public int GetUpdateAmount()
    {
        return m_updateAmount;
    }
    
    /**
     * Function will return ALL of the Column Headers from the query
     * @return Returns array of column headers
     */
    public String [] GetQueryHeaders()
    {
        return m_jdbcData.GetHeaders();
    }
    
    /**
     * After the query has been run, all of the data has been captured into
     * a multi-dimensional string array which contains all the row's. For each
     * row it also has all the column data. It is in string format
     * @return multi-dimensional array of String data based on the resultset 
     * from the query
     */
    public String[][] GetQueryData()
    {
        return m_jdbcData.GetData();
    }

    public String GetProjectTeamApplication()
    {
        return m_projectTeamApplication;        
    }
    public boolean  isActionQuery (int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryAction();
    }
    
    public boolean isParameterQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryParm();
    }
    
     
    public boolean ExecuteQuery(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteQuery(e.GetQueryString(), parms, e.GetAllLikeParams());
        return bOK;
    }
    
     public boolean ExecuteUpdate(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteUpdate(e.GetQueryString(), parms);
        m_updateAmount = m_jdbcData.GetUpdateCount();
        return bOK;
    }   
    
      
    public boolean Connect(String szHost, String szUser, String szPass, String szDatabase)
    {

        boolean bConnect = m_jdbcData.ConnectToDatabase(szHost, szUser, szPass, szDatabase);
        if (bConnect == false)
            m_error = m_jdbcData.GetError();        
        return bConnect;
    }
    
    public boolean Disconnect()
    {
        // Disconnect the JDBCData Object
        boolean bConnect = m_jdbcData.CloseDatabase();
        if (bConnect == false)
            m_error = m_jdbcData.GetError();
        return true;
    }
    
    public String GetError()
    {
        return m_error;
    }
 
    private QueryJDBC m_jdbcData;
    private String m_error;    
    private String m_projectTeamApplication;
    private ArrayList<QueryData> m_queryArray;  
    private int m_updateAmount;
      
}
