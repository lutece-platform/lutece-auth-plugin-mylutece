package fr.paris.lutece.plugins.mylutece.business.attribute;

import fr.paris.lutece.plugins.mylutece.service.MyLutecePlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * MyLuteceUserFieldFilter
 *
 */
public class MyLuteceUserFieldFilter 
{
	// CONSTANTS
	private static final String EMPTY_STRING = "";
	private static final String CONSTANT_ESPERLUETTE = "&";
	private static final String CONSTANT_EQUAL = "=";
	private static final String CONSTANT_UNDERSCORE = "_";
	
	// PARAMETERS
	private static final String PARAMETER_SEARCH_IS_SEARCH = "search_is_search";
	private static final String PARAMETER_ATTRIBUTE = "attribute";
	
	// PROPERTIES
	private static final String PROPERTY_ENCODING_URL = "lutece.encoding.url";
	
	private List<MyLuteceUserField> _listUserFields;
	
	/**
	 * Get list user fields
	 * @return list user fields
	 */
	public List<MyLuteceUserField> getListUserFields(  )
	{
		return _listUserFields;
	}
	
	/**
	 * Set list user fields
	 * @param listUserFields list user fields
	 */
	public void setListUserFields( List<MyLuteceUserField> listUserFields )
	{
		_listUserFields = listUserFields;
	}
	
	/**
	 * Set admin user field filter
	 * @param request HttpServletRequest
	 * @param locale locale
	 */
	public void setMyLuteceUserFieldFilter( HttpServletRequest request, Locale locale )
	{
		_listUserFields = new ArrayList<MyLuteceUserField>(  );
		String strIsSearch = request.getParameter( PARAMETER_SEARCH_IS_SEARCH );
		
		if ( strIsSearch != null )
		{
			Plugin plugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );
			List<IAttribute> listAttributes = AttributeHome.findAll( locale, plugin );
	        for ( IAttribute attribute : listAttributes )
	        {
	        	for ( MyLuteceUserField userField : attribute.getUserFieldsData( request, 0 ) )
	        	{
	        		if ( userField != null && !userField.getValue(  ).equals( EMPTY_STRING ) )
	        		{
	        			_listUserFields.add( userField );
	        		}
	        	}
	        }
	        
		}
	}

    /**
     * Build url attributes
     * @param url the url
     */
    public void setUrlAttributes( UrlItem url )
    {
    	for ( MyLuteceUserField userField : _listUserFields )
    	{
    		try
        	{
        		url.addParameter( PARAMETER_ATTRIBUTE + CONSTANT_UNDERSCORE + userField.getAttribute(  ).getIdAttribute(  ), 
        				URLEncoder.encode( userField.getValue(  ), AppPropertiesService.getProperty( PROPERTY_ENCODING_URL ) ) );
        	}
        	catch( UnsupportedEncodingException e )
        	{
        		e.printStackTrace(  );
        	}
    	}
    }
    
    /**
     * Build url attributes
     * @return the url attributes
     */
    public String getUrlAttributes(  )
    {
    	StringBuilder sbUrlAttributes = new StringBuilder(  );
    	
    	for ( MyLuteceUserField userField : _listUserFields )
    	{
    		if ( userField.getAttribute(  ).getAttributeType(  ).getClassName(  ).equals( AttributeComboBox.class.getName(  ) ) )
    		{
    			sbUrlAttributes.append( CONSTANT_ESPERLUETTE + PARAMETER_ATTRIBUTE + CONSTANT_UNDERSCORE + userField.getAttribute(  ).getIdAttribute(  ) + CONSTANT_EQUAL +
    					userField.getAttributeField(  ).getIdField(  ) );
    		}
    		else 
    		{
    			try
            	{
            		sbUrlAttributes.append( CONSTANT_ESPERLUETTE + PARAMETER_ATTRIBUTE + CONSTANT_UNDERSCORE + userField.getAttribute(  ).getIdAttribute(  ) + CONSTANT_EQUAL +
            				URLEncoder.encode( userField.getValue(  ), AppPropertiesService.getProperty( PROPERTY_ENCODING_URL ) ) );
            	}
            	catch( UnsupportedEncodingException e )
            	{
            		e.printStackTrace(  );
            	}
    		}
    	}
    	
    	return sbUrlAttributes.toString(  );
    }
}
