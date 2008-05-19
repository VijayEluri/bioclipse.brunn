package net.bioclipse.brunn.ui.dialogs.importOrcaResults;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bioclipse.brunn.Springcontact;
import net.bioclipse.brunn.business.operation.IOperationManager;
import net.bioclipse.brunn.business.plate.IPlateManager;
import net.bioclipse.brunn.pojos.Plate;
import net.bioclipse.brunn.results.orcaParser.OrcaParser;
import net.bioclipse.brunn.results.orcaParser.OrcaParser.OrcaPlateRead;
import net.bioclipse.brunn.ui.Activator;
import net.bioclipse.brunn.ui.dialogs.importOrcaResults.ImportOrcaResults;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

public class WizardPage2 extends WizardPage {

	private Table table;
	private String path = "";
	private OrcaParser parser;
	
	private CheckboxTableViewer tableViewer;
	private List<OrcaPlateRead> plateReads;
	private List<String> barcodesInDatabase;
	
	public static final String[] COLUMN_NAMES = {"import?", "Plate read", "Barcode", "Database status", "Parse status"} ;
	
	/**
	 * Create the wizard
	 */
	public WizardPage2() {
		super("wizardPage");
		setTitle("Select Plates To Import");
		setDescription("The results will be imported for the selected plates.");
	}

	/**
	 * Create contents of the wizard
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new FillLayout());
		//
		setControl(container);

		tableViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
		
		tableViewer.setContentProvider( new ContentProvider()    );
		tableViewer.setLabelProvider(   new TableLabelProvider() );
		tableViewer.setSorter(          new Sorter()             );
		
		table = tableViewer.getTable();
		
		TableColumn column1 = new TableColumn(table, SWT.CENTER, 0);
		column1.setText(COLUMN_NAMES[4]);
		column1.setWidth(100);
		
		TableColumn column2 = new TableColumn(table, SWT.LEFT, 0);
		column2.setText(COLUMN_NAMES[3]);
		column2.setWidth(150);

		TableColumn column3 = new TableColumn(table, SWT.LEFT, 0);
		column3.setText(COLUMN_NAMES[2]);
		column3.setWidth(100);
		
		TableColumn column4 = new TableColumn(table, SWT.LEFT, 0);
		column4.setText(COLUMN_NAMES[1]);
		column4.setWidth(150);
		
		TableColumn column5 = new TableColumn(table, SWT.LEFT, 0);
		column5.setText(COLUMN_NAMES[0]);
		column5.setWidth(80);
		
		tableViewer.setColumnProperties(COLUMN_NAMES);
		table.setHeaderVisible(true);
		
		CellEditor[] editors = new CellEditor[COLUMN_NAMES.length];
		editors[1] = new CheckboxCellEditor(table);
		editors[2] = new TextCellEditor(table);
		tableViewer.setCellEditors(editors);
		tableViewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return property.equals(COLUMN_NAMES[2]);
			}
			public Object getValue(Object element, String property) {
				
				if( COLUMN_NAMES[2].equals(property) )
					return ((OrcaPlateRead)element).getBarCode();
				return null;
			}
			public void modify(Object element, String property, Object value) {
				if( COLUMN_NAMES[2].equals(property) )
					( (OrcaPlateRead)( (TableItem)element ).getData() )
						.setBarCode((String)value);
				tableViewer.refresh();
			}
		});
		this.setPageComplete(false);
	}

	public void setVisible(boolean visible) {
		if( visible ) {
			this.setPageComplete(true);
			String newPath = ( (ImportOrcaResults)getWizard() ).getPath();
			if( !path.equals(newPath) ) {
				path = newPath;
				try {
					parser = new OrcaParser(new File(path));
				} catch (FileNotFoundException e) {
					MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							                 "File not found",  
							                 e.getMessage() ); 
				}
				
				IPlateManager pm = (IPlateManager) Springcontact.getBean("plateManager");
				barcodesInDatabase = pm.getAllPlateBarcodes();
				
				plateReads = parser.getPlatesInFile();
				tableViewer.setInput(plateReads);
				
				tableViewer.refresh();
			}
		}
	}
	
	class ContentProvider implements IStructuredContentProvider {
	
		public Object[] getElements(Object inputElement) {
			if( inputElement instanceof List ) {
				List list = (List)inputElement;
				return list.toArray();
			}
			return new Object[0];
		}
		
		public void dispose() {
		}
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			OrcaPlateRead plateRead = (OrcaPlateRead)element;
			String result = "";
			switch (columnIndex) {
			case 0:  //import?
				break;
				
			case 1: //PlateRead
				result = plateRead.getName();
				break;
			
			case 2: //BarCode
				result = plateRead.getBarCode();
				break;
				
			case 3: //Database status
				
				if( barcodesInDatabase.contains( plateRead.getBarCode()) ) {
					result = "exists";
				}
				else {
					result = "doesn't exist";
				}
				break;
				
			case 4: //Parse status
				result = plateRead.getError();
				break;
			
			default:
				throw new IllegalArgumentException("unknown columnindex");
			}
			return result;
		}
		
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}
	
	static class Sorter extends ViewerSorter {
		public int compare(Viewer viewer, Object e1, Object e2) {
			OrcaPlateRead plateRead1 = (OrcaPlateRead)e1;
			OrcaPlateRead plateRead2 = (OrcaPlateRead)e2;
			
			Pattern pattern = Pattern.compile("Cytomat384_(\\d+)");
			Matcher matcher1 = pattern.matcher( plateRead1.getName() );
			Matcher matcher2 = pattern.matcher( plateRead2.getName() );
			if( matcher1.matches() && matcher2.matches() ) {
				int M1 = Integer.parseInt( matcher1.group(1) );
				int M2 = Integer.parseInt( matcher2.group(1) );
				return M1 - M2;
			}
			else return plateRead1.getName().compareTo( plateRead2.getName() );
		}
	}

	public void addResultsToCheckedPlates() {

		final IPlateManager pm = (IPlateManager) Springcontact.getBean("plateManager");
		final List<String> platesToGetResults = new ArrayList<String>();
		for( Object o : tableViewer.getCheckedElements() ) {
			OrcaPlateRead plateRead = (OrcaPlateRead)o;
			platesToGetResults.add( plateRead.getBarCode() );
		}
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true, false, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					pm.addResult( Activator.getDefault().getCurrentUser(), parser, platesToGetResults, monitor);
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
