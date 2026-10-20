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
package fr.paris.lutece.plugins.mylutece.web.security;

import java.io.File;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.mylutece.service.security.AuthenticationFilterService;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

/**
 * The delete link of a public URL and the URL of its confirmation give back the exact code of the public URL, whatever
 * characters it holds (the code is free text): the code is URL-encoded where it is put into a query string.
 */
public class PublicUrlRemoveLinkTest extends LuteceTestCase
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String TEMPLATE_PARAMETERS = "/admin/plugins/mylutece/security/authentication_filter_advanced_parameters.html";
    private static final String PAGE = "public_url_page.ftl";
    private static final String PAGE_SOURCE = "<#include '" + TEMPLATE_PARAMETERS + "'><@myluteceAuthFilterAdvParamsMainForm />";
    /** Stand-ins of the core macros the macro calls: aButton writes its href, the others their nested content. */
    private static final String MACROS = "mylutece_public_url_test_macros.ftl";
    private static final String MACROS_SOURCE = "<#macro h rest...><#nested></#macro><#macro tform rest...><#nested></#macro>"
            + "<#macro input rest...></#macro><#macro span rest...><#nested></#macro><#macro row rest...><#nested></#macro>"
            + "<#macro columns rest...><#nested></#macro><#macro formGroup rest...><#nested></#macro><#macro inputGroup rest...><#nested></#macro>"
            + "<#macro button rest...></#macro><#macro empty rest...></#macro>"
            + "<#macro aButton href='' rest...><a href=\"${href}\"></a></#macro>";
    private static final String PARAMETER_PUBLIC_URL_CODE = "public_url_code";
    private static final String [ ] CODES = {
            "news&events", "faq#top", "a+b=c%20", "page"
    };

    /**
     * Parses a URL as a browser and a servlet container do: HTML entities decoded, fragment dropped, parameters split on &
     * and URL-decoded.
     *
     * @param strUrl
     *            the URL
     * @return the parameters of the query string
     */
    private static Map<String, List<String>> parameters( String strUrl )
    {
        String strQuery = strUrl.replace( "&amp;", "&" );
        int nFragment = strQuery.indexOf( '#' );
        strQuery = ( nFragment >= 0 ) ? strQuery.substring( 0, nFragment ) : strQuery;
        strQuery = strQuery.substring( strQuery.indexOf( '?' ) + 1 );
        Map<String, List<String>> mapParameters = new HashMap<>( );
        for ( String strPair : strQuery.split( "&" ) )
        {
            int nEquals = strPair.indexOf( '=' );
            String strName = URLDecoder.decode( ( nEquals >= 0 ) ? strPair.substring( 0, nEquals ) : strPair, StandardCharsets.UTF_8 );
            String strValue = ( nEquals >= 0 ) ? URLDecoder.decode( strPair.substring( nEquals + 1 ), StandardCharsets.UTF_8 ) : "";
            mapParameters.computeIfAbsent( strName, k -> new ArrayList<>( ) ).add( strValue );
        }

        return mapParameters;
    }

    /**
     * The delete link of each public URL of the list carries its exact code, and nothing else.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testDeleteLinkCarriesTheExactCode( ) throws Exception
    {
        List<Map<String, String>> listPublicUrls = new ArrayList<>( );
        for ( String strCode : CODES )
        {
            listPublicUrls.add( Map.of( "code", strCode, "name", "jsp/site/Portal.jsp?page=x" ) );
        }
        StringTemplateLoader stubs = new StringTemplateLoader( );
        stubs.putTemplate( MACROS, MACROS_SOURCE );
        stubs.putTemplate( PAGE, PAGE_SOURCE );
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_0 );
        cfg.setURLEscapingCharset( "UTF-8" );
        cfg.setTemplateLoader( new MultiTemplateLoader( new TemplateLoader [ ] {
                stubs, new FileTemplateLoader( new File( TEMPLATES_ROOT ) )
        } ) );
        cfg.addAutoInclude( MACROS );
        Map<String, Object> model = new HashMap<>( );
        model.put( AuthenticationFilterService.MARK_PUBLIC_LIST_URL, listPublicUrls );
        StringWriter writer = new StringWriter( );
        cfg.getTemplate( PAGE ).process( model, writer );

        Matcher matcher = Pattern.compile( "href=\"([^\"]*ConfirmRemovePublicUrl[^\"]*)\"" ).matcher( writer.toString( ) );
        List<String> listLinks = new ArrayList<>( );
        while ( matcher.find( ) )
        {
            listLinks.add( matcher.group( 1 ) );
        }
        assertEquals( CODES.length, listLinks.size( ), writer.toString( ) );
        for ( int i = 0; i < CODES.length; i++ )
        {
            assertEquals( Map.of( PARAMETER_PUBLIC_URL_CODE, List.of( CODES [i] ) ), parameters( listLinks.get( i ) ), listLinks.get( i ) );
        }
    }

    /**
     * The OK button of the confirmation targets the removal of the exact code, and nothing else.
     *
     * @throws Exception
     *             if the confirmation fails
     */
    @Test
    public void testConfirmationUrlCarriesTheExactCode( ) throws Exception
    {
        for ( String strCode : CODES )
        {
            MockHttpServletRequest request = new MockHttpServletRequest( );
            AdminUser user = new AdminUser( );
            user.addRoles( Map.of( "mylutece_manager", new RBACRole( "mylutece_manager", "MyLutece manager" ) ) );
            AdminUserUtils.registerAdminUserWithRight( request, user, AuthenticationFilterJspBean.RIGHT_MANAGE_AUTHENTICATION_FILTER );
            request.addParameter( PARAMETER_PUBLIC_URL_CODE, strCode );
            AuthenticationFilterJspBean jspBean = new AuthenticationFilterJspBean( );
            jspBean.init( request, AuthenticationFilterJspBean.RIGHT_MANAGE_AUTHENTICATION_FILTER );

            jspBean.getConfirmRemovePublicUrl( request );

            String strUrl = AdminMessageService.getMessage( request ).getUrl( );
            assertEquals( Map.of( PARAMETER_PUBLIC_URL_CODE, List.of( strCode ) ), parameters( strUrl ), strUrl );
        }
    }
}
