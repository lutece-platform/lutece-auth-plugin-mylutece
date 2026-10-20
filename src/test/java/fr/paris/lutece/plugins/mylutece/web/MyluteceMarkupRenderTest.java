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
package fr.paris.lutece.plugins.mylutece.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.mylutece.business.attribute.AbstractAttribute;
import fr.paris.lutece.plugins.mylutece.business.attribute.MyLuteceUserFieldFilter;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.TemplateHTMLOutputModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Renders, with HTML auto-escaping (LUT-33153), the HTML fragments mylutece builds in Java and the templates that write
 * them: the captcha of the login forms (MyLuteceApp), the login fragments of the page include (UserLoginInclude) and
 * the attribute forms (AbstractAttribute.getHtmlFormAttribute, getHtmlFormSearchAttribute) the mylutece modules write
 * in their user forms. The markup keeps its tags, the data around it is escaped. The core macros the templates call are
 * replaced by stand-ins that write their arguments (escaped) and their nested content: only the FreeMarker engine is
 * needed.
 */
public class MyluteceMarkupRenderTest
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String TEMPLATE_LOGIN_FORM = "skin/plugins/mylutece/login_form.html";
    private static final String TEMPLATE_LOGIN_FORM_MULTI = "skin/plugins/mylutece/login_form_multi.html";
    private static final String TEMPLATE_USER_LOGIN_INCLUDE = "skin/plugins/mylutece/includes/user_login_include.html";
    private static final String TEMPLATE_ATTRIBUTE_TEXT = "admin/plugins/mylutece/attribute/text/html_code_form_attribute_text.html";

    /** The core macros the templates call, written as stand-ins: nested content only, or an element with the argument. */
    private static final String [ ] MACROS_NESTED = {
            "cTpl", "cRow", "cCol", "cForm", "cCard", "cField", "cBtn", "cIcon", "cLink", "cImg", "cRadio", "cInputGroup",
            "cInputGroupAddon", "cInputPassword", "cText", "cInline", "cContainer", "cTitle", "cBreadCrumb", "chList", "chItem",
            "cDropdown"
    };
    private static final String MACROS = "macros.ftl";
    private static final String MACROS_SOURCE = "<#macro cAlert title='' rest...><div role=\"alert\">${title}<#nested></div></#macro>"
            + "<#macro cBlock rest...><div><#nested></div></#macro>" + "<#macro cInput value='' rest...><input value=\"${value}\"></#macro>"
            + "<#macro formGroup labelKey='' rest...><div class=\"form-group\"><label>${labelKey}</label><#nested></div></#macro>"
            + "<#macro input value='' rest...><input value=\"${value}\"></#macro>";

    private static final String CAPTCHA = "<div class=\"captcha\"><img src=\"jcaptcha.jpg\" alt=\"Captcha\"><input name=\"j_captcha_response\"></div>";
    private static final String HOSTILE = "a <b>x</b> \"q\" <script>alert(1)</script>";
    private static final String HOSTILE_ESCAPED = "a &lt;b&gt;x&lt;/b&gt; &quot;q&quot; &lt;script&gt;alert(1)&lt;/script&gt;";

    /**
     * Builds the configuration of the core: HTML output format with auto-escaping.
     *
     * @return the configuration
     * @throws IOException
     *             if the templates directory cannot be read
     */
    private static Configuration configuration( ) throws IOException
    {
        StringBuilder sbMacros = new StringBuilder( MACROS_SOURCE );
        for ( String strMacro : MACROS_NESTED )
        {
            sbMacros.append( "<#macro " ).append( strMacro ).append( " rest...><#nested></#macro>" );
        }
        StringTemplateLoader macros = new StringTemplateLoader( );
        macros.putTemplate( MACROS, sbMacros.toString( ) );
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_0 );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        cfg.setTemplateLoader( new MultiTemplateLoader( new TemplateLoader [ ] {
                macros, new FileTemplateLoader( new File( TEMPLATES_ROOT ) )
        } ) );
        cfg.addAutoInclude( MACROS );

        return cfg;
    }

    /**
     * Renders a template of the plugin.
     *
     * @param strTemplate
     *            the template path
     * @param model
     *            the model
     * @return the HTML
     * @throws IOException
     *             if the template cannot be read
     * @throws TemplateException
     *             if the rendering fails
     */
    private static String render( String strTemplate, Map<String, Object> model ) throws IOException, TemplateException
    {
        StringWriter writer = new StringWriter( );
        configuration( ).getTemplate( strTemplate ).process( model, writer );

        return writer.toString( );
    }

    /**
     * Renders a template given as a string, as a consumer of a fragment writes it.
     *
     * @param strSource
     *            the template source
     * @param model
     *            the model
     * @return the HTML
     * @throws IOException
     *             if the template cannot be parsed
     * @throws TemplateException
     *             if the rendering fails
     */
    private static String renderString( String strSource, Map<String, Object> model ) throws IOException, TemplateException
    {
        StringWriter writer = new StringWriter( );
        new Template( "consumer", strSource, configuration( ) ).process( model, writer );

        return writer.toString( );
    }

    /**
     * @return the model of a login form, as MyLuteceApp builds it, with the captcha enabled and a hostile error detail
     */
    private static Map<String, Object> loginModel( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "url_dologin", "jsp/site/plugins/mylutece/DoMyLuteceLogin.jsp" );
        model.put( "token", "TOKEN" );
        model.put( "error_message", "Invalid login" );
        model.put( "error_detail", HOSTILE );
        model.put( "is_active_captcha", Boolean.TRUE );
        model.put( "captcha", HtmlMarkup.of( CAPTCHA ) );
        model.put( "list_authentications", List.of( ) );

        return model;
    }

    /**
     * The login forms write the captcha of the captcha service as HTML, and escape the error detail read from the
     * request.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testLoginFormsWriteTheCaptchaAsHtml( ) throws Exception
    {
        String strHtml = render( TEMPLATE_LOGIN_FORM, loginModel( ) );

        assertTrue( strHtml.contains( CAPTCHA ), strHtml );
        assertTrue( strHtml.contains( "Invalid login " + HOSTILE_ESCAPED ), strHtml );
        assertFalse( strHtml.contains( "<script>" ), strHtml );
    }

    /**
     * The multi-authentication login form writes the captcha as HTML and escapes the error message.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testMultiLoginFormWritesTheCaptchaAsHtml( ) throws Exception
    {
        Map<String, Object> model = loginModel( );
        model.put( "error_message", HOSTILE );
        model.put( "list_authentications", List.of( Map.of( "name", "mylutece-database", "authServiceName", "Database", "externalAuthentication",
                false, "delegatedAuthentication", false ), Map.of( "name", "mylutece-other", "authServiceName", "Other", "externalAuthentication",
                false, "delegatedAuthentication", false ) ) );

        String strHtml = render( TEMPLATE_LOGIN_FORM_MULTI, model );

        assertTrue( strHtml.contains( CAPTCHA ), strHtml );
        assertTrue( strHtml.contains( HOSTILE_ESCAPED ), strHtml );
        assertFalse( strHtml.contains( "<script>" ), strHtml );
    }

    /**
     * The login fragment UserLoginInclude renders is put in the page model as markup: the page template writes its
     * form as HTML.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testUserLoginIncludeIsWrittenAsHtml( ) throws Exception
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "url_dologin", "jsp/site/plugins/mylutece/DoMyLuteceLogin.jsp" );
        model.put( "token", "TOKEN" );
        String strFragment = render( TEMPLATE_USER_LOGIN_INCLUDE, model );

        String strPage = renderString( "<header>${pageinclude_userlogin}</header>", Map.of( "pageinclude_userlogin", HtmlMarkup.of( strFragment ) ) );

        assertEquals( "<header>" + strFragment + "</header>", strPage );
        assertTrue( strPage.contains( "<input value=\"TOKEN\">" ), strPage );
    }

    /**
     * The attribute forms are markup: a module template writes ${attribute.getHtmlFormAttribute( locale )} without
     * ?no_esc, and the attribute form template escapes the title and the value of the attribute.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testAttributeFormsAreMarkup( ) throws Exception
    {
        assertEquals( TemplateHTMLOutputModel.class, AbstractAttribute.class.getMethod( "getHtmlFormAttribute", Locale.class ).getReturnType( ) );
        assertEquals( TemplateHTMLOutputModel.class, AbstractAttribute.class.getMethod( "getHtmlFormAttribute", Locale.class, List.class ).getReturnType( ) );
        assertEquals( TemplateHTMLOutputModel.class,
                AbstractAttribute.class.getMethod( "getHtmlFormSearchAttribute", MyLuteceUserFieldFilter.class, Locale.class ).getReturnType( ) );

        Map<String, Object> field = Map.of( "idField", 7, "value", HOSTILE, "width", 50, "maxSizeEnter", 20 );
        Map<String, Object> attribute = Map.of( "idAttribute", 3, "title", HOSTILE, "helpMessage", "help", "mandatory", false, "listAttributeFields",
                List.of( field ) );
        String strForm = render( TEMPLATE_ATTRIBUTE_TEXT, Map.of( "attribute", attribute ) );

        assertTrue( strForm.contains( "<label>" + HOSTILE_ESCAPED + "</label>" ), strForm );
        assertTrue( strForm.contains( "<input value=\"" + HOSTILE_ESCAPED + "\">" ), strForm );

        String strPage = renderString( "<form>${form}</form>", Map.of( "form", HtmlMarkup.of( strForm ) ) );

        assertEquals( "<form>" + strForm + "</form>", strPage );
        assertFalse( strPage.contains( "<script>" ), strPage );
    }
}
