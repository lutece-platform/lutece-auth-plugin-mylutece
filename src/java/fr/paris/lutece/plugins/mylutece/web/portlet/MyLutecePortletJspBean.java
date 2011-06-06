/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.mylutece.web.portlet;

import fr.paris.lutece.plugins.mylutece.business.portlet.MyLutecePortlet;
import fr.paris.lutece.plugins.mylutece.business.portlet.MyLutecePortletHome;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage MyLutece Portlet
 */
public class MyLutecePortletJspBean extends PortletJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // Right
    public static final String RIGHT_MANAGE_ADMIN_SITE = "CORE_ADMIN_SITE";
    private static final String PARAMETER_PAGE_ID = "page_id";
    private static final String PARAMETER_PORTLET_ID = "portlet_id";
    private static final String PARAMETER_PORTLET_TYPE_ID = "portlet_type_id";

    /**
     * Creates a new MyLutecePortletJspBean object.
     */
    public MyLutecePortletJspBean(  )
    {
    }

    /**
     * Returns the Download portlet creation form
     *
     * @param request The http request
     * @return The HTML form
     */
    public String getCreate( HttpServletRequest request )
    {
        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );
        String strIdPortletType = request.getParameter( PARAMETER_PORTLET_TYPE_ID );
        HtmlTemplate template = getCreateTemplate( strIdPage, strIdPortletType );

        return template.getHtml(  );
    }

    /**
     * Returns the Download portlet modification form
     *
     * @param request The Http request
     * @return The HTML form
     */
    public String getModify( HttpServletRequest request )
    {
        String strIdPortlet = request.getParameter( PARAMETER_PORTLET_ID );
        int nIdPortlet = Integer.parseInt( strIdPortlet );
        MyLutecePortlet portlet = (MyLutecePortlet) PortletHome.findByPrimaryKey( nIdPortlet );
        HtmlTemplate template = getModifyTemplate( portlet );

        return template.getHtml(  );
    }

    /**
     * Process portlet's creation
     *
     * @param request The Http request
     * @return The Jsp management URL of the process result
     */
    public String doCreate( HttpServletRequest request )
    {
        MyLutecePortlet portlet = new MyLutecePortlet(  );

        // get portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        int nIdPage = Integer.parseInt( request.getParameter( PARAMETER_PAGE_ID ) );
        portlet.setPageId( nIdPage );

        //Portlet creation
        MyLutecePortletHome.getInstance(  ).create( portlet );

        //Displays the page with the new Portlet
        return getPageUrl( nIdPage );
    }

    /**
     * Process portlet's modification
     *
     * @param request The http request
     * @return Management's Url
     */
    public String doModify( HttpServletRequest request )
    {
        //recovery of the portlet
        String strIdPortlet = request.getParameter( PARAMETER_PORTLET_ID );
        int nIdPortlet = Integer.parseInt( strIdPortlet );
        MyLutecePortlet portlet = (MyLutecePortlet) PortletHome.findByPrimaryKey( nIdPortlet );

        // get portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        //Update of the portlet
        portlet.update(  );

        //Displays the page with the updated portlet
        return getPageUrl( portlet.getPageId(  ) );
    }
}
