package net.bioclipse.brunn.ui.editors.plateEditor.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Point;

import net.bioclipse.brunn.pojos.AbstractSample;
import net.bioclipse.brunn.pojos.CellSample;
import net.bioclipse.brunn.pojos.DrugSample;
import net.bioclipse.brunn.pojos.PatientSample;
import net.bioclipse.brunn.pojos.Well;
import net.bioclipse.brunn.results.PlateResults;
import net.bioclipse.brunn.ui.editors.plateEditor.Summary;
import net.bioclipse.brunn.ui.explorer.model.nonFolders.Compound;
import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableDefaultModel;
import de.kupzog.ktable.renderers.FixedCellRenderer;
import de.kupzog.ktable.renderers.TextCellRenderer;

public class SummaryTableModel extends KTableDefaultModel {

	/*
     * a representation of the matrix
     */
    private String[][] matrix;
    private TextCellRenderer   renderer      = new TextCellRenderer(  TextCellRenderer.INDICATION_FOCUS_ROW );
    private KTableCellRenderer fixedRenderer = new FixedCellRenderer( FixedCellRenderer.STYLE_PUSH       |
                                                                      FixedCellRenderer.INDICATION_FOCUS );
    private net.bioclipse.brunn.pojos.Plate plate; 
    private KTable table;
    private Summary editor;
    private ArrayList<String> columnNames;
	private int cols;
	private int rows;
    
