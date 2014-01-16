package vgi.display;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import vgi.automata.*;

/*
 * edge_properties.java
 *
 * Created on 2012/1/6, 下午 03:59:32
 */
/**
 *
 * @author bl606
 */
public class EdgePropertiesPanel extends javax.swing.JPanel {

    /**
     * Creates new form edge_properties
     */
    public EdgePropertiesPanel(mxCell cell, Transition transition, DisplayUtil display,JgraphXInternalFrame jif) {
        
        initComponents();
        //this.cell = cell;
        this.transition = transition;
        
        
        this.graph = display.getGraph();
        this.automata = display.getAutomata();
        
        if(this.transition==null){
            if(cell.getSource()==null){
                target=(State)automata.cellToState((mxCell)cell.getTarget());
                initial=target.getInitial();
                System.out.println("Initial!"+initial.getWeight());
            }else if(cell.getTarget()==null){
                source=(State)automata.cellToState((mxCell)cell.getSource());
                finall=source.getFinal();
                
                System.out.println("Final!"+finall.getWeight());
            }
        }else{
            source=transition.getSourceState();
            target=transition.getTargetState();
        }
         
        showLabel();
        showGeoAndDrawingData();
        
        
      
        jInternalFrame=jif;
        
        
    }
    
    private void showLabel() {
        //labelTextField.setText(cell.getValue().toString());
       if(transition!=null) 
            if(transition.getLabel()!=null)
                labelTextField.setText(transition.getLabel().toString());
       if(initial!=null)
           labelTextField.setText(String.valueOf(initial.getWeight()));
       if(finall!=null)
           labelTextField.setText(String.valueOf(finall.getWeight()));
        
    }
    
