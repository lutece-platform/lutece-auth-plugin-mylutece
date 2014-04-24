/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.mylutece.business.attribute;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 *
 * AttributeHome
 *
 */
public class AttributeHome
{
    private static IAttributeDAO _dao = SpringContextService.getBean( "mylutece.myLuteceAttributeDAO" );

    /**
     * Load attribute
     * @param nIdAttribute ID Attribute
     * @param locale Locale
     * @param plugin The plugin
     * @return Attribute
     */
    public static IAttribute findByPrimaryKey( int nIdAttribute, Locale locale, Plugin plugin )
    {
        return _dao.load( nIdAttribute, locale, plugin );
    }

    /**
     * Insert an new attribute
     * @param attribute attribute
     * @param plugin The plugin
     * @return new PK
     */
    public static int create( IAttribute attribute, Plugin plugin )
    {
        return _dao.insert( attribute, plugin );
    }

    /**
     * Update an attribute
     * @param attribute the attribute
     * @param plugin The plugin
     */
    public static void update( IAttribute attribute, Plugin plugin )
    {
        _dao.store( attribute, plugin );
    }

    /**
     * Delete an attribute
     * @param nIdAttribute The id of the attribute
     * @param plugin The plugin
     */
    public static void remove( int nIdAttribute, Plugin plugin )
    {
        _dao.delete( nIdAttribute, plugin );
    }

    /**
     * Load every attributes
     * @param locale locale
     * @param plugin The plugin
     * @return list of attributes
     */
    public static List<IAttribute> findAll( Locale locale, Plugin plugin )
    {
        return _dao.selectAll( locale, plugin );
    }

    /**
     * Load every attributes associated to a plugin
     * @param strPluginName plugin name
     * @param locale locale
     * @param plugin The plugin
     * @return list of attributes
     */
    public static List<IAttribute> findPluginAttributes( String strPluginName, Locale locale, Plugin plugin )
    {
        return _dao.selectPluginAttributes( strPluginName, locale, plugin );
    }

    /**
     * Load every attributes that do not come from a plugin
     * @param locale locale
     * @param plugin The plugin
     * @return list of attributes
     */
    public static List<IAttribute> findMyLuteceAttributes( Locale locale, Plugin plugin )
    {
        return _dao.selectMyLuteceAttributes( locale, plugin );
    }

    /**
     * Update the anonymization status of the attribute.
     * @param nIdAttribute Id of the attribute
     * @param bAnonymize New value of the anonymization status. True means the
     *            attribute should be anonymize, false means it doesn't.
     * @param plugin The plugin
     */
    public static void updateAttributeAnonymization( int nIdAttribute, boolean bAnonymize, Plugin plugin )
    {
        _dao.updateAttributeAnonymization( nIdAttribute, bAnonymize, plugin );
    }

    /**
     * Get a map of anonymization status of a user field.
     * @param plugin The plugin
     * @return A map containing the associations of user field name and a
     *         boolean describing whether the field should be anonymized.
     */
    public static Map<String, Boolean> getAnonymizationStatusUserStaticField( Plugin plugin )
    {
        return _dao.selectAnonymizationStatusUserStaticField( plugin );
    }

    /**
     * Add an anonymization status to a user field.
     * @param strFieldName Name of the field
     * @param bAnonymizeFiled True if the field should be anonymize, false
     *            otherwise
     * @param plugin The plugin
     */
    public static void addAnonymizationStatusUserField( String strFieldName, boolean bAnonymizeFiled, Plugin plugin )
    {
        _dao.addAnonymizationStatusUserField( strFieldName, bAnonymizeFiled, plugin );
    }

    /**
     * Remove an anonymization status of a user field.
     * @param strFieldName Name of the field
     * @param plugin The plugin
     */
    public static void removeAnonymizationStatusUserField( String strFieldName, Plugin plugin )
    {
        _dao.removeAnonymizationStatusUserField( strFieldName, plugin );
    }

    /**
     * Update the anonymization status of a user field.
     * @param strFieldName Name of the field to update
     * @param bAnonymizeFiled True if the field should be anonymize, false
     *            otherwise
     * @param plugin The plugin
     */
    public static void updateAnonymizationStatusUserStaticField( String strFieldName, boolean bAnonymizeFiled,
        Plugin plugin )
    {
        _dao.updateAnonymizationStatusUserStaticField( strFieldName, bAnonymizeFiled, plugin );
    }
}
