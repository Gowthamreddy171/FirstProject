package test;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {

	public static void main(String[] args) {

		try {
			File XMLfile = new File("C://AJAY_XMLS//AVLT_indices_1_testing.XML");
			String tableName = XMLfile.getName().replace(".XML", "");
			System.out.println(tableName);
			ArrayList<String> attrList = new ArrayList<>();
			DocumentBuilderFactory docBldrFac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBldr = docBldrFac.newDocumentBuilder();
			Document doc = docBldr.parse(XMLfile);
			doc.getDocumentElement().normalize();
//			System.out.println("Root Element: " + doc.getDocumentElement().getNodeName());
			NodeList fieldList = doc.getElementsByTagName("FIELD");
			
//			Creating an array list of fields and their data types separated by "-" 
			for (int temp = 0; temp < fieldList.getLength(); temp++) {
				Node fieldNode = fieldList.item(temp);
				if (fieldNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) fieldNode;
					String FldType;
					if(eElement.getAttribute("fieldtype").equals("i1")||eElement.getAttribute("fieldtype").equals("i2")||eElement.getAttribute("fieldtype").equals("i4")||eElement.getAttribute("fieldtype").equals("r4")||eElement.getAttribute("fieldtype").equals("r8")){
						FldType = "int";
					}
					else{
						FldType = "varchar(60)";
					}
					attrList.add(eElement.getAttribute("attrname")+"-"+FldType);
				}
			}

//			Creating attribute list for future use
			ArrayList<String> attributeList = new ArrayList<>();
			for(int l=0; l < attrList.size(); l++){
				String[] attributes = attrList.get(l).split("-");
				attrList.set(l, attributes[0] +" "+ attributes[1]);
				attributeList.add(attributes[0]);
			}
			
//			SQL query for Create table 
			String createTblQuery = "CREATE TABLE "+tableName+" (";
			for(int m =0; m  < attrList.size(); m++){
				createTblQuery = createTblQuery + attrList.get(m);
				if(m!=attrList.size()-1){
					createTblQuery = createTblQuery + ", ";
				}
			}
			createTblQuery = createTblQuery + ") ";
			System.out.println(createTblQuery);
			
//			Creating an array list of data for the table along with the Insert command in SQL
			ArrayList<String> dataList = new ArrayList<>(); 
			NodeList rowList = doc.getElementsByTagName("ROW");
			for (int i=0; i<rowList.getLength(); i++){
				Node rowNodes = rowList.item(i);
				if(rowNodes.getNodeType() == Node.ELEMENT_NODE){
					Element rowElement = (Element) rowNodes;
					for(int j=0; j<fieldList.getLength();j++){
						dataList.add(rowElement.getAttribute(attributeList.get(j)));
					}
					
//					Validation of number of columns vs the data set in each row
					if(dataList.size()!=attributeList.size()){
						System.out.println("Column and data set rows does not match");
					}
					
//					SQL command for Inserting data in to the table
					String insertingDataQuery = "insert into "+tableName+" values "+"(";
					for (int k=0; k < dataList.size(); k++) {
						String data = dataList.get(k);
						String[] dataType = attrList.get(k).split(" ");
						if(dataType[1].equals("varchar(60)")){
							insertingDataQuery = insertingDataQuery + "'" + data + "'";
						}
						else{
							insertingDataQuery = insertingDataQuery + "" + data + "";
						}
						if(k!=dataList.size()-1){
							insertingDataQuery = insertingDataQuery + ",";
						}
					}
					insertingDataQuery = insertingDataQuery +")";
					dataList.clear();
					System.out.println(insertingDataQuery);
				}
			}
			
		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