    private void showGeoAndDrawingData(){
        
        NumberFormat formatter=new DecimalFormat("#.##");
        
        if(transition!=null){ // transition
            State source=transition.getSourceState();
            State target=transition.getTargetState();
       
            if(source==target){ // loop
                angleLabel.setVisible(true);
                angleTextField.setVisible(true);
                lengthLabel.setVisible(true);
                lengthTextField.setVisible(true);
                
                tgd=automata.getTransitionGeometricData(transition);
                if(tgd.controlPoints.size()>0){
                    
                    StateGeometricData sgd=automata.getStateGeometricData(source);
                    Point2D controlpt=tgd.controlPoints.get(0);
                    Point2D center=sgd.getLocation();

                    double deltax=controlpt.getX()-center.getX();
                    double deltay=controlpt.getY()-center.getY();
                    double length=Math.sqrt(deltax*deltax+deltay*deltay);
                    
                    double lengthState=Math.sqrt(sgd.getWidth()*sgd.getWidth()+sgd.getHeight()*sgd.getHeight())/2;
                    double lengthRatio=length/lengthState-1;
                    lengthTextField.setText(formatter.format(lengthRatio));

                    double theta=Math.atan2(deltay,deltax);
                    theta=Math.toDegrees(theta);
                    angleTextField.setText(formatter.format(theta));

                }
                
            }else{
                angleLabel.setVisible(false);
                angleTextField.setVisible(false);
                lengthLabel.setVisible(false);
                lengthTextField.setVisible(false);
                
                tgd=automata.getTransitionGeometricData(transition);
                if(tgd.controlPoints.size()>0){
                    Point2D controlpt=tgd.controlPoints.get(0);
                    Point2D center=source.getGeometricData().getLocation();

                    double deltax=controlpt.getX()-center.getX();
                    double deltay=controlpt.getY()-center.getY();
                    double length=Math.sqrt(deltax*deltax+deltay*deltay);
                    lengthTextField.setText(formatter.format(length));

                    double theta=Math.atan2(deltay,deltax);
                    theta=Math.toDegrees(theta);
                    angleTextField.setText(formatter.format(theta));

                }
            }
            
            tdd=automata.getTransitionDrawingData(transition);
            
        }else{ //initial/final
             if(source==null){
//            mxPoint term=cell.getGeometry().getTerminalPoint(true);
//            mxPoint center=new mxPoint(target.getGeometry().getCenterX(),target.getGeometry().getCenterY());
//            
//            //System.out.println(term.toString()+" "+center.toString());
//            
//            double deltax=term.getX()-center.getX();
//            double deltay=term.getY()-center.getY();
//            double length=Math.sqrt(deltax*deltax+deltay*deltay);
//            lengthTextField.setText(formatter.format(length));
//            
            //State state=(State)automata.cellToState(target);
                double theta=automata.getIniFinGeometricData(target, true).direction;
                theta=Math.toDegrees(theta);

                angleTextField.setText(formatter.format(theta));

                double length=automata.getIniFinGeometricData(target, true).lengthRatio;
                lengthTextField.setText(formatter.format(length));
                
                
                tdd=automata.getIniFinDrawingData(target,true);
                
            }else if(target==null){
//            mxPoint term=cell.getGeometry().getTerminalPoint(false);
//            mxPoint center=new mxPoint(source.getGeometry().getCenterX(),source.getGeometry().getCenterY());
//            
//            //System.out.println(term.toString()+" "+center.toString());
//             
//            double deltax=term.getX()-center.getX();
//            double deltay=term.getY()-center.getY();
//            double length=Math.sqrt(deltax*deltax+deltay*deltay);
//            lengthTextField.setText(formatter.format(length));
//            
//            double theta=Math.atan2(deltay,deltax);
//            theta=Math.toDegrees(theta);
//            angleTextField.setText(formatter.format(theta));
            
            //State state=(State)automata.cellToState(source);
                double theta=automata.getIniFinGeometricData(source, false).direction;
                theta=Math.toDegrees(theta);

                angleTextField.setText(formatter.format(theta));

                double length=automata.getIniFinGeometricData(source, false).lengthRatio;
                lengthTextField.setText(formatter.format(length));
                
                tdd=automata.getIniFinDrawingData(source, false);
            }
            
        }
//else if(source==target){
            
            
//            mxCell cell=automata.stateToCell(source);
//            if(cell.getGeometry().getPoints().size()>0){
//                mxPoint controlpt=cell.getGeometry().getPoints().get(0);
//                mxPoint center=new mxPoint(source.getGeometricData().getX(),source.getGeometricData().getY());
//
//                double deltax=controlpt.getX()-center.getX();
//                double deltay=controlpt.getY()-center.getY();
//                double length=Math.sqrt(deltax*deltax+deltay*deltay);
//                lengthTextField.setText(formatter.format(length));
//
//                double theta=Math.atan2(deltay,deltax);
//                theta=Math.toDegrees(theta);
//                angleTextField.setText(formatter.format(theta));
//            
//            }
//            tgd=automata.getTransitionGeometricData(transition);
//            if(tgd.controlPoints.size()>0){
//                Point2D controlpt=tgd.controlPoints.get(0);
//                Point2D center=source.getGeometricData().getLocation();
//                        
//                double deltax=controlpt.getX()-center.getX();
//                double deltay=controlpt.getY()-center.getY();
//                double length=Math.sqrt(deltax*deltax+deltay*deltay);
//                lengthTextField.setText(formatter.format(length));
//
//                double theta=Math.atan2(deltay,deltax);
//                theta=Math.toDegrees(theta);
//                angleTextField.setText(formatter.format(theta));
//            
//            }
        //}
        
//        Map<String,Object> styles=graph.getCellStyle(cell);
        
        if(tdd==null) return;
        String color=tdd.getStrokeColor();
        strokeColor=Color.decode(color);
        strokeColorButton.setBackground(strokeColor);
        
        strokeWidth=Double.valueOf(tdd.getStrokeWidth());
        if(strokeWidth!=null){
            int ind=strokeWidth.intValue();
            strokeWidthBox.setSelectedIndex(ind-1);
        }
        String startArrow=tdd.getStartArrow();
        if(startArrow!=null) startStyleComboBox.setSelectedItem(startArrow);
               
        String endArrow=tdd.getEndArrow();
        if(endArrow!=null) endStyleComboBox.setSelectedItem(endArrow);
        
        
        //TODO: there's no textShape field in TransitionDrawingData
        String textShape="";
        if(textShape.equals("curve")) curveLabelCheckBox.setSelected(true);
        
        
        
    }
    private void setStartEndArrow(JComboBox comboBox, Boolean startEnd) {
        //String arrowDir = (startEnd) ? "startArrow" : "endArrow";
        String arrowType = ((String)comboBox.getSelectedItem()).toLowerCase();
        
        if(startEnd == true)
        {
            tdd.setStartArrow(arrowType);
        }
        else
        {
            tdd.setEndArrow(arrowType);
        }
        if(transition!=null) automata.setTransitionDrawingData(transition, tdd);
        else if(initial!=null) automata.setIniFinDrawingData(target, tdd, true);
        else if(finall!=null) automata.setIniFinDrawingData(source, tdd, false);
        
        if(jInternalFrame!=null) jInternalFrame.setModified(true);
    }
    
