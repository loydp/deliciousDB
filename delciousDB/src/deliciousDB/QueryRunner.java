/*
 * Alisa Wallace, Peter Loyd, Doug Herstad
 * Seattle University
 * CPSC 5021 Fall 2019
 * Project Milestone 3
 * Based on code provided by Michael McKee of Seattle University
 */

package deliciousDB;


import java.util.ArrayList;
//import java.util.Scanner;

// TODO I think the Scanner here is for the console version.

/**
 * QueryRunner takes a list of Queries that are initialized in it's constructor
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
         * String query (the entire query)
         * String[] parms  (null if no params OR list of parameter names)
         * boolean [] likeparms (if parameter has "LIKE" clause; null if no params OR true/false for each param in array
         * boolean isAction (Action query = insert, update, delete)
         * boolean isParm (True if query takes params, false if not)
        */
        m_queryArray.add(new QueryData(
        		"Show all ingredients transported by horse", 
        		"Select ingredient.ingredient_id AS 'Ingredient', ingredient_name as 'Ingredient Name',\r\n" + 
        		"        ingredient_category as 'Category', product_transport_method as 'Transport Method'\r\n" + 
        		"from ingredient join vendor_product using (ingredient_id)\r\n" + 
        		"where product_transport_method = 'horseback'\r\n" + 
        		"order by category, ingredient;", 
        		null, 
        		null, 
        		false, 
        		false,
        		null));
        
        m_queryArray.add(new QueryData(
        		"Show ingredient inventory for specified dish at specified location", 
        		"SELECT location_name as 'Location Name', menu_item_name as 'Menu Item', ingredient_name as 'Ingredient', " + 
        		"	RHI.ingredient_quant AS 'Quantity Needed', " + 
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
        		true,
                new String[] {"Moroccan Spiced Seitan", "The Bleu Danube"}));        
       
        m_queryArray.add(new QueryData(
        		"Show all dietary restrictions for the menu items in menu plan 1", 
        		"SELECT DISTINCT menu_item_name as 'Menu Item', dietary_restr_name as 'Dietary Restriction' " + 
        		"FROM menu_plan_composition AS MPC\r\n" + 
        		"	JOIN menu_item as MI\r\n" + 
        		"	ON MPC.menu_item_ID = MI.menu_item_ID\r\n" + 
        		"	JOIN menu_item_has_recipe as MIHR\r\n" + 
        		"	ON MI.menu_item_ID = MIHR.menu_item_ID\r\n" + 
        		"	JOIN recipe as R\r\n" + 
        		"	ON MIHR.recipe_ID = R.recipe_ID\r\n" + 
        		"	JOIN recipe_has_ingr as RHI\r\n" + 
        		"	ON R.recipe_ID = RHI.recipe_ID\r\n" + 
        		"	JOIN ingredient as I\r\n" + 
        		"	ON RHI.ingredient_ID = I.ingredient_ID\r\n" + 
        		"	JOIN ingr_has_diet_restr as IHDR\r\n" + 
        		"	ON I.ingredient_ID = IHDR.ingredient_ID\r\n" + 
        		"	JOIN dietary_restriction as DR\r\n" + 
        		"	ON IHDR.dietary_restr_ID = DR.dietary_restr_ID\r\n" + 
        		"WHERE MPC.menu_ID = 1\r\n" + 
        		"ORDER BY menu_item_name;", 
        		null, 
        		null, 
        		false, 
        		false,
        		null));
        
        m_queryArray.add(new QueryData(
        		"Show all seasonal vendor products within specified time range", 
        		"SELECT	ingredient.ingredient_name as 'Ingredient', vendor_product.product_id as 'Product ID', " + 
        		"	vendor_product.product_season_start as 'Season Start', vendor_product.product_season_end as 'Season End' " + 
        		"FROM		vendor_product natural join ingredient\r\n" + 
        		"WHERE	vendor_product.product_limited_avail = 1\r\n" + 
        		"AND		vendor_product.product_season_start > ?\r\n" + 
        		"AND		vendor_product.product_season_end < ?\r\n" + 
        		"ORDER BY  	vendor_product.product_season_start;",
        		new String [] {"Start date (yyyy-mm-dd)", "End date (yyy-mm-dd)"}, 
        		new boolean [] {false, false}, 
        		false, true,
                new String[] {"2019-09-01", "2023-04-05"}));
        
        // TODO messed up formatting
        m_queryArray.add(new QueryData(
        		"Show the minimum total amount of miles products need to travel to make a menu item",
        		"Select recipe.recipe_id as 'Recipe ID', recipe.recipe_name as 'Recipe Name', " + 
        		"sum(vendor_product.vendor_miles_from_source + vendor.vendor_distance_miles) as 'Total Min distance traveled' " + 
        		"From " + 
        		"menu_plan_composition natural join menu_item natural join menu_item_has_recipe natural join recipe natural join recipe_has_ingr " + 
        		"natural join ingredient natural join vendor_product natural join vendor " + 
        		"where (vendor_product.vendor_miles_from_source + vendor.vendor_distance_miles) <= " + 
        		"(select min(vendor_product.vendor_miles_from_source + vendor.vendor_distance_miles) " + 
        		"    From vendor_product natural join vendor " + 
        		"	where vendor_product.ingredient_id = ingredient.ingredient_id) " +
        		"group by menu_item_has_recipe.recipe_id " + 
        		"order by sum(vendor_product.vendor_miles_from_source + vendor.vendor_distance_miles) asc",
        		null, 
        		null, 
        		false, 
        		false, 
        		null));
        
        m_queryArray.add(new QueryData(
        		"Show top selling dishes in the last month for all locations", 
        		"SELECT menu_item.menu_item_name as 'Menu Item', sum(item_quantity) as 'Total Dishes Sold'\r\n" + 
        		"FROM customer_check\r\n" + 
        		"	JOIN check_item\r\n" + 
        		"	ON check_item.check_number = customer_check.check_number\r\n" + 
        		"	JOIN menu_item\r\n" + 
        		"	ON menu_item.menu_item_ID = check_item.menu_item_ID\r\n" + 
        		"WHERE (customer_check.check_date >= ('2019-10-09') \r\n" + 
        		"	AND customer_check.check_date <= '2019-11-09')\r\n" + 
        		"GROUP BY menu_item_name\r\n" + 
        		"ORDER BY sum(item_quantity) DESC;",
        		null,
        		null, 
        		false, 
        		false, 
        		null));
        
        m_queryArray.add(new QueryData(
        		"Show all dietary restrictions in database", 
        		"SELECT	dietary_restr_ID as 'Restr. ID', dietary_restr_name as 'Dietary Restriction Name' from dietary_restriction \n" +
        		"ORDER BY dietary_restr_ID;",
        		null,
        		null, 
        		false, 
        		false,
        		null));
        
        // INSERT
        m_queryArray.add(new QueryData(
        		"Add a dietary restriction to database", 
        		"insert into dietary_restriction (dietary_restr_id, dietary_restr_name) values (?,?);",
        		new String [] {"Dietary restriction ID", "Dietary restriction name"}, 
        		new boolean [] {false, false}, 
        		true, 
        		true,
        		new String[] {"120", "Pork"}));    
        
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
    
    public String GetParamDefault(int queryChoice, int parmnum )
    {
       QueryData e=m_queryArray.get(queryChoice);        
       return e.GetQueryDefault(parmnum);
    }
    
    public String GetQueryText(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetQueryString();        
    }
    
    public String[] GetQueryDefaults(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.getQueryDefaults();               
    }
    
    public String GetQueryName(int queryChoice)
    {
    	QueryData e=m_queryArray.get(queryChoice);
    	return e.GetQueryName();
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
        if (!bOK) {
        	m_error = m_jdbcData.GetError(); 
        }
        return bOK;
    }
    
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
