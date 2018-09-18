package rs.kimimaro.datavec;

import org.apache.spark.api.java.JavaRDD;
import org.datavec.api.transform.quality.DataQualityAnalysis;
import org.datavec.api.transform.quality.columns.ColumnQuality;
import org.datavec.api.transform.quality.columns.IntegerQuality;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.Writable;
import org.datavec.spark.transform.AnalyzeSpark;

import java.util.List;

public class DataAnalysis {

    private Schema schema;
    private JavaRDD<List<Writable>> data;
    private DataQualityAnalysis dqa;

    public DataAnalysis(Schema dataSchema, JavaRDD<List<Writable>> data) {
        this.schema = dataSchema;
        this.data = data;

        dqa = AnalyzeSpark.analyzeQuality(dataSchema, data);
    }

    public DataQualityAnalysis analyzeQuality() {
        return dqa;
    }

    public ColumnQuality analyzeColumn(String columnName) {
        int index = dqa.getSchema().getIndexOfColumn(columnName);

        return dqa.getColumnQualityList().get(index);
    }
}