    public void setStrokeColor(mxGraph graph, Color color) {
//        Object[] edge = {cell};
//        graph.setCellStyles("strokeColor", mxUtils.hexString(color), edge);
        if(color!=null){
        tdd.setStrokeColor(mxUtils.hexString(color));
        if(transition!=null) automata.setTransitionDrawingData(transition, tdd);
        else if(initial!=null) automata.setIniFinDrawingData(target, tdd, true);
        else if(finall!=null) automata.setIniFinDrawingData(source, tdd, false);
        
        if(jInternalFrame!=null)jInternalFrame.setModified(true);
        }
    }

    public void setStrokeWidth(mxGraph graph, float width) {
//        Object[] edge = {cell};
//        String wid = String.valueOf(width);
//        graph.setCellStyles("strokeWidth", wid, edge);
        
        tdd.setStrokeWidth(width);
        if (transition != null) {
            automata.setTransitionDrawingData(transition, tdd);
        } else if (initial != null) {
            automata.setIniFinDrawingData(target, tdd, true);
        } else if (finall != null) {
            automata.setIniFinDrawingData(source, tdd, false);
        }

        if(jInternalFrame!=null) jInternalFrame.setModified(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel3 = new javax.swing.JLabel();
        labelLabel = new javax.swing.JLabel();
        labelTextField = new javax.swing.JTextField();
        angleLabel = new javax.swing.JLabel();
        angleTextField = new javax.swing.JTextField();
        lengthLabel = new javax.swing.JLabel();
        lengthTextField = new javax.swing.JTextField();
        stylePanel = new javax.swing.JPanel();
        startStyleLabel = new javax.swing.JLabel();
        startStyleComboBox = new javax.swing.JComboBox();
        ednStyleLabel = new javax.swing.JLabel();
        endStyleComboBox = new javax.swing.JComboBox();
        strokeLabel = new javax.swing.JLabel();
        strokeColorButton = new javax.swing.JButton();
        strokeWidthBox = new javax.swing.JComboBox();
        curveLabelCheckBox = new javax.swing.JCheckBox();

        jLabel3.setText("jLabel3");

        setPreferredSize(new java.awt.Dimension(325, 362));
        setLayout(new java.awt.GridBagLayout());

        labelLabel.setText("Label :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(labelLabel, gridBagConstraints);

        labelTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelTextFieldMouseClicked(evt);
            }
        });
        labelTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(labelTextField, gridBagConstraints);

