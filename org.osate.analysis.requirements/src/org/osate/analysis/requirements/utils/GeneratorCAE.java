package org.osate.analysis.requirements.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.certware.argument.arm.ArgumentElement;
import net.certware.argument.caz.Argument;
import net.certware.argument.caz.ArgumentDiagram;
import net.certware.argument.caz.CazFactory;
import net.certware.argument.caz.Claim;
import net.certware.argument.caz.Evidence;
import net.certware.argument.caz.diagram.part.CazCreationWizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osate.aadl2.modelsupport.WriteToFile;
import org.osate.aadl2.util.OsateDebug;

import fr.openpeople.rdal.model.core.AbstractRequirement;
import fr.openpeople.rdal.model.core.ContainerType;
import fr.openpeople.rdal.model.core.IdentifiedElement;
import fr.openpeople.rdal.model.core.Requirement;
import fr.openpeople.rdal.model.core.RequirementsContainer;
import fr.openpeople.rdal.model.core.VerificationActivity;
import fr.openpeople.rdal.model.core.VerificationActivityContainer;
import fr.openpeople.rdal.model.core.impl.RequirementImpl;

public class GeneratorCAE
{
	private static final CazFactory f = CazFactory.eINSTANCE;

	private static ArgumentDiagram argumentDiagram = f.createArgumentDiagram();
	private static int id = 1;
	private final static int SUBSTR_LEN = 15;

	public static void init ()
	{
		argumentDiagram = f.createArgumentDiagram();
		id = 1;
	}
	
	public static void generate (ArgumentElement parent, IdentifiedElement entity)
	{
		String content = "";
		if (entity == null)
		{
			return;	
		}
		
		if (entity instanceof Requirement) {
			Requirement ar = (Requirement) entity;
			Claim claim = f.createClaim();

			content = ar.getName();
			if (ar.getDescription() != null)
			{
				content += " - " + ar.getDescription().substring(0, SUBSTR_LEN) + " ...";
			}
			
			claim.setIdentifier(Integer.toString(id++));
			claim.setContent(content);
			claim.setDescription(ar.getDescription());
			
			argumentDiagram.getClaims().add(claim);
			if (parent != null) 
			{
				if (parent instanceof Argument)
				{
					Argument a = (Argument) parent;
					a.getArgumentClaims().add(claim);
				}
			}

			parent = claim;
			generate (parent, ar.getSubRequirements());
			
			for (VerificationActivity vatmp : ar.getVerifiedBy())
			{
				generate (parent, vatmp);
			}
			
			if ((ar.getSubVerificationActivities() != null) && (ar.getSubVerificationActivities().getVerificationActivites().size() > 0))
			{
				generate (parent, ar.getSubVerificationActivities());
			}
		}

		if (entity instanceof RequirementsContainer)
		{
			RequirementsContainer rc = (RequirementsContainer) entity;
			Argument arg = f.createArgument();
			
			
			if(rc.getType() == ContainerType.AND)
			{
				content = "AND";
			}
			
			if(rc.getType() == ContainerType.OR)
			{
				content = "OR";
			}
			
			if (rc.getDescription() != null)
			{
				content += " - " + rc.getDescription().substring(0, SUBSTR_LEN) + " ...";
			}
			
			arg.setContent(content);
			
			arg.setDescription(rc.getDescription());
			argumentDiagram.getArguments().add(arg);

			if (parent != null) 
			{
				if (parent instanceof Claim)
				{
					Claim c = (Claim) parent;
					c.getClaimStrategies().add (arg);
				}
			}
			
			parent = arg;
			
			for (Requirement rtmp : rc.getRequirements())
			{
				generate(parent, rtmp);
			}
		}
		
		if (entity instanceof VerificationActivityContainer)
		{
			VerificationActivityContainer vac = (VerificationActivityContainer) entity;
			Argument arg = f.createArgument();
			
			
			if(vac.getType() == ContainerType.AND)
			{
				content = "AND";
			}
			
			if(vac.getType() == ContainerType.OR)
			{
				content = "OR";
			}
			
			if (vac.getDescription() != null)
			{
				content += " - " + vac.getDescription().substring(0, SUBSTR_LEN) + " ...";
			}
			
			arg.setContent(content);
			
			arg.setDescription(vac.getDescription());
			argumentDiagram.getArguments().add(arg);

			if (parent != null) 
			{
				if (parent instanceof Claim)
				{
					Claim c = (Claim) parent;
					c.getClaimStrategies().add (arg);
				}
			}
			
			parent = arg;
			
			for (VerificationActivity rtmp : vac.getVerificationActivites())
			{
				generate(parent, rtmp);
			}
		}
		
		

		if (entity instanceof VerificationActivity)
		{
			VerificationActivity va = (VerificationActivity) entity;
			Evidence ev = f.createEvidence();
			
			content = va.getName();
			if (va.getDescription() != null)
			{
				content += " - " + va.getDescription().substring(0, SUBSTR_LEN) + " ...";
			}
			
			ev.setContent(content);
			ev.setDescription(va.getDescription());
			argumentDiagram.getEvidence().add(ev);

			if (parent != null) 
			{
				if (parent instanceof Claim)
				{
					Claim c = (Claim) parent;
					c.getClaimSolutions().add (ev);
				}
				
				if (parent instanceof Argument)
				{
					Argument a = (Argument) parent;
					a.getArgumentEvidence().add (ev);
				}
			}

		}
	}


public static void save(IProject project) {
	CazCreationWizard wizard = new CazCreationWizard();
	IStructuredSelection selection = new StructuredSelection(project);
	wizard.init(PlatformUI.getWorkbench(), selection);
	Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	WizardDialog dialog = new WizardDialog(shell, wizard);

	if (dialog.open() == WizardDialog.OK) {
		Diagram diagram = (Diagram) wizard.getDiagram().getContents().get(0);
		final Resource modelResource = diagram.getElement().eResource();

		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(modelResource);
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			@Override 
			protected void doExecute() {
				modelResource.getContents().clear();
				modelResource.getContents().add(argumentDiagram);
				try {
					modelResource.save(null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
}
