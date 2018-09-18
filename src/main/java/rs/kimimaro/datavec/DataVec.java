package rs.kimimaro.datavec;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.quality.DataQualityAnalysis;
import org.datavec.api.transform.schema.InferredSchema;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.Writable;
import org.datavec.spark.transform.AnalyzeSpark;
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

    public JavaRDD<List<Writable>> loadDataFromFile(String filePath) throws Exception {
        Schema.Builder builder = new Schema.Builder();

        for(Column column : columnData.getColumns()) {
            if(column.getType().equals("int")) {
                builder.addColumnInteger(column.getColumnName(), 1, 5);
            } else {
                builder.addColumnCategorical(column.getColumnName(), column.getOptions());
            }
        }

        Schema inputDataSchema = builder.build();

        TransformProcess tp = new TransformProcess.Builder(inputDataSchema).build();

        SparkConf conf = new SparkConf();
        conf.setMaster("local[*]");
        conf.setAppName("Vinerator");

        JavaSparkContext sc = new JavaSparkContext(conf);



        File file = new File(filePath);

        JavaRDD<String> stringData = sc.textFile(file.getAbsolutePath());

        JavaRDD<List<Writable>> parsedInputData = stringData.map(new StringToWritablesFunction(new CSVRecordReader()));

        //Now, let's execute the transforms we defined earlier:
        JavaRDD<List<Writable>> processedData = SparkTransformExecutor.execute(parsedInputData, tp);

        //For the sake of this example, let's collect the data locally and print it:
        JavaRDD<String> processedAsString = processedData.map(new WritablesToStringFunction(","));

        List<String> processedCollected = processedAsString.collect();

        System.out.println("\n\n---- Processed Data ----");
        for(String s : processedCollected) System.out.println(s);

        System.out.println("\n\nDONE");

        return processedData;
    }


}
