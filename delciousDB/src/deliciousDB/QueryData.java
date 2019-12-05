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
 *
 * @author mckeem
 */
public class QueryData {
     QueryData()
    {
    }
//    QueryData(String query)
//    {
//        m_queryString = query;
//    }
    
    QueryData(String name, String query, String[] parms, boolean [] likeparms, boolean isAction, boolean isParm, String[] defaults)
    {
        queryName = name;
    	m_queryString = query;
        m_arrayParms = parms;
        m_arrayLikeParms = likeparms;
        m_isAction = isAction;
        m_isParms = isParm;
        m_defaults = defaults;
    }
    
//    void Set(String query, ArrayList<String>parms, boolean isAction, boolean isParm)
//    {
//        m_queryString = query;
//        m_arrayParms = parms;
//        m_isAction = isAction;
//        m_isParms = isParm;
//    }
    
    String GetQueryName() {
    	return queryName;
    }
    
    String GetQueryString()
    {
        return m_queryString;
    }
    
    int GetParmAmount()
    {
        if (m_arrayParms == null)
            return 0;
        else
            return m_arrayParms.length;
    }
    
  
    String GetParamText(int index)
    {
        return m_arrayParms[index];
    }
    
    String GetQueryDefault(int index) {
        return m_defaults[index];
    }
    
    boolean GetLikeParam(int index)
    {
        return m_arrayLikeParms[index];
    }
    
    boolean [] GetAllLikeParams()
    {
        return m_arrayLikeParms;
    }
    
    boolean IsQueryAction()
    {
        return m_isAction;
    }
    
    boolean IsQueryParm()
    {
        return m_isParms;
    }
    
    String[] getQueryDefaults() {
        return m_defaults;
    }
    private String[] m_defaults;
    private String queryName;
    private String m_queryString;
    private String [] m_arrayParms;
    private boolean m_isAction;
    private boolean m_isParms;   
    private boolean [] m_arrayLikeParms;
}
