<html>
<head>
<style>
h1 {
	text-align: center;
	text-shadow: 3px 2px grey;
}

form {
	text-align: center;
	margin-top: 50px;
	margin-right: 200px;
	margin-left: 200px;
}

inputXML{
input[type ="text"]{
text-align:top;
}

</style>

</head>

<body>
<img src="/XMLReader/images/logo.png" height="75" width="150"> 
<br />
	<div id='headingName'>
		<h1>eRx Raw XML Reader</h1>
	</div>
	<form action="/XMLReader/ParseXMLData" id="pageForm" method="doPost">

		<div id="inputXML">
			<h4>Paste XML here:</h4>
			<textarea id="rawXML" name="rawXML" placeholder="XML Text here"
				style="height: 350px; width: 350px;"  ></textarea>
		</div>
		<br />
		<div id="buttons">
			<input type="submit" id="submit" name="submit" value="Generate Data">
		</div>
	</form>

</body>
</html>