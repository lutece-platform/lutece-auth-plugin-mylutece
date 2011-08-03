<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


<xsl:template match="portlet">
	<div class="portlet -lutece-border-radius append-bottom">
		<xsl:choose>
	        <xsl:when test="not(string(display-portlet-title)='1')">
				<h3 class="portlet-header">
					<xsl:value-of disable-output-escaping="yes" select="portlet-name" />
				</h3>
				<div class="portlet-content">
			    	<xsl:apply-templates select="mylutece-portlet" />
				</div>
	        </xsl:when>
	        <xsl:otherwise>
				<div class="portlet-content">
			    	<xsl:apply-templates select="mylutece-portlet" />
				</div>
	        </xsl:otherwise>
		</xsl:choose>
	</div>
</xsl:template>


<xsl:template match="mylutece-portlet">
	<xsl:apply-templates select="user-not-signed" />
	<xsl:apply-templates select="lutece-user" />
</xsl:template>


<xsl:template match="user-not-signed">
	<form action="jsp/site/plugins/mylutece/DoMyLuteceLogin.jsp" method="post">
		
		<xsl:apply-templates select="lutece-user-authentication-service[@delegated='true']" />
		<xsl:apply-templates select="lutece-user-authentication-service[@loginpassword-required='true']" />
		<xsl:if test="count(lutece-user-authentication-service[@loginpassword-required='true']) &gt;= 1">
		<center>
			<p>
				<label for="username">Code d'accès :</label>
				<input name="username" class="-lutece-input -lutece-border-radius-mini" id="username" autocomplete="off" tabindex="1" type="text"/><br />
				<label for="password">Mot de passe :</label>
				<input name="password" class="-lutece-input -lutece-border-radius-mini" id="password" autocomplete="off" tabindex="2" type="password" />
			</p>
			<p>
				<input class="-lutece-input -lutece-border-radius-mini"  value="Connexion" tabindex="3" type="submit" />
			</p>
		</center>
		</xsl:if>
	</form>
	<xsl:apply-templates select="lutece-user-new-account-url" />
	<xsl:apply-templates select="lutece-user-lost-password-url" />
</xsl:template>

<xsl:template match="lutece-user-authentication-service[@loginpassword-required='true']">
	<center>
		<input type="radio" name="auth_provider" value="{name}" />
		<img src="{icon-url}" alt="{display-name}" title="{display-name}"/>&#160;
		<xsl:value-of select="display-name" />
	</center>
</xsl:template>

<xsl:template match="lutece-user-authentication-service[@delegated='true']">
	<center>
		<a href="{url}?auth_provider={name}">
			<img src="{icon-url}" alt="{display-name}" title="{display-name}"/>
		</a>
	</center>
</xsl:template>


<xsl:template match="lutece-user">
    Bienvenue   <xsl:value-of disable-output-escaping="yes" select="lutece-user-name-given" />&#160;
                <xsl:value-of disable-output-escaping="yes" select="lutece-user-name-family" />
    <br />
    <br />
    <xsl:apply-templates select="lutece-user-logout-url" />
    <xsl:apply-templates select="lutece-user-view-account-url" />
</xsl:template>


<xsl:template match="lutece-user-logout-url">
   <form name="logout" action="{.}" method="post">
   	<center>
	   	<input type="submit" value="D&#233;connexion" class="-lutece-input -lutece-border-radius-mini" />
    </center>
   </form>
</xsl:template>


<xsl:template match="lutece-user-new-account-url">
	<div id="lutece-user-new-account" >
	<form name="logout" action="{.}" method="post">
    	<center>
		   	<input type="submit" value="Cr&#233;er un compte" class="-lutece-input -lutece-border-radius-mini" />
	    </center>
    </form>
    </div>
</xsl:template>


<xsl:template match="lutece-user-lost-password-url">
	<div id="lutece-user-lost-password" >
		<form name="logout" action="{.}" method="post">
	    	<center>
			   	<input type="submit" value="Mot de passe perdu" class="-lutece-input -lutece-border-radius-mini" />
		    </center>
	   </form>
   </div>
</xsl:template>

<xsl:template match="lutece-user-view-account-url">
	<form name="logout" action="{.}" method="post">
    	<center>
		   	<input type="submit" value="Voir mon compte" class="-lutece-input -lutece-border-radius-mini" />
	    </center>
    </form>
</xsl:template>

</xsl:stylesheet>

