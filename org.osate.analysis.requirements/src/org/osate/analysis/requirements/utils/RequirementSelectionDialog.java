package org.osate.analysis.requirements.utils;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class RequirementSelectionDialog extends Dialog {

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


