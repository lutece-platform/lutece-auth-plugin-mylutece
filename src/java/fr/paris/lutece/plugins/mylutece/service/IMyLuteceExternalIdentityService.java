package fr.paris.lutece.plugins.mylutece.service;

import java.util.List;
import java.util.Map;

public interface IMyLuteceExternalIdentityService
{

    /**
     * Return identities informations on user
     * @param strUserName the user name
     * @return identities informations on user
     */
    Map<String,String>getIdentityInformations(String strUserName);
    
    /**
     * Return identities informations on user
     * @param strUserName the user name
     * @param attributeUserMappings the user Mapping
     * @return identities informations on user
     */
    Map<String,String>getIdentityInformations(String strUserName,Map<String, List<String>> attributeUserMappings);
    
}
