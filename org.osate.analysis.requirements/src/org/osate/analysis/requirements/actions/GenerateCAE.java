/*
 * Copyright 2013 Carnegie Mellon University
 * 
 * The AADL/DSM Bridge (org.osate.importer.lattix ) (the �Content� or �Material�) 
 * is based upon work funded and supported by the Department of Defense under 
 * Contract No. FA8721-05-C-0003 with Carnegie Mellon University for the operation 
 * of the Software Engineering Institute, a federally funded research and development 
 * center.

 * Any opinions, findings and conclusions or recommendations expressed in this 
 * Material are those of the author(s) and do not necessarily reflect the 
 * views of the United States Department of Defense. 

 * NO WARRANTY. THIS CARNEGIE MELLON UNIVERSITY AND SOFTWARE ENGINEERING 
 * INSTITUTE MATERIAL IS FURNISHED ON AN �AS-IS� BASIS. CARNEGIE MELLON 
 * UNIVERSITY MAKES NO WARRANTIES OF ANY KIND, EITHER EXPRESSED OR IMPLIED, 
 * AS TO ANY MATTER INCLUDING, BUT NOT LIMITED TO, WARRANTY OF FITNESS FOR 
 * PURPOSE OR MERCHANTABILITY, EXCLUSIVITY, OR RESULTS OBTAINED FROM USE OF 
 * THE MATERIAL. CARNEGIE MELLON UNIVERSITY DOES NOT MAKE ANY WARRANTY OF 
 * ANY KIND WITH RESPECT TO FREEDOM FROM PATENT, TRADEMARK, OR COPYRIGHT 
 * INFRINGEMENT.
 * 
 * This Material has been approved for public release and unlimited 
 * distribution except as restricted below. 
 * 
 * This Material is provided to you under the terms and conditions of the 
 * Eclipse Public License Version 1.0 ("EPL"). A copy of the EPL is 
 * provided with this Content and is also available at 
 * http://www.eclipse.org/legal/epl-v10.html.
 * 
 * Carnegie Mellon� is registered in the U.S. Patent and Trademark 
 * Office by Carnegie Mellon University. 
 * 
 * DM-0000232
 * 
 */

package org.osate.analysis.requirements.actions;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.osate.aadl2.util.OsateDebug;
import org.osate.analysis.requirements.Activator;
import org.osate.analysis.requirements.utils.GeneratorAxml;
import org.osate.analysis.requirements.utils.GeneratorCAE;
import org.osate.analysis.requirements.utils.RequirementSelectionDialog;
import org.osate.analysis.requirements.utils.Utils;
import org.osgi.framework.Bundle;


import fr.openpeople.rdal.model.core.AbstractRequirement;
import fr.openpeople.rdal.model.core.RequirementsGroup;
import fr.openpeople.rdal.model.core.Specification;



public final class GenerateCAE implements IWorkbenchWindowActionDelegate  {
	String selectedRequirement;
	List<String> selectedModules;
	
	protected Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

	protected String getMarkerType() {
		return "";
	}

	protected String getActionName() {
		return "Confidence Map Generator";
	}
	

	
	public void run(IAction action) 
	{
		Specification 		specs;
		AbstractRequirement requirement;
		final List<String> requirementsNameList;
		
		requirement = null;
		
		requirementsNameList = new ArrayList<String>();
		specs = Utils.getSelectedRDALModel();
		if (specs == null)
		{
			OsateDebug.osateDebug("[GenerateCAE] null specs");
			return;
		}
		
		for (RequirementsGroup rg : specs.getRequirementGroups())
		{
			for (AbstractRequirement req : rg.getRequirements())
			{
				requirementsNameList.add(req.getName());
			}
		}
		
	
		
		final Display d = PlatformUI.getWorkbench().getDisplay();
		d.syncExec(new Runnable(){

			public void run() {
				IWorkbenchWindow window;
				Shell sh;
				RequirementSelectionDialog csd;
				window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				sh = window.getShell();
		 		csd = new RequirementSelectionDialog(sh, requirementsNameList);
				selectedRequirement = csd.open();
			}
		});
		
		for (RequirementsGroup rg : specs.getRequirementGroups())
		{
			for (AbstractRequirement req : rg.getRequirements())
			{
				if ( (selectedRequirement != null) && (req.getName().equalsIgnoreCase(selectedRequirement)))
				{
					requirement = req;
				}
			}
		}
		
		if (requirement == null)
		{
			//FIXME should show a dialogbox for error
			OsateDebug.osateDebug("[GenerateCAE] ERROR");
			return;
		}
		GeneratorCAE.init();
		GeneratorCAE.generate (null, requirement);
		GeneratorCAE.save(Utils.getSelectedProject());
		
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}


	public void dispose() 
	{
		
	}

	public void init(IWorkbenchWindow window)
	{

	}
}
