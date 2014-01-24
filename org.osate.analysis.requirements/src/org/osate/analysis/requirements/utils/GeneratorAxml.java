package org.osate.analysis.requirements.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.osate.aadl2.modelsupport.WriteToFile;
import org.osate.aadl2.util.OsateDebug;

import fr.openpeople.rdal.model.core.AbstractRequirement;
import fr.openpeople.rdal.model.core.impl.RequirementImpl;

public class GeneratorAxml
{
	private static int NODE_ID;
	private static int LINK_ID;
	private static HashMap<AbstractRequirement,String> NODE_IDENTIFIER_MAP;
	public static final int BOX_WIDTH = 5000;
	public static final int BOX_HEIGHT = 2000;
	public static final int YMARGIN = 500;
	public static final int XMARGIN = 500;
	private static int NB_ELEMENTS[] = new int[100];
	private static int CURRENT_ROW = 0;
	
	
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
		report.addOutputNewline("<font name=\"Tahoma\" size=\"8pt\"/>");
		report.addOutputNewline("<manual-attach>True</manual-attach>");
		report.addOutputNewline("</general>");
	}
	
	public static void writeNode (WriteToFile report, AbstractRequirement ar)
	{
		generateKey(ar);
		int xpos;
		int ypos;
		
		ypos = YMARGIN + (YMARGIN + BOX_HEIGHT) * CURRENT_ROW;
		
		xpos = XMARGIN + (XMARGIN + BOX_WIDTH) * NB_ELEMENTS[CURRENT_ROW];
		report.addOutputNewline("<node reference=\""+NODE_IDENTIFIER_MAP.get(ar)+"\">");
		report.addOutputNewline("   <layout x=\""+ xpos +"\" y=\""+ypos+"\" height=\""+BOX_HEIGHT+"\" width=\""+BOX_WIDTH+"\"/>");
		report.addOutputNewline("   <type>12</type>");
		report.addOutputNewline("   <user-id><![CDATA["+ar.getName()+"]]></user-id>");
		report.addOutputNewline("   <user-title><![CDATA["+ar.getDescription()+"]]></user-title>");
		report.addOutputNewline("   <status-fields>");
		report.addOutputNewline("	   <status-field type=\"boolean\" name=\"hasexternalreference\"><![CDATA[False]]></status-field>");
		report.addOutputNewline("	   <status-field type=\"boolean\" name=\"requiresdevelopment\"><![CDATA[False]]></status-field>");
		report.addOutputNewline("	   <status-field type=\"boolean\" name=\"requiresinstantiation\"><![CDATA[False]]></status-field>");
		report.addOutputNewline("	   <status-field type=\"string\" name=\"defeater\"><![CDATA[No]]></status-field>");
		report.addOutputNewline("	   <status-field type=\"boolean\" name=\"completed\"><![CDATA[False]]></status-field>");
		report.addOutputNewline("	   <status-field type=\"string\" name=\"resourced\"><![CDATA[]]></status-field>");
		report.addOutputNewline("	   <status-field type=\"string\" name=\"comments\"><![CDATA[]]></status-field>");
		report.addOutputNewline("	   <status-field type=\"long\" name=\"risk\"><![CDATA[1]]></status-field>");
		report.addOutputNewline("	   <status-field type=\"string\" name=\"confidence\"><![CDATA[Off]]></status-field>");
		report.addOutputNewline("	   <status-field type=\"string\" name=\"spectrum1\"><![CDATA[Off]]></status-field>");
		report.addOutputNewline("	   <status-field type=\"string\" name=\"spectrum2\"><![CDATA[Off]]></status-field>");
		report.addOutputNewline("   </status-fields>");
		report.addOutputNewline("   <html-annotation><![CDATA[<p>&nbsp;</p>]]></html-annotation>");
		report.addOutputNewline("</node>");
		
		NB_ELEMENTS[CURRENT_ROW] = NB_ELEMENTS[CURRENT_ROW] + 1;
	
		CURRENT_ROW = CURRENT_ROW + 1;
		for (AbstractRequirement sar : ar.getContainedRequirements())
		{	
			writeNode (report, sar);
		}
		
		if (ar instanceof RequirementImpl)
		{
			RequirementImpl reqImpl = (RequirementImpl) ar;
			for (EObject eo : reqImpl.getRefinedBy())
			{	
				writeNode (report, (AbstractRequirement)eo);
			}
		}
		CURRENT_ROW = CURRENT_ROW - 1;

	}
	
	public static void writeLink (WriteToFile report, AbstractRequirement source, AbstractRequirement destination)
	{
		report.addOutputNewline("<link reference=\"LN"+ LINK_ID +"\">");
		report.addOutputNewline("   <type>6</type>");
		report.addOutputNewline("   <strength>1</strength>");
		report.addOutputNewline("   <source-reference>"+ NODE_IDENTIFIER_MAP.get(destination) +"</source-reference>");
		report.addOutputNewline("   <destination-reference>"+ NODE_IDENTIFIER_MAP.get(source) +"</destination-reference>");
		report.addOutputNewline("   <attachment x-source=\"0.5\" y-source=\"0\" x-destination=\"0.5\" y-destination=\"1\"/>");
		report.addOutputNewline("</link>");
		LINK_ID = LINK_ID + 1;
	}
	
	public static void generateKey (AbstractRequirement ar)
	{
		if (! NODE_IDENTIFIER_MAP.containsKey(ar))
		{
			NODE_IDENTIFIER_MAP.put(ar, "NODE" + NODE_ID);
			NODE_ID = NODE_ID + 1;
		}
	}
	
	public static void writeLinks (WriteToFile report, AbstractRequirement req)
	{
		for (AbstractRequirement subreq : req.getContainedRequirements())
		{
			writeLink (report, req, subreq);
			writeLinks (report, subreq);
		}
		
		if (req instanceof RequirementImpl)
		{
			RequirementImpl reqImpl = (RequirementImpl) req;
			for (EObject eo : reqImpl.getRefinedBy())
			{	
				writeLink (report, req, (AbstractRequirement)eo);
				writeLinks (report, (AbstractRequirement)eo);	
			}
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
		LINK_ID = 0;
		
		for (int i = 0 ; i < 100 ; i++)
		{
			NB_ELEMENTS[i] = 0;
			CURRENT_ROW = 0;
		}
		
		writeAxmlHeader(report);
		report.addOutputNewline("<nodes>");
		
		allSubRequirements = new ArrayList<AbstractRequirement>();
		
		Utils.gatherAllSubRequirements (requirement, allSubRequirements);
		
		generateKey(requirement);
		writeNode (report, requirement);

		report.addOutputNewline("</nodes>");
		
		
		report.addOutputNewline("<links>");
		
		writeLinks (report, requirement);
		report.addOutputNewline("</links>");
		report.addOutputNewline("<views/><embedded-images/><export-paths/></asce-network>");

		report.saveToFile();
	}
}
