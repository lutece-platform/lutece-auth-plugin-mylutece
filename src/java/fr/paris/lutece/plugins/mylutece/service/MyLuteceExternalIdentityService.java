/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.mylutece.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.mylutece.business.LuteceUserAttributeDescription;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class MyLuteceExternalIdentityService implements IMyLuteceExternalIdentityService
{

    private static final String PROPERTY_USER_MAPPING_ATTRIBUTES = "mylutece.identity.userMappingAttributes";
    private static final String CONSTANT_LUTECE_USER_PROPERTIES_PATH = "mylutece.attribute";
    private static Map<String, List<String>> ATTRIBUTE_USER_MAPPING;
    private static final String BEAN_MY_LUTECE_IDENTITY_SERVICE = "mylutece.myLuteceExternalIdentityService";
    private static volatile IMyLuteceExternalIdentityService _singleton;
    private static final String SEPARATOR = ",";

    /**
     * Get the instance of the MyLuteceExternalIdentityService service
     * 
     * @return the instance of the MyLuteceExternalIdentityService service
     */
    public static IMyLuteceExternalIdentityService getInstance( )
    {
        if ( _singleton == null )
        {
            synchronized( IMyLuteceExternalIdentityService.class )
            {
                IMyLuteceExternalIdentityService service = SpringContextService.getBean( BEAN_MY_LUTECE_IDENTITY_SERVICE );
                _singleton = service;
                String strUserMappingAttributes = AppPropertiesService.getProperty( PROPERTY_USER_MAPPING_ATTRIBUTES );
                ATTRIBUTE_USER_MAPPING = new HashMap<String, List<String>>( );

                if ( StringUtils.isNotBlank( strUserMappingAttributes ) )
                {
                    String [ ] tabUserProperties = strUserMappingAttributes.split( SEPARATOR );
                    String userProperties;

                    for ( int i = 0; i < tabUserProperties.length; i++ )
                    {
                        userProperties = AppPropertiesService.getProperty( CONSTANT_LUTECE_USER_PROPERTIES_PATH + "." + tabUserProperties [i] );

                        if ( StringUtils.isNotBlank( userProperties ) )
                        {
                            if ( !ATTRIBUTE_USER_MAPPING.containsKey( userProperties ) )
                            {
                                ATTRIBUTE_USER_MAPPING.put( userProperties, new ArrayList<String>( ) );
                            }
                            ATTRIBUTE_USER_MAPPING.get( userProperties ).add( tabUserProperties [i] );

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
    public Map<String, String> getIdentityInformations( String strUserName )
    {
        return getIdentityInformations( strUserName, ATTRIBUTE_USER_MAPPING );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getIdentityInformations( String strUserName, Map<String, List<String>> attributeUserMappings )
    {
        Map<String, String> mapIdentityInformationsResult = new HashMap<String, String>( );
        Map<String, String> mapIdentityInformations = null;
        for ( IMyLuteceExternalIdentityProviderService identityProviderService : getProviders() )
        {
            mapIdentityInformations = identityProviderService.getIdentityInformations( strUserName );
            break;
        }
        if ( mapIdentityInformations != null )
        {
            for ( Entry<String, String> entry : mapIdentityInformations.entrySet( ) )
            {
                if ( attributeUserMappings.containsKey( entry.getKey( ) ) )
                {
                    for ( String strUserInfo : attributeUserMappings.get( entry.getKey( ) ) )
                    {
                        mapIdentityInformationsResult.put( strUserInfo, entry.getValue( ) );
                    }
                }
            }

            mapIdentityInformationsResult.putAll( mapIdentityInformations );

        }
        return mapIdentityInformationsResult;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<LuteceUserAttributeDescription> getDefaulLuteceUserAttributeDescription(Locale locale)
    {
    	
    	List<LuteceUserAttributeDescription> listUserDescription=  new ArrayList<LuteceUserAttributeDescription>();
    
    	
    	String strUserMappingAttributes = AppPropertiesService.getProperty( PROPERTY_USER_MAPPING_ATTRIBUTES );
        ATTRIBUTE_USER_MAPPING = new HashMap<String, List<String>>( );

        if ( StringUtils.isNotBlank( strUserMappingAttributes ) )
        {
            String [ ] tabUserProperties = strUserMappingAttributes.split( SEPARATOR );
            
            for ( int i = 0; i < tabUserProperties.length; i++ )
            {
                 
                  listUserDescription.add(new LuteceUserAttributeDescription( tabUserProperties [i],  AppPropertiesService.getProperty( CONSTANT_LUTECE_USER_PROPERTIES_PATH + "." + tabUserProperties [i] ) , ""));
            }
        }
    	
         	
        return listUserDescription;
    }
    
    /**
	 * Gets the providers.
	 *
	 * @return the providers
	 */
	public List<IMyLuteceExternalIdentityProviderService> getProviders()
	{
		return  SpringContextService
                .getBeansOfType( IMyLuteceExternalIdentityProviderService.class );
		
	}

}
