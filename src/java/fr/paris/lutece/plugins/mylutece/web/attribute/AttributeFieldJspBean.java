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
package fr.paris.lutece.plugins.mylutece.web.attribute;

import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeField;
import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeFieldHome;
import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeHome;
import fr.paris.lutece.plugins.mylutece.business.attribute.IAttribute;
import fr.paris.lutece.plugins.mylutece.business.attribute.MyLuteceUserFieldHome;
import fr.paris.lutece.plugins.mylutece.service.MyLutecePlugin;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.admin.AdminFeaturesPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * AttributeFieldJspBean
 *
 */
public class AttributeFieldJspBean extends AdminFeaturesPageJspBean
{
    // CONSTANTS
    private static final String EMPTY_STRING = "";

    // PROPERTIES
    private static final String PROPERTY_CREATE_ATTRIBUTE_FIELDS_PAGETITLE = "mylutece.create_attribute_field.pageTitle";
    private static final String PROPERTY_MODIFY_ATTRIBUTE_FIELDS_PAGETITLE = "mylutece.modify_attribute_field.pageTitle";
    private static final String PROPERTY_MESSAGE_CONFIRM_REMOVE_ATTRIBUTE_FIELD = "mylutece.modify_attribute.message.removeAttributeField";

    // TEMPLATES
    private static final String TEMPLATE_CREATE_ATTRIBUTE_FIELD = "admin/plugins/mylutece/attribute/create_attribute_field.html";
    private static final String TEMPLATE_MODIFY_ATTRIBUTE_FIELD = "admin/plugins/mylutece/attribute/modify_attribute_field.html";

    // PARAMETERS
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_ID_ATTRIBUTE = "id_attribute";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_VALUE = "value";
    private static final String PARAMETER_DEFAULT_VALUE = "default_value";
    private static final String PARAMETER_ID_FIELD = "id_field";

    // MARKS
    private static final String MARK_ATTRIBUTE_FIELD = "attribute_field";
    private static final String MARK_ATTRIBUTE = "attribute";

    // JSP
    private static final String JSP_MODIFY_ATTRIBUTE = "ModifyAttribute.jsp";
    private static final String JSP_URL_REMOVE_ATTRIBUTE_FIELD = "jsp/admin/plugins/mylutece/attribute/DoRemoveAttributeField.jsp";

    /**
     * Create attribute field
     * @param request HttpServletRequest
     * @return the html form
     */
    public String getCreateAttributeField( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_CREATE_ATTRIBUTE_FIELDS_PAGETITLE );

