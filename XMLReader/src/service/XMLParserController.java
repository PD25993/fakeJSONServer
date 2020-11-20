package service;

import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLParserController {
	public static LinkedHashMap<String,String> getXMLData( String xmlInString) 
	{
		LinkedHashMap<String,String> values= new LinkedHashMap<String,String>();
		ArrayList<String> stringList1 = new ArrayList<String>();
		ArrayList<String> stringListForAllergy = new ArrayList<String>();
		//String strs[] = new String[] {"Header", "BenefitsCoordination","Patient","Pharmacy","Prescriber","MedicationPrescribed","AllergyOrAdverseEvent"};
		//List<String> stringList = Arrays.asList(strs);
		String pat = "NewRx";
		ArrayList<String> stringListForNewRxChileNode = new ArrayList<String>();
		stringListForNewRxChileNode.add("Header");

		try{
			System.out.println("In Controller : ");

			Document xml = convertXMLStringToDocument(xmlInString);

			xml.getDocumentElement().normalize();//Normalize XML structure
			Element root = xml.getDocumentElement();
			String rootElement= root.getNodeName();

			NodeList childNodeListNewRx = xml.getElementsByTagName(pat);

			stringListForNewRxChileNode.addAll(getFirstChildForNewRx(childNodeListNewRx, stringList1,pat));

			System.out.println("stringListForNewRxChileNode : " + stringListForNewRxChileNode );

			NodeList childNodeList = xml.getElementsByTagName(rootElement);


			int taskCount=1;
			int renameCounter=1;
			values = VisitChildNodes(childNodeList,values,taskCount,stringListForNewRxChileNode,xml,renameCounter,stringListForAllergy);

		}
		catch(Exception e){
			e.printStackTrace();
		}
		return values;
	}

	private static ArrayList<String> getFirstChildForNewRx(NodeList childNodeListNewRx, ArrayList<String> stringList1, String pat)
	{
		//String firstChildNode;


		//System.out.println("In getFirstChildForNewRx  " +childNodeListNewRx.getLength());
		//Node tempNode = xml.getFirstChild();
		//NodeList nodeLs = tempNode.getChildNodes();

		for (Integer counter = 0; counter < childNodeListNewRx.getLength(); counter++)
		{
			//Element elm = (Element) childNodeListNewRx.item(counter);
			Node node = childNodeListNewRx.item(counter);
			if ( node.getNodeType() == Node.ELEMENT_NODE)
			{
				if(node.hasChildNodes())
				{
					if(node.getParentNode().getNodeName().equalsIgnoreCase(pat))
					{
						stringList1.add(node.getNodeName());
					}

					getFirstChildForNewRx(node.getChildNodes(), stringList1, pat);
				}
			}
		}

		//System.out.println("stringList1 : " + stringList1 );
		return stringList1;
	}

	private static LinkedHashMap<String,String> VisitChildNodes(NodeList childNodeList, LinkedHashMap<String,String> values,int taskCount, ArrayList<String> stringList,Document xml, int renameCounter, ArrayList<String> stringList1)
	{

		
		String firstChildNode;
		//ArrayList<String> stringList1 = new ArrayList<String>();
		ArrayList<String> stringList2 = new ArrayList<String>();

		for (Integer counter = 0; counter < childNodeList.getLength(); counter++)
		{
			Node node = childNodeList.item(counter);


			if ( node.getNodeType() == Node.ELEMENT_NODE)
			{


				String nodeName=node.getNodeName();
				if(stringList.contains(nodeName))
				{

					stringList1=new ArrayList<String>();
					//Integer locationForNode = stringList.indexOf(nodeName);
					String keyDtlForPrint="Tag "+ taskCount;		

					NodeList childNodeListNodeName = xml.getElementsByTagName(nodeName);
					/*if(nodeName.equals("AllergyOrAdverseEvent"))
					{*/
						getFirstChildForNewRx(childNodeListNodeName, stringList1,nodeName);
						
						/*stringList.remove(nodeName);
						stringList.addAll(locationForNode, stringList1);
						System.out.println("Updated Header : " + stringList);*/
					//}
					
						values.put(keyDtlForPrint, nodeName);
						taskCount++;
					
				}

				if(node.hasChildNodes())
				{

					String firstChildNodeDetail = node.getFirstChild().toString();

					if(firstChildNodeDetail.contains("null"))
					{
						firstChildNode = "";
					}
					else
					{

						firstChildNode = node.getFirstChild().getTextContent().trim();
					}

					String parentNodePattern= node.getParentNode().toString();
					String[] arrOfParentNode = parentNodePattern.split("[:]+");
					String parentNode1=arrOfParentNode[0];
					String parentNode=parentNode1.substring(1, parentNode1.length());

					//System.out.println(node.getNodeName() + " Parent Node : "+ parentNode + " " + node.getNextSibling());
					if((! firstChildNode.isEmpty()))
					{
						//String nodeNamePattern=parentNode.concat("-"+node.getNodeName()+renameCounter);
						
						String nodeNamePattern=node.getNodeName().concat(":"+parentNode+renameCounter);
						if(values.containsKey(node.getNodeName()))
						{
							values.put(nodeNamePattern,node.getTextContent());
							renameCounter++;
						}
						else
						{
							values.put(node.getNodeName(), node.getTextContent());
						}
					}
					else
					{
						
						if (stringList1.contains(node.getNodeName()))
						{
							//System.out.println("childNodeListNodeName : " + stringList1 + "----" +node.getNodeName());
							/*stringList2.add(node.getNodeName());
							System.out.println("stringList2 : " + stringList2);*/
							String newNodeName = node.getNodeName()+":"+renameCounter;
							values.put(newNodeName,"");
							renameCounter++;
							
						}
						/*stringList1.add(node.getNodeName());
						stringList1.add("Parent");
						stringList1.add(node.getParentNode().getNodeName());*/
					}
					VisitChildNodes(node.getChildNodes(), values, taskCount, stringList,xml,renameCounter,stringList1);
				}
			}

		}
		
		return values;
	}

	private static Document convertXMLStringToDocument(String xmlInString) 
	{

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = factory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(xmlInString)));
			return doc;
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		} catch (SAXException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return null;
	}
}
