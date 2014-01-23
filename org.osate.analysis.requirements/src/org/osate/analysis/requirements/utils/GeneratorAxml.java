package org.osate.analysis.requirements.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.osate.aadl2.modelsupport.WriteToFile;
import org.osate.aadl2.util.OsateDebug;

import fr.openpeople.rdal.model.core.AbstractRequirement;

public class GeneratorAxml
{
	private static int NODE_ID;
	private static HashMap<AbstractRequirement,String> NODE_IDENTIFIER_MAP;
	
	public static void writeAxmlHeader (WriteToFile report)
	{
		report.addOutputNewline("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		report.addOutputNewline("<asce-network version=\"1.0\"><general>");
		report.addOutputNewline("<project-version><![CDATA[]]></project-version>");
		report.addOutputNewline("<project-name><![CDATA[]]></project-name>");
		report.addOutputNewline("<project-description><![CDATA[]]></project-description>");
		report.addOutputNewline("<project-author><![CDATA[]]></project-author>");
		report.addOutputNewline("<interpretation-schema><![CDATA[ASCE-GSN-CM 1.0]]></interpretation-schema>");
		report.addOutputNewline("<canvas-units>twips</canvas-units>");
		report.addOutputNewline("<asce-password></asce-password>");
		report.addOutputNewline("<tool-version>4.1.7</tool-version>");
		report.addOutputNewline("<font name=\"Tahoma\" size=\"9pt\"/>");
		report.addOutputNewline("<manual-attach>True</manual-attach>");
		report.addOutputNewline("</general>");
	}
	
	public static void writeNode (WriteToFile report, AbstractRequirement ar)
	{
		report.addOutputNewline("<node reference=\""+NODE_IDENTIFIER_MAP.get(ar)+"\">");
		report.addOutputNewline("<layout x=\"3630\" y=\"4905\" height=\"855\" width=\"1290\"/>");
		report.addOutputNewline("<type>12</type>");
		report.addOutputNewline("<user-id><![CDATA["+ar.getName()+"]]></user-id>");
		report.addOutputNewline("<user-title><![CDATA["+ar.getDescription()+"]]></user-title>");
		report.addOutputNewline("<status-fields>");
		report.addOutputNewline("	<status-field type=\"boolean\" name=\"hasexternalreference\"><![CDATA[False]]></status-field>");
		report.addOutputNewline("	<status-field type=\"boolean\" name=\"requiresdevelopment\"><![CDATA[False]]></status-field>");
		report.addOutputNewline("	<status-field type=\"boolean\" name=\"requiresinstantiation\"><![CDATA[False]]></status-field>");
		report.addOutputNewline("	<status-field type=\"string\" name=\"defeater\"><![CDATA[No]]></status-field>");
		report.addOutputNewline("	<status-field type=\"boolean\" name=\"completed\"><![CDATA[False]]></status-field>");
		report.addOutputNewline("	<status-field type=\"string\" name=\"resourced\"><![CDATA[]]></status-field>");
		report.addOutputNewline("	<status-field type=\"string\" name=\"comments\"><![CDATA[]]></status-field>");
		report.addOutputNewline("	<status-field type=\"long\" name=\"risk\"><![CDATA[1]]></status-field>");
		report.addOutputNewline("	<status-field type=\"string\" name=\"confidence\"><![CDATA[Off]]></status-field>");
		report.addOutputNewline("	<status-field type=\"string\" name=\"spectrum1\"><![CDATA[Off]]></status-field>");
		report.addOutputNewline("	<status-field type=\"string\" name=\"spectrum2\"><![CDATA[Off]]></status-field>");
		report.addOutputNewline("</status-fields>");
		report.addOutputNewline("<html-annotation><![CDATA[<p>&nbsp;</p>]]></html-annotation>");
		report.addOutputNewline("</node>");		
	}
	
	public static void generateKey (AbstractRequirement ar)
	{
		if (! NODE_IDENTIFIER_MAP.containsKey(ar))
		{
			NODE_IDENTIFIER_MAP.put(ar, "NODE" + NODE_ID);
			NODE_ID = NODE_ID + 1;
		}
	}
	
	public static void generateConfidenceMap (AbstractRequirement requirement)
	{
		WriteToFile report;
		List <AbstractRequirement> allSubRequirements;
		
		report = new WriteToFile("confidencemap", requirement);
		report.setFileExtension("axml");
		NODE_IDENTIFIER_MAP = new HashMap<AbstractRequirement,String> ();
		NODE_ID = 0;
		
		writeAxmlHeader(report);
		report.addOutputNewline("<nodes>");
		
		allSubRequirements = new ArrayList<AbstractRequirement>();
		
		Utils.gatherAllSubRequirements (requirement, allSubRequirements);
		
		generateKey(requirement);
		writeNode (report, requirement);
		for (AbstractRequirement ar : allSubRequirements)
		{
			generateKey(ar);
			writeNode (report, ar);
		}
		report.addOutputNewline("</nodes>");
		
		report.saveToFile();
	}
}
