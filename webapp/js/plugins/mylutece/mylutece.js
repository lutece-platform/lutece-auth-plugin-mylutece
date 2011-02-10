function getAuthMenuAction() 
{
	var menuDiv = document.getElementById('header-menu');
	if(menuDiv.style.display == "block")
	{
		menuDiv.style.display = "none";
	}
	else
	{
		menuDiv.style.display = "block";
	}
}