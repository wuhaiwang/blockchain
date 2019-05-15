package com.seasun.management;

import com.seasun.management.helper.ExcelHelper;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;

public class XmlTest {


    @Test
    public void xmlToSqlFile() throws Exception {
        File inputFile = new File("C:/20180207Rtx员工编号.xml");
        File outputFile = new File("C:/a_sys_user.sql");
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
        String tableName = outputFile.getName().substring(0, outputFile.getName().lastIndexOf("."));
        //使用dom方式解析xml
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document parse = documentBuilder.parse(inputFile);
        //获取该名称节点集合
        NodeList nodeList = parse.getElementsByTagName("Item");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element item = (Element) nodeList.item(i);
            NamedNodeMap attributes = item.getAttributes();
            StringBuilder fieldsSb = new StringBuilder("insert into " + tableName + "(");
            StringBuilder valuesSb = new StringBuilder(" VALUES(");
            for (int j = 0; j < attributes.getLength(); j++) {
                Node item1 = attributes.item(j);
                fieldsSb.append(item1.getNodeName() + ",");
                valuesSb.append("'" + item1.getNodeValue() + "'" + ",");
            }

            String insert = "";
            insert = fieldsSb.substring(0, fieldsSb.length() - 1) + ") ";
            String substring = valuesSb.substring(0, valuesSb.length() - 1) + ");\r\n";
            insert = insert + substring;

            bw.write(insert);
        }
        bw.flush();
        bw.close();
    }

    @Test
    public void aa() throws Exception {
        Calendar calendar= Calendar.getInstance();
        calendar.set(2018,8,1);
        System.out.println("1time:" +calendar.get(Calendar.WEEK_OF_MONTH));
        System.out.println("1time:" +calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
        calendar.set(2018,8,3);
        System.out.println("2time:" +calendar.get(Calendar.WEEK_OF_MONTH));
        System.out.println("2time:" +calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
        calendar.set(2018,8,4);
        System.out.println("3time:" +calendar.get(Calendar.WEEK_OF_MONTH));
        System.out.println("3time:" +calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
        calendar.set(2018,8,10);
        System.out.println("4time:" +calendar.add(Calendar.WEEK_OF_MONTH));
        System.out.println("4time:" +calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));


    }
}
