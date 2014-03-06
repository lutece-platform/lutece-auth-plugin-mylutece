/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 *
 * IAttributeDAO
 *
 */
public interface IAttributeDAO
{
    /**
     * Load attribute
     * @param nIdAttribute ID
     * @param locale Locale
     * @param plugin The plugin
     * @return Attribute
     */
    IAttribute load( int nIdAttribute, Locale locale, Plugin plugin );

    /**
     * Insert a new attribute
     * @param attribute the attribute
     * @param plugin The plugin
     * @return new PK
     */
    int insert( IAttribute attribute, Plugin plugin );

    /**
     * Update an attribute
     * @param attribute the attribute
     * @param plugin The plugin
     */
    void store( IAttribute attribute, Plugin plugin );

    /**
     * Delete an attribute
     * @param nIdAttribute the ID of the attribute
     * @param plugin The plugin
     */
    void delete( int nIdAttribute, Plugin plugin );

    /**
     * Load every attributes
     * @param locale locale
     * @param plugin The plugin
     * @return list of attributes
     */
    List<IAttribute> selectAll( Locale locale, Plugin plugin );

    /**
     * Load every attributes from plugin name
     * @param strPluginName plugin name
     * @param locale locale
     * @param plugin The plugin
     * @return list of attributes
     */
    List<IAttribute> selectPluginAttributes( String strPluginName, Locale locale, Plugin plugin );

    /**
     * Load every attributes that do not come from a plugin
     * @param locale locale
     * @param plugin The plugin
     * @return list of attributes
     */
    List<IAttribute> selectMyLuteceAttributes( Locale locale, Plugin plugin );

    /**
     * Update the anonymization status of the attribute.
     * @param nIdAttribute Id of the attribute
     * @param bAnonymize New value of the anonymization status. True means the
     *            attribute should be anonymize, false means it doesn't.
     * @param plugin The plugin
     */
    void updateAttributeAnonymization( int nIdAttribute, boolean bAnonymize, Plugin plugin );

    /**
     * Get the anonymization status of a user field.
     * @param plugin The plugin
     * @return A map containing the associations of user field name and a
     *         boolean describing whether the field should be anonymized.
     */
    Map<String, Boolean> selectAnonymizationStatusUserStaticField( Plugin plugin );

    /**
     * Add an anonymization status to a user field.
     * @param strFieldName Name of the field
     * @param bAnonymizeFiled True if the field should be anonymize, false
     *            otherwise
     * @param plugin The plugin
     */
    void addAnonymizationStatusUserField( String strFieldName, boolean bAnonymizeFiled, Plugin plugin );

    /**
     * Remove an anonymization status to a user field.
     * @param strFieldName Name of the field
     * @param plugin The plugin
     */
    void removeAnonymizationStatusUserField( String strFieldName, Plugin plugin );

    /**
     * Update the anonymization status of a user field.
     * @param strFieldName Name of the field to update
     * @param bAnonymizeFiled True if the field should be anonymize, false
     *            otherwise
     * @param plugin The plugin
     */
    void updateAnonymizationStatusUserStaticField( String strFieldName, boolean bAnonymizeFiled, Plugin plugin );
}