	public SummaryTableModel( net.bioclipse.brunn.pojos.Plate plate,
			                   KTable table,
			                   Summary editor,
			                   PlateResults plateResults, 
			                   Map<String, String> cvMap ) {
		
		columnNames = new ArrayList<String>();
		Collections.addAll( columnNames, new String[] {"Cell Type", "Compound Names", "Concentration"} );
		columnNames.addAll( plate.getWellFunctionNames() );
		columnNames.add( "CV%" );
		
		/*
		 * set up the matrix from the plate 
		 */
		List<String[]> values = new ArrayList<String[]>();
		for ( Well well : plate.getWells() ) {
			
			//We are not interested in listing values for 
			//wells without at least one more sample than the seeded cell
			if(well.getSampleContainer().getSamples().size() < 2) {
				continue;
			}
			
			String[] row = new String[columnNames.size()+1];

			for (int col = 0; col < columnNames.size(); col++) {
				
				//Cell Type
				if(col == 0) {
					row[col] = getCellType(well);
					continue;
				}
				
				DrugSample[] drugSamples = getDrugSamples(well);
				
				//Compound Names
				if(col == 1) {
					row[col] = getCompoundNames(drugSamples);
					continue;
				}
				
				//Concentration
				if(col == 2) {
					row[col] = getConcentrations(drugSamples);
					continue;
				}
				
				//Wellfunctions
				for( String wellFunction : columnNames.subList(3, columnNames.size()) ) {
					if ( "raw".equals(wellFunction) && 
						 well.isOutlier() ) {
						row[col] = "outlier";
					}
					else {
						try {
							DecimalFormat df = new DecimalFormat("0");
							row[col] = df.format( plateResults.getValue( well.getCol(), 
									                                     well.getRow(), 
									                                     wellFunction) );
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					col++;
				}
				
				row[col-1] = cvMap.get(row[1]+row[2]);
				if( row[col-1] == null ) {
					row[col-1] = "?";
				}
				row[row.length-1] = well.getName();
				break;
			}
			values.add(row);
		}
		Collections.sort(values, new Comparator<String[]>() {
			public int compare(String[] o1, String[] o2) {
				int c = o1[1].compareTo( o2[1] );
				if ( c != 0 ) 
					return c;
				c = Double.compare( Double.parseDouble( 
		                                o1[2].contains(" ") ? o1[2].substring( 0, 
		                		                                               o1[2].indexOf(' '))
		                		                            : o1[2]), 
		                            Double.parseDouble(
		                                o2[2].contains(" ") ? o2[2].substring( 0, 
		                				                  					   o2[2].indexOf(' '))
		                				                    : o2[2]) );
					return c;
			}
		});
		matrix = new String[values.size()][columnNames.size()];
		int rowNumber = 0;
		for(String[] row : values) {
			matrix[rowNumber++] = row;
		}
		rows = matrix.length;
		cols = columnNames.size();
		this.plate  = plate;
		this.table  = table;
		this.editor = editor;
		initialize();
	}

	private String getConcentrations(DrugSample[] drugSamples) {

		StringBuffer result = new StringBuffer();
		
		for( DrugSample drugSample : drugSamples ) {
			result.append( drugSample.getConcentration() );
			result.append(" ");
			result.append( drugSample.getConcUnit() == null ? "?"
			    		                                    : drugSample
			    		                                      .getConcUnit()
			    		                                      .toString() );
			result.append(" | ");
		}
		//remove the last " | " and return
		return result.substring(0, result.length() - 3);
	}

	private DrugSample[] getDrugSamples(Well well) {
		ArrayList<DrugSample> result = new ArrayList<DrugSample>();
		for( AbstractSample s : well.getSampleContainer().getSamples() ) {
			if(s instanceof DrugSample) {
				result.add(( (DrugSample)s ));
			}
		}
		Collections.sort(result, new Comparator<DrugSample>() {
			public int compare(DrugSample o1, DrugSample o2) {
				return o1.getName().compareTo( o2.getName() );
			}
		});
		return result.toArray( new DrugSample[0] );
	}

	private String getCellType(Well well) {
		
		for( AbstractSample s : well.getSampleContainer().getSamples() ) {
			if(s instanceof CellSample) {
				return ( (CellSample)s ).getCellOrigin().getName();
			}
			if(s instanceof PatientSample) {
				return ( (PatientSample) s).getPatientOrigin().getName();
			}
		}
		return "";
	}

	private String getCompoundNames(DrugSample[] drugSamples) {
		
		String result = "";
		for( DrugSample drugSample : drugSamples ) {
			result += drugSample.getName() + " | ";
		}
		result = result.substring(0, result.length()-3);
		return result;
	}

	@Override
	public KTableCellEditor doGetCellEditor(int col, int row) {
		return null;
	}

	@Override
	public KTableCellRenderer doGetCellRenderer(int col, int row) {
		if (isFixedCell(col, row))
            return fixedRenderer;
        else
            return renderer;
	}

	@Override
	public int doGetColumnCount() {
		return cols;
	}

	@Override
	public Object doGetContentAt(int col, int row) {
		if( col >= 0 && row >= 1 ) {
			return matrix[row-1][col];
		}
		if( row == 0 ) {
			return columnNames.get(col);
		}
		throw new IllegalArgumentException(col +" or " + row + " not a legal argument");
	}
	
	@Override
	public int doGetRowCount() {
		return rows+1;
	}

	@Override
	public void doSetContentAt(int col, int row, Object value) {
	
	}

	@Override
	public int getInitialColumnWidth(int column) {
		return 150;
	}

	@Override
	public int getInitialRowHeight(int row) {
		return 20;
	}

	public int getFixedHeaderColumnCount() {
		return 0;
	}

	public int getFixedHeaderRowCount() {
		return 1;
	}

	public int getFixedSelectableColumnCount() {
		return 0;
	}

	public int getFixedSelectableRowCount() {
		return 0;
	}

	public int getRowHeightMinimum() {
		return 30;
	}

	public boolean isColumnResizable(int col) {
		return true;
	}

	public boolean isRowResizable(int row) {
		return true;
	}
	
	public String doGetTooltipAt(int col, int row) {
        return null;
    }
	
	public Well getWellFromSelectedRowNumber(int rowNumber) {
		for ( Well well : plate.getWells() ) {
			if( matrix[rowNumber-1][matrix[rowNumber-1].length-1].equals(well.getName()) ) {
				return well;
			}
		}
		throw new IllegalStateException( " could not find well corresponding to row: "+rowNumber);
	}
}
