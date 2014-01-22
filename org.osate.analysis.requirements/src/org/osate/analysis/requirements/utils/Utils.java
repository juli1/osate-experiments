/*
 * Copyright 2013 Carnegie Mellon University

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
 */

package org.osate.analysis.requirements.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.PlatformUI;
import org.osate.aadl2.Element;


public class Utils 
{

	
	public static void refreshWorkspace (IProgressMonitor monitor)
	{
		for(IProject ip : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			try {
				ip.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
	

	public static Resource getResource(IResource ires) {
		IPath path = ires.getFullPath();
		return null;
	}

	public static Element getSelectedRDALModel ()
	{
		String selectedFolder;
		ISelection selection;
		IFolder selectedIFolder;;
		
		selectedFolder = null;
		
		 selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
	        if (selection != null) 
	        {
	        	try
	        	{
	            if (selection instanceof IStructuredSelection) 
	            {
	                if (selection instanceof ITreeSelection) 
	                {
	                    TreeSelection treeSelection = (TreeSelection) selection;
	                    TreePath[] treePaths = treeSelection.getPaths();
	                    TreePath treePath = treePaths[0];

	                    Object lastSegmentObj = treePath.getLastSegment();

	            		if (lastSegmentObj instanceof IFile
	            				&& (((IFile) lastSegmentObj).getFileExtension()).equalsIgnoreCase("rdal")) {
	            			Resource res = getResource((IResource) lastSegmentObj);
	            			EList<EObject> rl = res.getContents();
	            			if (!rl.isEmpty() && rl.get(0) instanceof Element)
	            				return (Element) rl.get(0);
	            		}
	                    if(lastSegmentObj instanceof IFolder) 
	                    {
	                    	
	                    	selectedIFolder = (IFolder) ((IAdaptable) lastSegmentObj).getAdapter(IFolder.class);
	                        if(selectedIFolder != null) 
	                        {
	                        	selectedFolder = selectedIFolder.getRawLocation().toOSString();
	                        }
	                        
	                    }
	                }
	            }
	        	}
	        	catch (NullPointerException npe)
	        	{
	        		selectedFolder = null;
	        	}
	        }
	        return null;
	}
}
