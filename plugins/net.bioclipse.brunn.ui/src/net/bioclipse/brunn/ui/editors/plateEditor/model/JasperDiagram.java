package net.bioclipse.brunn.ui.editors.plateEditor.model;

import java.util.ArrayList;
import java.util.List;


/**
 * @author jonalv
 *
 */
public class JasperDiagram {
    private String       name;
    private String       ic50;
    private String       unit;
    private List<JasperPoint> points;
    
    public JasperDiagram() {
        points = new ArrayList<JasperPoint>();
    }
    
    public void setName( String name ) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setIc50( String ic50 ) {
        this.ic50 = ic50;
    }
    
    public String getIc50() {
        return ic50;
    }

    public void setUnit( String unit ) {

        this.unit = unit;
    }

    public String getUnit() {

        return unit;
    }

    public void setPoints( List<JasperPoint> points ) {
        this.points = points;
    }
    
    public List<JasperPoint> getPoints() {
        return points;
    }
}
