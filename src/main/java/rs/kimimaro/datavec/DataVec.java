package rs.kimimaro.datavec;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.InferredSchema;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.Writable;
import org.datavec.spark.transform.SparkTransformExecutor;
import org.datavec.spark.transform.misc.StringToWritablesFunction;
import org.datavec.spark.transform.misc.WritablesToStringFunction;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.io.ClassPathResource;
import rs.kimimaro.common.Column;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataVec {

    private ColumnData columnData;

    public DataVec(ColumnData cd) {
        columnData = cd;
    }

    public DataSetIterator loadDataFromFile(String filePath) throws Exception {
        Schema.Builder builder = new Schema.Builder();

        for(Column column : columnData.getColumns()) {
            if(column.getType().equals("int")) {
                builder.addColumnInteger(column.getColumnName(), 1, 5);
            } else {
                builder.addColumnCategorical(column.getColumnName(), column.getOptions());
            }
        }

        Schema inputDataSchema = builder.build();

        File file = new File(filePath);
        RecordReader recordReader = new CSVRecordReader(1, ',');

        recordReader.initialize(new FileSplit(file));

        return new RecordReaderDataSetIterator(recordReader, 100,123,5);
    }
}
