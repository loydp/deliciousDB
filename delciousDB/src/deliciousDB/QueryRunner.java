/*
 * Alisa Wallace, Peter Loyd, Doug Herstad
 * Seattle University
 * CPSC 5021 Fall 2019
 * Project Milestone 3
 * Based on code provided by Michael McKee of Seattle University
 */

package deliciousDB;


import java.util.ArrayList;

/**
 * QueryRunner takes a list of Queries that are initialized in its constructor
 * and provides functions that will call the various functions in the QueryJDBC class 
 * which will enable MYSQL queries to be executed. It also has functions to provide the
 * returned data from the Queries. Currently the eventHandlers in QueryFrame call these
 * functions in order to run the Queries.
 * 
 * @author Michael McKee
 * 
 * Enhanced by Alisa Wallace, Peter Loyd, and Doug Herstad
 */

public class QueryRunner {

    /**
     * Constructor
     * Initializes QueryRunner object with queries manually input
     */
    public QueryRunner()
    {
        this.m_jdbcData = new QueryJDBC();
        m_updateAmount = 0;
        m_queryArray = new ArrayList<>();
        m_error="";
        
        this.m_projectTeamApplication="Delicious Business";
        
        // Each row that is added to m_queryArray is a separate query. It does not work on Stored procedure calls.
        // The 'new' Java keyword is a way of initializing the data that will be added to QueryArray. Please do not change
        // Format for each row of m_queryArray is: (QueryText, ParamaterLabelArray[], LikeParameterArray[], IsItActionQuery, IsItParameterQuery)
        
        //    QueryText is a String that represents your query. It can be anything but Stored Procedure
        //    Parameter Label Array  (e.g. Put in null if there is no Parameters in your query, otherwise put in the Parameter Names)
        //    LikeParameter Array  is an array I regret having to add, but it is necessary to tell QueryRunner which parameter has a LIKE Clause. If you have no parameters, put in null. Otherwise put in false for parameters that don't use 'like' and true for ones that do.
        //    IsItActionQuery (e.g. Mark it true if it is, otherwise false)
        //    IsItParameterQuery (e.g.Mark it true if it is, otherwise false)
        
        /* Argument format: 
         * queryName The name of the query, to be presented in menus.
         * String query (the entire query)
         * String[] parms  (null if no params OR list of parameter names)
         * boolean [] likeparms (if parameter has "LIKE" clause; null if no params OR true/false for each param in array
         * boolean isAction (Action query = insert, update, delete)
         * boolean isParm (True if query takes params, false if not)
         * string[] defaults A string of defaults to be placed into params, in parallel.
        */
        m_queryArray.add(new QueryData(
        		"Show all ingredients transported by horse", 
        		"Select ingredient.ingredient_id AS 'Ingredient',\n    ingredient_name as 'Ingredient Name',\n   " + 
        		"ingredient_category as 'Category',\n    product_transport_method as 'Transport Method'\n" + 
        		"from ingredient join vendor_product\n    using (ingredient_id)\n" + 
        		"where product_transport_method = 'horseback'\n" + 
        		"order by category, ingredient;", 
        		null, 
        		null, 
        		false, 
        		false,
        		null));
        
        m_queryArray.add(new QueryData(
        		"Show ingredient inventory for specified dish at specified location", 
        		"SELECT location_name as 'Location Name', \n   menu_item_name as 'Menu Item', \n   ingredient_name as 'Ingredient', " + 
        		"\n   RHI.ingredient_quant AS 'Quantity Needed', " + 
        		"\n   RHI.ingredient_quant_unit AS 'Needed Units'," + 
        		"\n   sum(INV.ingredient_quant) AS 'Quantity Available'," + 
        		"\n   INV.ingredient_quant_unit AS 'Available Units'" + 
        		"\nFROM menu_item as MI" + 
        		"\n   JOIN menu_item_has_recipe as MIHR" + 
        		"\n   ON MI.menu_item_ID = MIHR.menu_item_ID" + 
        		"\n   JOIN recipe as R" + 
        		"\n   ON MIHR.recipe_ID = R.recipe_ID" + 
        		"\n   JOIN recipe_has_ingr as RHI" + 
        		"\n   ON R.recipe_ID = RHI.recipe_ID" + 
        		"\n   JOIN ingredient as I" + 
        		"\n   ON RHI.ingredient_ID = I.ingredient_ID" + 
        		"\n   JOIN vendor_product as VP" + 
        		"\n   ON I.ingredient_ID = VP.ingredient_ID" + 
        		"\n   JOIN inventory as INV" + 
        		"\n   ON VP.product_ID = INV.product_ID" + 
        		"\n   JOIN restaurant_location as L" + 
        		"\n   ON INV.location_ID = L.location_ID" + 
        		"\nWHERE menu_item_name = ? AND location_name = ?" + 
        		"\nGROUP BY ingredient_name;", 
        		new String [] {"menu item name", "location name"}, 
        		new boolean [] {false, false},  
        		false, 
        		true,
                new String[] {"Moroccan Spiced Seitan", "The Bleu Danube"}));        
       
        m_queryArray.add(new QueryData(
        		"Show all dietary restrictions for the menu items in menu plan 1", 
        		"SELECT DISTINCT menu_item_name as 'Menu Item', \n   dietary_restr_name as 'Dietary Restriction' " + 
        		"\nFROM menu_plan_composition AS MPC" + 
        		"\n   JOIN menu_item as MI" + 
        		"\n   ON MPC.menu_item_ID = MI.menu_item_ID" + 
        		"\n   JOIN menu_item_has_recipe as MIHR" + 
        		"\n   ON MI.menu_item_ID = MIHR.menu_item_ID" + 
        		"\n   JOIN recipe as R" + 
        		"\n   ON MIHR.recipe_ID = R.recipe_ID" + 
        		"\n   JOIN recipe_has_ingr as RHI" + 
        		"\n   ON R.recipe_ID = RHI.recipe_ID" + 
        		"\n   JOIN ingredient as I" + 
        		"\n   ON RHI.ingredient_ID = I.ingredient_ID" + 
        		"\n   JOIN ingr_has_diet_restr as IHDR" + 
        		"\n   ON I.ingredient_ID = IHDR.ingredient_ID" + 
        		"\n   JOIN dietary_restriction as DR" + 
        		"\n   ON IHDR.dietary_restr_ID = DR.dietary_restr_ID" + 
        		"\nWHERE MPC.menu_ID = 1" + 
        		"\nORDER BY menu_item_name;", 
        		null, 
        		null, 
        		false, 
        		false,
        		null));
        
        m_queryArray.add(new QueryData(
        		"Show all seasonal vendor products within specified time range", 
        		"SELECT ingredient.ingredient_name as 'Ingredient', \n   vendor_product.product_id as 'Product ID',\n   " + 
        		"vendor_product.product_season_start as 'Season Start', \n   " +
        		"vendor_product.product_season_end as 'Season End'\n" +
        		"FROM vendor_product natural join ingredient\n" + 
        		"WHERE vendor_product.product_limited_avail = 1\n   " + 
        		"AND vendor_product.product_season_start > ?\n   " + 
        		"AND vendor_product.product_season_end < ?\n" + 
        		"ORDER BY vendor_product.product_season_start;",
        		new String [] {"Start date (yyyy-mm-dd)", "End date (yyy-mm-dd)"}, 
        		new boolean [] {false, false}, 
        		false, true,
                new String[] {"2019-09-01", "2023-04-05"}));
        
        m_queryArray.add(new QueryData(
        		"Show the minimum total amount of miles products need to travel to make a menu item",
        		"SELECT recipe.recipe_id as 'Recipe ID', \n   recipe.recipe_name as 'Recipe Name', \n   " + 
        		"sum(vendor_product.vendor_miles_from_source + vendor.vendor_distance_miles) \n   as 'Total Min distance traveled' \n" + 
        		"FROM menu_plan_composition natural \n   join menu_item natural \n   join menu_item_has_recipe natural \n   join recipe natural \n   join recipe_has_ingr \n   " + 
        		"natural join ingredient \n   natural join vendor_product \n   natural join vendor " + 
        		"WHERE (vendor_product.vendor_miles_from_source + \n   vendor.vendor_distance_miles) <= \n   " + 
        		"(select min(vendor_product.vendor_miles_from_source \n   + vendor.vendor_distance_miles) " + 
        		"\nFrom vendor_product natural join vendor " + 
        		"\nWHERE vendor_product.ingredient_id = ingredient.ingredient_id) " +
        		"\nGROUP BY menu_item_has_recipe.recipe_id " + 
        		"\nORDER BY sum(vendor_product.vendor_miles_from_source + \n   vendor.vendor_distance_miles) asc",
        		null, 
        		null, 
        		false, 
        		false, 
        		null));
        
        m_queryArray.add(new QueryData(
        		"Show top selling dishes in the last month for all locations", 
        		"SELECT menu_item.menu_item_name as 'Menu Item', \n   sum(item_quantity) as 'Total Dishes Sold'\n" + 
        		"FROM customer_check\n   " + 
        		"JOIN check_item\n   " + 
        		"ON check_item.check_number \n   = customer_check.check_number\n   " + 
        		"JOIN menu_item\n   " + 
        		"ON menu_item.menu_item_ID = check_item.menu_item_ID\n" + 
        		"WHERE (customer_check.check_date >= ('2019-10-09') \n   " + 
        		"AND customer_check.check_date <= '2019-11-09')\n" + 
        		"GROUP BY menu_item_name\n" + 
        		"ORDER BY sum(item_quantity) DESC;",
        		null,
        		null, 
        		false, 
        		false, 
        		null));
        
        m_queryArray.add(new QueryData(
        		"Show all dietary restrictions in database", 
        		"SELECT	dietary_restr_ID as 'Restr. ID', \n   dietary_restr_name as 'Dietary Restriction Name' \nFROM dietary_restriction\n" +
        		"ORDER BY dietary_restr_ID;",
        		null,
        		null, 
        		false, 
        		false,
        		null));
        
        m_queryArray.add(new QueryData(
        		"Add a dietary restriction to database",
        		"INSERT INTO dietary_restriction \n   (dietary_restr_id, dietary_restr_name) values (?,?);",
        		new String [] {"Dietary restriction ID", "Dietary restriction name"}, 
        		new boolean [] {false, false}, 
        		true, 
        		true,
        		new String[] {"120", "Pork"}));
    }
       