        String strIdAttribute = request.getParameter( PARAMETER_ID_ATTRIBUTE );
        int nIdAttribute = Integer.parseInt( strIdAttribute );
        Plugin plugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );

        IAttribute attribute = AttributeHome.findByPrimaryKey( nIdAttribute, getLocale(  ), plugin );

        HtmlTemplate template;
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ATTRIBUTE, attribute );

        template = AppTemplateService.getTemplate( TEMPLATE_CREATE_ATTRIBUTE_FIELD, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     *
     * @param request The request
     * @return the html form
     */
    public String doCreateAttributeField( HttpServletRequest request )
    {
        String strIdAttribute = request.getParameter( PARAMETER_ID_ATTRIBUTE );
        int nIdAttribute = Integer.parseInt( strIdAttribute );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strDefaultValue = request.getParameter( PARAMETER_DEFAULT_VALUE );
        String strCancel = request.getParameter( PARAMETER_CANCEL );

        if ( strCancel == null )
        {
            if ( ( strTitle == null ) || strTitle.equals( EMPTY_STRING ) )
            {
                return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
            }

            if ( ( strValue == null ) || strValue.equals( EMPTY_STRING ) )
            {
                return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
            }

            Plugin plugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );
            AttributeField attributeField = new AttributeField(  );
            attributeField.setTitle( strTitle );
            attributeField.setValue( strValue );
            attributeField.setDefaultValue( strDefaultValue != null );

            IAttribute attribute = AttributeHome.findByPrimaryKey( nIdAttribute, getLocale(  ), plugin );
            attributeField.setAttribute( attribute );

            AttributeFieldHome.create( attributeField, plugin );
        }

        String strUrl = JSP_MODIFY_ATTRIBUTE + "?" + PARAMETER_ID_ATTRIBUTE + "=" + nIdAttribute;

        return strUrl;
    }

    /**
     * Modify an attribute field
     * @param request HttpServletRequest
     * @return the html form
     */
    public String getModifyAttributeField( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MODIFY_ATTRIBUTE_FIELDS_PAGETITLE );

        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = Integer.parseInt( strIdField );
        String strIdAttribute = request.getParameter( PARAMETER_ID_ATTRIBUTE );
        int nIdAttribute = Integer.parseInt( strIdAttribute );
        Plugin plugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );

        IAttribute attribute = AttributeHome.findByPrimaryKey( nIdAttribute, getLocale(  ), plugin );

        AttributeField attributeField = AttributeFieldHome.findByPrimaryKey( nIdField, plugin );

        HtmlTemplate template;
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ATTRIBUTE_FIELD, attributeField );
        model.put( MARK_ATTRIBUTE, attribute );

        template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_ATTRIBUTE_FIELD, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Modify an attribute field
     * @param request HttpServletRequest
     * @return The Jsp URL of the process result
     */
    public String doModifyAttributeField( HttpServletRequest request )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strDefaultValue = request.getParameter( PARAMETER_DEFAULT_VALUE );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = Integer.parseInt( strIdField );
        String strIdAttribute = request.getParameter( PARAMETER_ID_ATTRIBUTE );
        String strCancel = request.getParameter( PARAMETER_CANCEL );

        if ( strCancel == null )
        {
            if ( ( strTitle == null ) || strTitle.equals( EMPTY_STRING ) )
            {
                return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
            }

            if ( ( strValue == null ) || strValue.equals( EMPTY_STRING ) )
            {
                return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
            }

            Plugin plugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );

            AttributeField attributeField = new AttributeField(  );
            attributeField.setIdField( nIdField );
            attributeField.setTitle( strTitle );
            attributeField.setValue( strValue );
            attributeField.setDefaultValue( strDefaultValue != null );

            AttributeFieldHome.update( attributeField, plugin );
        }

        String strUrl = JSP_MODIFY_ATTRIBUTE + "?" + PARAMETER_ID_ATTRIBUTE + "=" + strIdAttribute;

        return strUrl;
    }

    /**
     * Confirm the removal of the attribute field
     * @param request HttpServletRequest
     * @return the html form
     */
    public String doConfirmRemoveAttributeField( HttpServletRequest request )
    {
        String strIdAttribute = request.getParameter( PARAMETER_ID_ATTRIBUTE );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        String strUrlRemove = JSP_URL_REMOVE_ATTRIBUTE_FIELD + "?" + PARAMETER_ID_ATTRIBUTE + "=" + strIdAttribute +
            "&" + PARAMETER_ID_FIELD + "=" + strIdField;

        String strUrl = AdminMessageService.getMessageUrl( request, PROPERTY_MESSAGE_CONFIRM_REMOVE_ATTRIBUTE_FIELD,
                strUrlRemove, AdminMessage.TYPE_CONFIRMATION );

        return strUrl;
    }

    /**
     * Remove the attribute field
     * @param request HttpServletRequest
     * @return The Jsp URL of the process result
     */
    public String doRemoveAttributeField( HttpServletRequest request )
    {
        String strIdAttribute = request.getParameter( PARAMETER_ID_ATTRIBUTE );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = Integer.parseInt( strIdField );
        Plugin plugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );

        AttributeFieldHome.remove( nIdField, plugin );
        MyLuteceUserFieldHome.removeUserFieldsFromIdField( nIdField, plugin );

        String strUrl = JSP_MODIFY_ATTRIBUTE + "?" + PARAMETER_ID_ATTRIBUTE + "=" + strIdAttribute;

        return strUrl;
    }

    /**
     * Move up the position of the attribute field
     * @param request HttpServletRequest
     * @return The Jsp URL of the process result
     */
    public String doMoveUpAttributeField( HttpServletRequest request )
    {
        String strIdAttribute = request.getParameter( PARAMETER_ID_ATTRIBUTE );
        int nIdAttribute = Integer.parseInt( strIdAttribute );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = Integer.parseInt( strIdField );
        Plugin plugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );

        List<AttributeField> listAttributeFields = AttributeFieldHome.selectAttributeFieldsByIdAttribute( nIdAttribute,
                plugin );
        AttributeField previousField = null;
        AttributeField currentField = null;

        Iterator<AttributeField> it = listAttributeFields.iterator(  );
        previousField = it.next(  );
        currentField = it.next(  );

        while ( it.hasNext(  ) && ( currentField.getIdField(  ) != nIdField ) )
        {
            previousField = currentField;
            currentField = it.next(  );
        }

        int previousFieldPosition = previousField.getPosition(  );
        int currentFieldPosition = currentField.getPosition(  );
        previousField.setPosition( currentFieldPosition );
        currentField.setPosition( previousFieldPosition );

        AttributeFieldHome.update( previousField, plugin );
        AttributeFieldHome.update( currentField, plugin );

        String strUrl = JSP_MODIFY_ATTRIBUTE + "?" + PARAMETER_ID_ATTRIBUTE + "=" + strIdAttribute;

        return strUrl;
    }

    /**
     * Move down the position of the attribute field
     * @param request HttpServletRequest
     * @return The Jsp URL of the process result
     */
    public String doMoveDownAttributeField( HttpServletRequest request )
    {
        String strIdAttribute = request.getParameter( PARAMETER_ID_ATTRIBUTE );
        int nIdAttribute = Integer.parseInt( strIdAttribute );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = Integer.parseInt( strIdField );
        Plugin plugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );

        List<AttributeField> listAttributeFields = AttributeFieldHome.selectAttributeFieldsByIdAttribute( nIdAttribute,
                plugin );
        AttributeField currentField = null;
        AttributeField nextField = null;

        Iterator<AttributeField> it = listAttributeFields.iterator(  );
        currentField = it.next(  );
        nextField = it.next(  );

        while ( it.hasNext(  ) && ( currentField.getIdField(  ) != nIdField ) )
        {
            currentField = nextField;
            nextField = it.next(  );
        }

        int nextFieldPosition = nextField.getPosition(  );
        int currentFieldPosition = currentField.getPosition(  );
        nextField.setPosition( currentFieldPosition );
        currentField.setPosition( nextFieldPosition );

        AttributeFieldHome.update( nextField, plugin );
        AttributeFieldHome.update( currentField, plugin );

        String strUrl = JSP_MODIFY_ATTRIBUTE + "?" + PARAMETER_ID_ATTRIBUTE + "=" + strIdAttribute;

        return strUrl;
    }
}
