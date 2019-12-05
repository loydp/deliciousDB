/*
 * Alisa Wallace, Peter Loyd, Doug Herstad
 * Seattle University
 * CPSC 5021 Fall 2019
 * Project Milestone 3
 * Based on code provided by Michael McKee of Seattle University
 */

package deliciousDB;

/**
 * QueryData holds all information needed to execute a single query.
 * @author mckeem
 * Enhanced by Doug Herstad, Peter Loyd, and Alisa Wallace
 */
public class QueryData {
    
	/**
	 * Constructor
	 * @param name		name/description of query
	 * @param query		query itself in SQL
	 * @param parms		array of parameter names
	 * @param likeparms	whether each parameter in parms in a LIKE parameter
	 * @param isAction	whether query is an action query (update, insert, delete)
	 * @param isParm	whether the query has any parameters
	 * @param defaults	array of default parameter values (for auto-run)
	 */
     QueryData(String name, String query, String[] parms, boolean [] likeparms, boolean isAction, boolean isParm, String[] defaults)
    {
        queryName = name;
    	m_queryString = query;
        m_arrayParms = parms;
        m_arrayLikeParms = likeparms;
        m_isAction = isAction;
        m_isParms = isParm;
        this.defaults = defaults;
    }
    
    /**
     * Returns query name
     * @return query name
     */
    String GetQueryName() {
    	return queryName;
    }
    
    /**
     * Returns the SQL query as a String
     * @return query
     */
    String GetQueryString()
    {
        return m_queryString;
    }
    
    /**
     * Returns the number of parameters the query has
     * @return number of parameters
     */
    int GetParmAmount()
    {
        if (m_arrayParms == null)
            return 0;
        else
            return m_arrayParms.length;
    }
    
    /**
     * Returns the name of a parameter at a given index
     * @param index		index of parameter name array
     * @return	parameter name
     */
    String GetParamText(int index)
    {
        return m_arrayParms[index];
    }
    
    /**
     * Returns the default parameter value for query at given index
     * @param index		index of parameter name array
     * @return default parameter value
     */
    String GetQueryDefault(int index) {
        return defaults[index];
    }
    
    /**
     * Returns whether a parameter of the query is a LIKE parameter, based on
     * position in parameter array
     * @param index
     * @return true if LIKE parameter, false if not
     */
    boolean GetLikeParam(int index)
    {
        return m_arrayLikeParms[index];
    }
    
    /**
     * Returns array detailing whether parameters are LIKE or not
     * @return LIKE parameter array
     */
    boolean [] GetAllLikeParams()
    {
        return m_arrayLikeParms;
    }
    
    /**
     * Returns whether the query is an action query
     * @return true if action query, false if not
     */
    boolean IsQueryAction()
    {
        return m_isAction;
    }
    
    /**
     * Returns whether the query has parameters
     * @return true if parameters, false if no parameters
     */
    boolean IsQueryParm()
    {
        return m_isParms;
    }
    
    /**
     * Returns default query values
     * @return
     */
    String[] getQueryDefaults() {
        return defaults;
    }
    
    private String[] defaults;
    private String queryName;
    private String m_queryString;
    private String [] m_arrayParms;
    private boolean m_isAction;
    private boolean m_isParms;   
    private boolean [] m_arrayLikeParms;
}
