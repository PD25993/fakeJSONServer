<%@ page import = "java.util.LinkedHashMap , java.util.Map.Entry" %>
<html>
<head>
</head>
<body>
<img src="/XMLReader/images/logo.png" height="75" width="150"> 
<style type="text/css">
	table.table-style-one {
		font-family: verdana,arial,sans-serif;
		font-size:11px;
		color:#333333;
		border-width: 1px;
		border-color: #3A3A3A;
		border-collapse: collapse;
	}
	table.table-style-one th {
		border-width: 1px;
		padding: 8px;
		border-style: solid;
		font-size:14px
		border-color: #3A3A3A;
		background-color: DARKGRAY;
	}
	table.table-style-one td {
		border-width: 1px;
		padding: 8px;
		border-style: solid;
		border-color: #3A3A3A;
	}
	table tr:nth-child(even){background-color: GAINSBORO;}
	
</style>


<br />

<center><h2>XML DATA</h2></center>

<form action="/XMLReader/Main" >
<p align="right">
<input type="submit"  value="Click Here to redirect to main page">
</p>

<table id="XMLData" align="center" class="table-style-one" >
<% 	

	LinkedHashMap<String,String> values = (LinkedHashMap<String,String>)request.getAttribute("name"); 
    for(Entry<String,String> pair : values.entrySet())
    {
    	String key = pair.getKey();
    	String value = pair.getValue();
    	
    	if(key!=null && value!=null)
    	{
    		if(key.contains("Tag"))
    		{
    			%><tr><th colspan=2 align="left"><%=value %></th></tr>
    			<%
    		}
    		else if (value == null || value.isEmpty()) 
    		{
    		%><tr><td colspan=2 align="left"><%=key %></td></tr>
    		<%
    		}
    		else
    		{
	    	%><tr><td><%=key %></td><td><%=value %></td></tr>
	    	<% 
	    	}
    	}
    }

%>
</table>
</form>

</body>
</html>