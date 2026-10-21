/*
 * Copyright (c) 2002-2026, City of Paris
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

import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeText;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

/**
 * Tests the screen the button of an error message of the attributes back office opens when there is no page to go back to.
 */
public class AttributeJspBeanTest extends LuteceTestCase
{
    @Inject
    private AttributeJspBean _attributeJspBean;
    @Inject
    private AttributeFieldJspBean _attributeFieldJspBean;

    /**
     * An attribute created without title returns to the form creating an attribute of the same type.
     */
    @Test
    public void testDoCreateAttributeWithoutTitleReturnsToTheCreationForm( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "attribute_type_class_name", AttributeText.class.getName( ) );
        request.addParameter( "width", "10" );

        String strUrl = _attributeJspBean.doCreateAttribute( request );

        assertTrue( strUrl.endsWith( AdminMessageService.getMessageRelativeUrl( ) ), strUrl );
        String strBackUrl = AdminMessageService.getMessage( request ).getBackUrl( );
        assertTrue( strBackUrl.endsWith( "jsp/admin/plugins/mylutece/attribute/CreateAttribute.jsp?attribute_type_class_name=" + AttributeText.class.getName( ) ),
                strBackUrl );
    }

    /**
     * A field of an attribute created without value returns to the form creating a field of this attribute.
     */
    @Test
    public void testDoCreateAttributeFieldWithoutValueReturnsToTheCreationForm( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "id_attribute", "5" );
        request.addParameter( "title", "title" );

        String strUrl = _attributeFieldJspBean.doCreateAttributeField( request );

        assertTrue( strUrl.endsWith( AdminMessageService.getMessageRelativeUrl( ) ), strUrl );
        String strBackUrl = AdminMessageService.getMessage( request ).getBackUrl( );
        assertTrue( strBackUrl.endsWith( "jsp/admin/plugins/mylutece/attribute/CreateAttributeField.jsp?id_attribute=5" ), strBackUrl );
    }

    /**
     * A field of an attribute modified without title returns to the form modifying this field.
     */
    @Test
    public void testDoModifyAttributeFieldWithoutTitleReturnsToTheModificationForm( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "id_attribute", "5" );
        request.addParameter( "id_field", "7" );
        request.addParameter( "value", "value" );

        String strUrl = _attributeFieldJspBean.doModifyAttributeField( request );

        assertTrue( strUrl.endsWith( AdminMessageService.getMessageRelativeUrl( ) ), strUrl );
        String strBackUrl = AdminMessageService.getMessage( request ).getBackUrl( );
        assertTrue( strBackUrl.endsWith( "jsp/admin/plugins/mylutece/attribute/ModifyAttributeField.jsp?id_attribute=5&id_field=7" ), strBackUrl );
    }
}
