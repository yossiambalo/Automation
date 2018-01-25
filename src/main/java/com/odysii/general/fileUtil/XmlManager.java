package com.odysii.general.fileUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XmlManager {

    /**
     * Method edit a specific node
     * @param file: xml file
     * @param nodeWrapper: the parent node name of node to edit
     * @param nodeToEdit: node to edit
     * @rturn: void
     */
    public static void updateNodeContent(File file, String nodeWrapper, String nodeToEdit, String newValue){

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            Node wrapper = doc.getElementsByTagName(nodeWrapper).item(0);
            NodeList list = wrapper.getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {

                Node node = list.item(i);
                if (nodeToEdit.equals(node.getNodeName())) {
                    node.setTextContent(newValue);
                }

            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        }
    }
    public static String getValueOfNode(File file, String nodeWrapper, String nodeToEdit){

        String res = "";
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            Node wrapper = doc.getElementsByTagName(nodeWrapper).item(0);
            NodeList list = wrapper.getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {

                Node node = list.item(i);
                if (nodeToEdit.equals(node.getNodeName())) {
                    res = node.getTextContent();
                }

            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        }
        return res;
    }
    public static boolean deleteNode(File file,String tagToDelete){
        boolean res = false;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;
        dbf.setValidating(false);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(file);
            // retrieve the element 'link'
            Element element = (Element) doc.getElementsByTagName(tagToDelete).item(0);
            // remove the specific node
            element.getParentNode().removeChild(element);
            // Normalize the DOM tree, puts all text nodes in the
            // full depth of the sub-tree underneath this node
            doc.normalize();
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
            res = true;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return res;
    }
    public static boolean isNodeExist(File file,String parentNode, String childNode){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;
        boolean found = false;
        dbf.setValidating(false);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(file);
            // retrieve the element 'link'
            NodeList nodeList = doc.getElementsByTagName(parentNode).item(0).getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++){
                if (nodeList.item(i).getNodeName().contains(childNode)){
                    found = true;
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return found;
    }
    public static boolean replaceNodeAttribute(File file, String tagName,String attribute,String oldValue, String newValue){
        boolean flag = false;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;
        dbf.setValidating(false);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(file);
            // retrieve the element 'link'
            doc.getElementsByTagName(tagName).item(0).getAttributes().getNamedItem(attribute).setTextContent(newValue);
            // Normalize the DOM tree, puts all text nodes in the
            // full depth of the sub-tree underneath this node
            doc.normalize();
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
            flag = true;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return flag;
    }
}