        angleLabel.setText("Angle :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(angleLabel, gridBagConstraints);

        angleTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                angleTextFieldActionPerformed(evt);
            }
        });
        angleTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                angleTextFieldKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(angleTextField, gridBagConstraints);

        lengthLabel.setText("LengthRatio:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(lengthLabel, gridBagConstraints);

        lengthTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lengthTextFieldActionPerformed(evt);
            }
        });
        lengthTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lengthTextFieldKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(lengthTextField, gridBagConstraints);

        stylePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Style", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        stylePanel.setLayout(new java.awt.GridBagLayout());

        startStyleLabel.setText("Start_Style :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        stylePanel.add(startStyleLabel, gridBagConstraints);

        startStyleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "classic", "block", "open", "oval", "diamond" }));
        startStyleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startStyleComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        stylePanel.add(startStyleComboBox, gridBagConstraints);

        ednStyleLabel.setText("End_Style :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        stylePanel.add(ednStyleLabel, gridBagConstraints);

        endStyleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "classic", "block", "open", "oval", "diamond" }));
        endStyleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endStyleComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        stylePanel.add(endStyleComboBox, gridBagConstraints);

        strokeLabel.setText("Stroke :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        stylePanel.add(strokeLabel, gridBagConstraints);

        strokeColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strokeColorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        stylePanel.add(strokeColorButton, gridBagConstraints);

        strokeWidthBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        strokeWidthBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strokeWidthBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        stylePanel.add(strokeWidthBox, gridBagConstraints);

        curveLabelCheckBox.setText("curve label");
        curveLabelCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                curveLabelCheckBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 10;
        stylePanel.add(curveLabelCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(stylePanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void startStyleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startStyleComboBoxActionPerformed
        if(jInternalFrame!=null) setStartEndArrow((JComboBox)evt.getSource(), true);
    }//GEN-LAST:event_startStyleComboBoxActionPerformed

    private void endStyleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endStyleComboBoxActionPerformed
        if(jInternalFrame!=null) setStartEndArrow((JComboBox)evt.getSource(), false);
    }//GEN-LAST:event_endStyleComboBoxActionPerformed

    private void labelTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelTextFieldMouseClicked
//        ExpressionEditor editor = new ExpressionEditor(
//                new JFrame(), 
//                true, 
//                (WeightedRegularExpression) ((mxCell) cell).getValue());
        Object label=null;
        if(transition!=null){
            label=transition.getLabel();
            ExpressionEditor editor = new ExpressionEditor(
                new JFrame(), 
                true, 
                (WeightedRegularExpression)label);
            editor.setVisible(true);
            //transition.setLabel(editor.getExpression());
            automata.setTransitionLabel(transition, editor.getExpression());
        }else if(initial!=null){
            String str = ((JTextField)evt.getSource()).getText();
            automata.setInitialWeight(target,str );
            
        }else if(finall!=null){ 
            label=finall.getWeight();
             String str = ((JTextField)evt.getSource()).getText();
             automata.setFinalWeight(source,str );
        
        }
      
        
    }//GEN-LAST:event_labelTextFieldMouseClicked

    private void strokeColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strokeColorButtonActionPerformed
        strokeColor=JColorChooser.showDialog( this,
                     "Fill color", strokeColor );
        strokeColorButton.setBackground(strokeColor);
        
        if(jInternalFrame!=null) setStrokeColor(graph,strokeColor);
        
    }//GEN-LAST:event_strokeColorButtonActionPerformed

    private void strokeWidthBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strokeWidthBoxActionPerformed
        JComboBox cb = (JComboBox)evt.getSource();
        String width=(String)cb.getSelectedItem();
        float wid=Float.parseFloat(width);
        if(jInternalFrame!=null) setStrokeWidth(graph,wid);
    }//GEN-LAST:event_strokeWidthBoxActionPerformed

    private void angleTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_angleTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String str = ((JTextField)evt.getSource()).getText();
            if (str.compareTo("") == 0)
                str = null;
            if(jInternalFrame!=null) setAngle(str);
        }
         graph.refresh();
        
    }//GEN-LAST:event_angleTextFieldKeyPressed

    private void lengthTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lengthTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String str = ((JTextField)evt.getSource()).getText();
            if (str.compareTo("") == 0)
                str = null;
            if(jInternalFrame!=null) setLength(str);
        }
         graph.refresh();
    }//GEN-LAST:event_lengthTextFieldKeyPressed

    private void curveLabelCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_curveLabelCheckBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED){
            
            if(jInternalFrame!=null) setCurveLabel(true);
        }else if (evt.getStateChange() == ItemEvent.DESELECTED){
            if(jInternalFrame!=null) setCurveLabel(false);
        }
    }//GEN-LAST:event_curveLabelCheckBoxItemStateChanged

    private void labelTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelTextFieldActionPerformed
        // TODO add your handling code here:
        System.out.println("do nothing");
    }//GEN-LAST:event_labelTextFieldActionPerformed

    private void angleTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_angleTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_angleTextFieldActionPerformed

    private void lengthTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lengthTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lengthTextFieldActionPerformed

    private void setAngle(String str){
//        mxGeometry geo=cell.getGeometry();
        Double angle_=Double.valueOf(str);
        angle_=Math.toRadians(angle_);
        
        if(angle_!=null){
//            mxCell source=(mxCell)cell.getSource();
//            mxCell target=(mxCell)cell.getTarget();
            if(source==target){ // loop
                Point2D ctrlpt=tgd.controlPoints.get(0);
                
                double deltax=ctrlpt.getX()-source.getGeometricData().getX();
                double deltay=ctrlpt.getY()-source.getGeometricData().getY();
                double length=Math.sqrt(deltax*deltax+deltay*deltay);
                
                Point2D newterm=new Point2D.Double(source.getGeometricData().getX()+length*Math.cos(angle_),
                                            source.getGeometricData().getY()+length*Math.sin(angle_));
                ArrayList<Point2D> list=new ArrayList<Point2D>();
                list.add(newterm);
                tgd.controlPoints=list;
                automata.setTransitionGeometricData(transition, tgd);
                
//                mxPoint ctrlpt=geo.getPoints().get(0);
//                
//                double deltax=ctrlpt.getX()-source.getGeometry().getCenterX();
//                double deltay=ctrlpt.getY()-source.getGeometry().getCenterY();
//                double length=Math.sqrt(deltax*deltax+deltay*deltay);
//                
//                mxPoint newterm=new mxPoint(source.getGeometry().getCenterX()+length*Math.cos(angle_),
//                                            source.getGeometry().getCenterY()+length*Math.sin(angle_));
//                ArrayList<mxPoint> list=new ArrayList<mxPoint>();
//                list.add(newterm);
//                geo.setPoints(list);
//                cell.setGeometry(geo);
            }else if(source==null){ // initial
//                State state=(State) automata.cellToState(target);
                IniFinGeometricData geodata=target.getInitial().geodata;
                geodata.direction=angle_;
                automata.setIniFinGeometricData(target, geodata, true);
                
//                mxPoint terminal=geo.getTerminalPoint(true);
//                
//                mxGeometry vertexgeo=target.getGeometry();
//                mxPoint center=new mxPoint(vertexgeo.getCenterX(),vertexgeo.getCenterY());
//                
//                double deltax=terminal.getX()-center.getX();
//                double deltay=terminal.getY()-center.getY();
//                double length=Math.sqrt(deltax*deltax+deltay*deltay);
//                
//                
//                mxPoint newterm=new mxPoint();
//                newterm.setX(center.getX()+length*Math.cos(angle_));
//                newterm.setY(center.getY()+length*Math.sin(angle_));
//                geo.setTerminalPoint(newterm, true);
                
            }else if(target==null){ // final
                
//                State state=(State) automata.cellToState(source);
                IniFinGeometricData geodata=source.getFinal().geodata;
                geodata.direction=angle_;
                automata.setIniFinGeometricData(source, geodata, false);
                
                
//                mxPoint terminal=geo.getTerminalPoint(false);
//                
//                mxGeometry vertexgeo=source.getGeometry();
//                mxPoint center=new mxPoint(vertexgeo.getCenterX(),vertexgeo.getCenterY());
//                
//                double deltax=terminal.getX()-center.getX();
//                double deltay=terminal.getY()-center.getY();
//                double length=Math.sqrt(deltax*deltax+deltay*deltay);
//                
//                
//                mxPoint newterm=new mxPoint();
//                newterm.setX(center.getX()+length*Math.cos(angle_));
//                newterm.setY(center.getY()+length*Math.sin(angle_));
//                geo.setTerminalPoint(newterm, false);
               
                
            }
            
//            cell.setGeometry(geo);
            jInternalFrame.setModified(true);
         }
    }
    private void setLength(String str){
//        mxGeometry geo=cell.getGeometry();
        Double lengthRatio=Double.valueOf(str);
        
        if(lengthRatio!=null){
//            mxCell source=(mxCell)cell.getSource();
//            mxCell target=(mxCell)cell.getTarget();
            if(source==target){ // loop
                Point2D ctrlpt=tgd.controlPoints.get(0);
                
                StateGeometricData sgd=automata.getStateGeometricData(source);
                double deltax=ctrlpt.getX()-sgd.getX();
                double deltay=ctrlpt.getY()-sgd.getY();
                
                double theta=Math.atan2(deltay,deltax);
                
                double lengthState=Math.sqrt(sgd.getWidth()*sgd.getWidth()+sgd.getHeight()*sgd.getHeight())/2;
                double length=lengthState*(1+lengthRatio);
                Point2D newterm=new Point2D.Double(sgd.getX()+length*Math.cos(theta),
                                            sgd.getY()+length*Math.sin(theta));
                ArrayList<Point2D> list=new ArrayList<Point2D>();
                list.add(newterm);
                tgd.controlPoints=list;
                automata.setTransitionGeometricData(transition, tgd);
                
//                mxPoint ctrlpt=geo.getPoints().get(0);
//                
//                double deltax=ctrlpt.getX()-source.getGeometry().getCenterX();
//                double deltay=ctrlpt.getY()-source.getGeometry().getCenterY();
//                
//                double theta=Math.atan2(deltay,deltax);
//                
//                mxPoint newterm=new mxPoint(source.getGeometry().getCenterX()+len*Math.cos(theta),
//                                            source.getGeometry().getCenterY()+len*Math.sin(theta));
//                ArrayList<mxPoint> list=new ArrayList<mxPoint>();
//                list.add(newterm);
//                geo.setPoints(list);
//                cell.setGeometry(geo);
                
            }else if(source==null){ // initial
                
                IniFinGeometricData igd=initial.geodata;
                igd.lengthRatio=lengthRatio;
                automata.setIniFinGeometricData(target, igd, true);
                
//                mxPoint terminal=geo.getTerminalPoint(true);
//                
//                mxGeometry vertexgeo=target.getGeometry();
//                mxPoint center=new mxPoint(vertexgeo.getCenterX(),vertexgeo.getCenterY());
//                
//                double deltax=terminal.getX()-center.getX();
//                double deltay=terminal.getY()-center.getY();
//                double theta=Math.atan2(deltay,deltax);
//                
//                mxPoint newterm=new mxPoint();
//                newterm.setX(center.getX()+len*Math.cos(theta));
//                newterm.setY(center.getY()+len*Math.sin(theta));
//                geo.setTerminalPoint(newterm, true);
//                
            }else if(target==null){ // final
                
                IniFinGeometricData fgd=finall.geodata;
                fgd.lengthRatio=lengthRatio;
                automata.setIniFinGeometricData(source, fgd, false);
                
                
//                mxPoint terminal=geo.getTerminalPoint(false);
//                
//                mxGeometry vertexgeo=source.getGeometry();
//                mxPoint center=new mxPoint(vertexgeo.getCenterX(),vertexgeo.getCenterY());
//                
//                double deltax=terminal.getX()-center.getX();
//                double deltay=terminal.getY()-center.getY();
//                double theta=Math.atan2(deltay,deltax);
//                
//                
//                mxPoint newterm=new mxPoint();
//                newterm.setX(center.getX()+len*Math.cos(theta));
//                newterm.setY(center.getY()+len*Math.sin(theta));
//                geo.setTerminalPoint(newterm, false);
               
                
            }
            
//            cell.setGeometry(geo);
            jInternalFrame.setModified(true);
         }
        
        
        
    }
    private void setCurveLabel(boolean useCurveLabel){
        
        // TODO
        mxCell cell=automata.transitionToCell(transition);
        Object[] edge = {cell};
        if(useCurveLabel)
            graph.setCellStyles("textShape","curve",edge);
        else
            graph.setCellStyles("textShape","default",edge);
        
        if(jInternalFrame!=null) jInternalFrame.setModified(true);
        
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel angleLabel;
    private javax.swing.JTextField angleTextField;
    private javax.swing.JCheckBox curveLabelCheckBox;
    private javax.swing.JLabel ednStyleLabel;
    private javax.swing.JComboBox endStyleComboBox;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel labelLabel;
    private javax.swing.JTextField labelTextField;
    private javax.swing.JLabel lengthLabel;
    private javax.swing.JTextField lengthTextField;
    private javax.swing.JComboBox startStyleComboBox;
    private javax.swing.JLabel startStyleLabel;
    private javax.swing.JButton strokeColorButton;
    private javax.swing.JLabel strokeLabel;
    private javax.swing.JComboBox strokeWidthBox;
    private javax.swing.JPanel stylePanel;
    // End of variables declaration//GEN-END:variables
    //private mxCell cell;
    private mxGraph graph;
    private Automata automata;
    private Transition transition;
    private DisplayUtil display;
    private Color strokeColor = Color.white;
    private Double strokeWidth = null;
    private double angle;
    JgraphXInternalFrame jInternalFrame;
    
    private Initial initial;
    private Final finall;
    private State source,target;
    
    private TransitionDrawingData tdd;
    private TransitionGeometricData tgd;
    
}