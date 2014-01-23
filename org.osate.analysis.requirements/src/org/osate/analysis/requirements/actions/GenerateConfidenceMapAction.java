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
import org.osate.analysis.requirements.utils.Utils;
import org.osgi.framework.Bundle;

import fr.openpeople.rdal.model.core.AbstractRequirement;
import fr.openpeople.rdal.model.core.RequirementsGroup;
import fr.openpeople.rdal.model.core.Specification;



class RequirementSelectionDialog extends Dialog {

	  private Combo requirementCombo;
	  private String selectedRequirement;
	  private List<String> requirementsList;


	  public RequirementSelectionDialog(Shell parent, List<String> rl) {
		    this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		    this.requirementsList = rl;
		    
		  }


		  public RequirementSelectionDialog(Shell parent, int style) {
		    super(parent, style);
		    setText("Select the requirements");
		  //  setMessage("Please select the requirement for generating the confidence map");
		    this.selectedRequirement = null;
		  }

		  public String open()
		  {
		    Shell shell = new Shell(getParent(), getStyle());
		    shell.setText(getText());
		    createContents(shell);
		    shell.pack();
		    shell.open();
		    Display display = getParent().getDisplay();
		    while (!shell.isDisposed()) 
		    {
		      if (!display.readAndDispatch()) 
		      {
		        display.sleep();
		      }
		    }
		    return this.selectedRequirement;
		  }
	  
		  private void createContents(final Shell shell) {

	    Label label1 = new Label(shell, SWT.NONE);
	    label1.setText("Root requirement");

	    requirementCombo = new Combo(shell, SWT.BORDER | SWT.READ_ONLY);
	    String reqs[] = new String[requirementsList.size()];
	    for (int i = 0 ; i < requirementsList.size() ; i++)
	    {
	    	reqs[i] = requirementsList.get(i);	
	    }
	    
	   requirementCombo.setItems(reqs);
	   requirementCombo.setText(reqs[0]);
	   
	   shell.setLayout(new GridLayout(2, true));

	    // Show the message
	    
	    GridData data = new GridData();
	    data.horizontalSpan = 2;
	    label1.setLayoutData(data);

 
	    
	    data = new GridData(GridData.FILL_HORIZONTAL);
	    data.horizontalSpan = 2;
	    requirementCombo.setLayoutData(data);

	/**
	 * The OK button that register the selected
	 * items.
	 */
	    Button ok = new Button(shell, SWT.PUSH);
	    ok.setText("OK");
	    data = new GridData(GridData.FILL_HORIZONTAL);
	    ok.setLayoutData(data);
	    ok.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	    	  
	    			  selectedRequirement = requirementCombo.getText();

	        shell.close();
	      }
	    });

	    // Create the cancel button and add a handler
	    // so that pressing it will set input to null
	    Button cancel = new Button(shell, SWT.PUSH);
	    cancel.setText("Cancel");
	    data = new GridData(GridData.FILL_HORIZONTAL);
	    cancel.setLayoutData(data);
	    cancel.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        selectedRequirement = null;
	        shell.close();
	      }
	    });

	    // Set the OK button as the default, so
	    // user can type input and press Enter
	    // to dismiss
	    shell.setDefaultButton(ok);
	  }

	




	  protected boolean isResizable() {
	    return true;
	  }


	  
	  public String getSelectedRequirementName ()
	  {
		  return this.selectedRequirement;
	  }

	} 


public final class GenerateConfidenceMapAction implements IWorkbenchWindowActionDelegate  {
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
			OsateDebug.osateDebug("null specs");
			return;
		}
		
		for (RequirementsGroup rg : specs.getRequirementGroups())
		{
			for (AbstractRequirement req : rg.getRequirements())
			{
				requirementsNameList.add(req.getName());
				OsateDebug.osateDebug("REQ NAME - " + req.getName());
			}
		}
		
		for (String s : requirementsNameList)
		{
			OsateDebug.osateDebug("REQ NAME - " + s);
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
		
		if (requirement != null)
		{
			OsateDebug.osateDebug(requirement.getId());
			OsateDebug.osateDebug("selected requirement" + selectedRequirement);
		}
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
