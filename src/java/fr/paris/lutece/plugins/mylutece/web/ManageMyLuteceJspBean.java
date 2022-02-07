/*
 * Copyright (c) 2002-2022, City of Paris
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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.mylutece.authentication.MultiLuteceAuthentication;
import fr.paris.lutece.plugins.mylutece.business.LuteceUserRoleDescription;
import fr.paris.lutece.plugins.mylutece.service.ILuteceUserRolesProvidedDescription;
import fr.paris.lutece.plugins.mylutece.service.IMyLuteceExternalIdentityProviderService;
import fr.paris.lutece.plugins.mylutece.service.IMyLuteceExternalRolesProvider;
import fr.paris.lutece.plugins.mylutece.service.MyLuteceExternalIdentityService;
import fr.paris.lutece.plugins.mylutece.service.MyluteceExternalRoleService;
import fr.paris.lutece.plugins.mylutece.service.RoleResourceIdService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

/**
 * This class provides the user interface to manage Administration features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageMylutece.jsp", controllerPath = "jsp/admin/plugins/mylutece/", right = "MYLUTECE_MANAGEMENT" )
public class ManageMyLuteceJspBean extends AbstractManageMyluteceJspBean
{
    // Templates
    private static final String TEMPLATE_MANAGE_MYLUTECE = "/admin/plugins/mylutece/manage_mylutece.html";
   

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_MYLUTECE = "mylutece.adminFeature.mylutece_management.name";
  
  
    // Views
    private static final String VIEW_MANAGE_MYLUTECE = "manageMylutece";
   
    //MARKERS
    private static final String MARK_LUTECE_AUTHENTICATION = "authentication";
    private static final String MARK_MAP_AUTH_ROLE_DESCRIPTION= "map_auth_role_description";
    private static final String MARK_MAP_EXTERNAL_PROVIDER_ROLE_DESCRIPTION= "map_external_provider_role_description";
    private static final String MARK_LOCALE = "locale";
    
    
    
    private static final String MARK_EXTERNAL_ROLE_PROVIDERS= "external_roles_providers";
    private static final String MARK_EXTERNAL_IDENTITY_PROVIDERS= "external_identity_providers";
    
    
    
    
    

    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_MYLUTECE, defaultView = true )
    public String getManageMylutece( HttpServletRequest request )
    {
    	
    	Map<String,Collection<LuteceUserRoleDescription>> mapAuthRolesDescrition=new HashMap<String, Collection<LuteceUserRoleDescription>>();
    	Map<String,Collection<LuteceUserRoleDescription>> mapExternalProviderRolesDescription=new HashMap<String, Collection<LuteceUserRoleDescription>>();
    	
    	LuteceAuthentication luteceAuthentication=SecurityService.getInstance().getAuthenticationService();
    	
    	//filter role description by user permission
    	if(luteceAuthentication.isMultiAuthenticationSupported())
    	{
    		for(LuteceAuthentication multiAuth:((MultiLuteceAuthentication)luteceAuthentication).getListLuteceAuthentication())
    		{
    			if(multiAuth instanceof ILuteceUserRolesProvidedDescription )
    			{
    				mapAuthRolesDescrition.put(multiAuth.getAuthServiceName(),filterRolesDescriptionByUser((ILuteceUserRolesProvidedDescription)multiAuth));
    			}
    		}
    		
    	}
    	else
    	{
    		if(luteceAuthentication instanceof ILuteceUserRolesProvidedDescription )
			{
    			mapAuthRolesDescrition.put(luteceAuthentication.getAuthServiceName(),filterRolesDescriptionByUser((ILuteceUserRolesProvidedDescription)luteceAuthentication));
			}
    		
    	}
    	
    	List<IMyLuteceExternalRolesProvider>  listMyluteceExternalRolesProvider= MyluteceExternalRoleService.getInstance().getProviders();
    	List<IMyLuteceExternalIdentityProviderService>  listMyluteceExternalIdentiryProvider= MyLuteceExternalIdentityService.getInstance().getProviders();
    	
    	 int nCpt=0;
 		//filter role description
    	 for(IMyLuteceExternalRolesProvider externalRoleProvider: listMyluteceExternalRolesProvider)
		 {
			mapExternalProviderRolesDescription.put(Integer.toString(nCpt),filterRolesDescriptionByUser(externalRoleProvider));
			
		}
    	
    	
    	 Map<String, Object> model = getModel(  );
    	 model.put( MARK_LOCALE, getLocale( ) );
    	 
    	 
    	 model.put(MARK_LUTECE_AUTHENTICATION, luteceAuthentication);
    	 model.put(MARK_EXTERNAL_ROLE_PROVIDERS, listMyluteceExternalRolesProvider);
    	 model.put(MARK_EXTERNAL_IDENTITY_PROVIDERS, listMyluteceExternalIdentiryProvider);
      	 model.put(MARK_MAP_AUTH_ROLE_DESCRIPTION, mapAuthRolesDescrition);
    	 model.put(MARK_MAP_EXTERNAL_PROVIDER_ROLE_DESCRIPTION, mapExternalProviderRolesDescription);
    	 
    	 
    	
    	 
    	
    	 
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_MYLUTECE, TEMPLATE_MANAGE_MYLUTECE, model );
    }
    
    
    private <T extends ILuteceUserRolesProvidedDescription> Collection<LuteceUserRoleDescription> filterRolesDescriptionByUser( T luteceUserRolesProvidedDescription)
    {
    	
    	Collection<LuteceUserRoleDescription>  rolesDescription=luteceUserRolesProvidedDescription.getLuteceUserRolesProvided(getLocale());
    	if(rolesDescription!=null)
    	{
    		rolesDescription = RBACService.getAuthorizedCollection( rolesDescription, RoleResourceIdService.PERMISSION_ASSIGN_ROLE, (User) getUser() );
    		rolesDescription = AdminWorkgroupService.getAuthorizedCollection( rolesDescription, (User) getUser( ) );
    	}
    	return rolesDescription;
    }

}
