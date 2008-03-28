package net.bioclipse.brunn.ui.dialogs.importOrcaResults;

import net.bioclipse.brunn.ui.explorer.model.ITreeObject;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.PlatformUI;

public class ImportOrcaResults extends Wizard {

	private WizardPage1 page1;
	private WizardPage2 page2;
	private ITreeObject dataSets;
	
	public ImportOrcaResults(ITreeObject dataSets) {
		this.dataSets = dataSets;
	}

	public void addPages()  {
		
		page1 = new WizardPage1();
		addPage(page1);		
		page2 = new WizardPage2();
		addPage(page2);
	}
	
	@Override
	public boolean performFinish() {

		page2.addResultsToCheckedPlates();
		dataSets.fireUpdate();
		return true;
	}
	
	public String getPath() {
		return ((WizardPage1) page1).getPath();
	}
}