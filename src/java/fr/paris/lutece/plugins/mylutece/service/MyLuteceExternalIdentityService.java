package fr.paris.lutece.plugins.mylutece.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class MyLuteceExternalIdentityService implements IMyLuteceExternalIdentityService
{

    private static final String PROPERTY_USER_MAPPING_ATTRIBUTES = "mylutece.identity.userMappingAttributes";
    private static final String CONSTANT_LUTECE_USER_PROPERTIES_PATH = "mylutece.attribute";
    private static Map<String, List<String>> ATTRIBUTE_USER_MAPPING;
    private static final String BEAN_MY_LUTECE_IDENTITY_SERVICE="mylutece.myLuteceExternalIdentityService";
    private static volatile IMyLuteceExternalIdentityService _singleton;
    private static final String SEPARATOR = ",";
   

    /**
     * Get the instance of the MyLuteceExternalIdentityService  service
     * 
     * @return the instance of the MyLuteceExternalIdentityService  service
     */
    public static IMyLuteceExternalIdentityService getInstance( )
    {
        if ( _singleton == null )
        {
            synchronized( IMyLuteceExternalIdentityService.class )
            {
                IMyLuteceExternalIdentityService service = SpringContextService.getBean(BEAN_MY_LUTECE_IDENTITY_SERVICE ); 
                _singleton = service;
                String strUserMappingAttributes = AppPropertiesService.getProperty( PROPERTY_USER_MAPPING_ATTRIBUTES );
                ATTRIBUTE_USER_MAPPING = new HashMap<String, List<String>>(  );

                if ( StringUtils.isNotBlank( strUserMappingAttributes ) )
                {
                    String[] tabUserProperties = strUserMappingAttributes.split( SEPARATOR );
                    String userProperties;

                    for ( int i = 0; i < tabUserProperties.length; i++ )
                    {
                        userProperties = AppPropertiesService.getProperty( CONSTANT_LUTECE_USER_PROPERTIES_PATH + "." +
                                tabUserProperties[i] );

                        if ( StringUtils.isNotBlank( userProperties ) )
                        {
                            if(!ATTRIBUTE_USER_MAPPING.containsKey(userProperties))
                            {
                                ATTRIBUTE_USER_MAPPING.put(userProperties,new ArrayList<String>());
                            }
                            ATTRIBUTE_USER_MAPPING.get(userProperties).add(tabUserProperties[i] );
                            
                        }
                    }
                }
            }
        }

        return _singleton;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String,String>getIdentityInformations(String strUserName)
    {
        return getIdentityInformations( strUserName, ATTRIBUTE_USER_MAPPING );
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String,String>getIdentityInformations(String strUserName,Map<String, List<String>> attributeUserMappings)
    {
        Map<String,String> mapIdentityInformationsResult=new HashMap<String, String>( );
        Map<String,String> mapIdentityInformations=null;
        for ( IMyLuteceExternalIdentityProviderService identityProviderService : SpringContextService.getBeansOfType( 
                IMyLuteceExternalIdentityProviderService.class ) )
        {
            mapIdentityInformations=identityProviderService.getIdentityInformations( strUserName );
            break;
        }
        if ( mapIdentityInformations != null )
        {
            for ( Entry<String, String> entry : mapIdentityInformations.entrySet(  ) )
            {
                if ( attributeUserMappings.containsKey( entry.getKey(  ) ) )
                {
                    for(String strUserInfo:attributeUserMappings.get( entry.getKey(  )))
                    {
                        mapIdentityInformationsResult.put(strUserInfo, entry.getValue(  ) );
                    }
                }
            }

            mapIdentityInformationsResult.putAll( mapIdentityInformations );

        }
        return mapIdentityInformationsResult;
    }
    

}
