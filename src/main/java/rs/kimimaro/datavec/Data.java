package rs.kimimaro.datavec;

import org.apache.spark.api.java.JavaRDD;
import org.datavec.api.writable.Writable;

import java.util.List;

abstract public class Data {

    private int numberOfClasses;
    private int labelIndex;
    private List<String> columnNames;
    private String labelName;
    private JavaRDD<List<Writable>> data;

    public int getNumberOfClasses() {
        return numberOfClasses;
    }

    public void setNumberOfClasses(int numberOfClasses) {
        this.numberOfClasses = numberOfClasses;
    }

    public int getLabelIndex() {
        return labelIndex;
    }

    public void setLabelIndex(int labelIndex) {
        this.labelIndex = labelIndex;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public JavaRDD<List<Writable>> getData() {
        return data;
    }

    public void setData(JavaRDD<List<Writable>> data) {
        this.data = data;
    }
}