    /**
     * Returns the number of queries, used in menus.
     * @return int Representing the number of queries.
     */
    public int GetTotalQueries()
    {
        return m_queryArray.size();
    }
    
    /**
     * Investigates a query to find how many parameters it has.
     * @param queryChoice The query being investigated
     * @return int the number of parameters in this query.
     */
    public int GetParameterAmtForQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetParmAmount();
    }
              
    /**
     * Returns the name of a parameter at a given location.
     * @param queryChoice The index of the query.
     * @param parmnum The index of the parameter.
     * @return The name of a parameter
     */
    public String GetParamText(int queryChoice, int parmnum )
    {
       QueryData e=m_queryArray.get(queryChoice);        
       return e.GetParamText(parmnum); 
    }   
    
    /**
     * Returns default values to be inserted into parameters.
     * @param queryChoice The index of the query.
     * @param parmnum The index of the parameter.
     * @return default values to be inserted into parameters.
     */
    public String GetParamDefault(int queryChoice, int parmnum )
    {
       QueryData e=m_queryArray.get(queryChoice);        
       return e.GetQueryDefault(parmnum);
    }
    
    /**
     * Returns the SQL query, as a string.
     * @param queryChoice The index of the query.
     * @return the SQL query, as a string.
     */
    public String GetQueryText(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetQueryString();        
    }
    
    /**
     * Returns the default parameter as a string.
     * @param queryChoice The index of the query.
     * @return The default parameter as a string.
     */
    public String[] GetQueryDefaults(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.getQueryDefaults();               
    }
    
    /**
     * Gets the name of the query
     * @param queryChoice The index of the query.
     * @return The name of the query
     */
    public String GetQueryName(int queryChoice)
    {
    	QueryData e=m_queryArray.get(queryChoice);
    	return e.GetQueryName();
    }
    
    /**
     * Gets the names of the queries.
     * @return An array of query names.
     */
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
     * @return multi-dimensional array of String data based on the result set 
     * from the query
     */
    public String[][] GetQueryData()
    {
        return m_jdbcData.GetData();
    }

    /**
     * Returns the name of the project.
     * @return The name of the project.
     */
    public String GetProjectTeamApplication()
    {
        return m_projectTeamApplication;        
    }
    
    /**
     * Returns a boolean representing whether the query is an action query.
     * @param queryChoice the index of the query in an array
     * @return boolean representing if the query was an action query
     */
    public boolean  isActionQuery (int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryAction();
    }
    
    /**
     * Returns a boolean representing if the query has parameters.
     * @param queryChoice The index of the query in an array.
     * @return boolean representing if the query has parameters.
     */
    public boolean isParameterQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryParm();
    }
    
     /**
      * Executes a query, reports if it worked.
      * @param queryChoice the index of the query
      * @param parms the parameters of the query
      * @return boolean representing if it worked.
      */
    public boolean ExecuteQuery(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteQuery(e.GetQueryString(), parms, e.GetAllLikeParams());
        if (!bOK) {
        	m_error = m_jdbcData.GetError(); 
        }
        return bOK;
    }
    
    /**
     * Executes a query that changes the DB
     * @param queryChoice the index of the query
     * @param parms the parameters of the query
     * @return a boolean representing if the query updated properly.
     */
     public boolean ExecuteUpdate(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteUpdate(e.GetQueryString(), parms);
        m_updateAmount = m_jdbcData.GetUpdateCount();
        if (!bOK) {
        	m_error = m_jdbcData.GetError();  
        }
        return bOK;
    }   
    
    /**
     * Passes connection information to jdbc.
     * @param szHost 
     * @param szUser
     * @param szPass
     * @param szDatabase
     * @return Boolean indicating if the connection was made properly
     */
    public boolean Connect(String szHost, String szUser, String szPass, String szDatabase)
    {

        boolean bConnect = m_jdbcData.ConnectToDatabase(szHost, szUser, szPass, szDatabase);
        if (bConnect == false)
            m_error = m_jdbcData.GetError();        
        return bConnect;
    }
    
    /**
     * Disconnects from the DB
     * @return boolean indicating that disconnection happened properly.
     */
    public boolean Disconnect()
    {
        // Disconnect the JDBCData Object
        boolean bConnect = m_jdbcData.CloseDatabase();
        if (bConnect == false)
            m_error = m_jdbcData.GetError();
        return true;
    }
    
    /**
     * Passes an error message back to the function that called it.
     * @return m_error An error message.
     */
    public String GetError()
    {
        return m_error;
    }
 
    private QueryJDBC m_jdbcData;               // A JDBC instance
    private String m_error;                     // Error messages
    private String m_projectTeamApplication;    // The name of the project
    private ArrayList<QueryData> m_queryArray;  // The queries.
    private int m_updateAmount;                 // Displays the number of
                                                // rows effected by an update
      
}
