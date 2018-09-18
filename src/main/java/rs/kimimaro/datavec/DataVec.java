package rs.kimimaro.datavec;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.MathOp;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.condition.ConditionOp;
import org.datavec.api.transform.condition.column.IntegerColumnCondition;
import org.datavec.api.transform.quality.DataQualityAnalysis;
import org.datavec.api.transform.quality.columns.ColumnQuality;
import org.datavec.api.transform.schema.InferredSchema;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.integer.ReplaceEmptyIntegerWithValueTransform;
import org.datavec.api.transform.transform.string.ReplaceEmptyStringTransform;
import org.datavec.api.writable.IntWritable;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataVec {

    private ColumnData columnData;
    private DataAnalysis dataAnalysis;

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

        TransformProcess.Builder tpBuilder = new TransformProcess.Builder(inputDataSchema);

        SparkConf conf = new SparkConf();
        conf.setMaster("local[*]");
        conf.setAppName("Vinerator");

        JavaSparkContext sc = new JavaSparkContext(conf);



        File file = new File(filePath);

        JavaRDD<String> stringData = sc.textFile(file.getAbsolutePath());

        JavaRDD<List<Writable>> parsedInputData = stringData.map(new StringToWritablesFunction(new CSVRecordReader()));

        dataAnalysis = new DataAnalysis(inputDataSchema, parsedInputData);

        Integer[] allowedValues = new Integer[] {1, 2, 3, 4, 5};

        for(Column c : columnData.getColumns()) {
            if(c.getType().equals("int")) {
                ColumnQuality cq = dataAnalysis.analyzeColumn(c.getColumnName());

                if (cq.getCountMissing() > 0) {
                    tpBuilder.transform(new ReplaceEmptyIntegerWithValueTransform(c.getColumnName(), 3));
                }
            } else {

                ColumnQuality cq = dataAnalysis.analyzeColumn(c.getColumnName());

                if(cq.getCountInvalid() > 0) {
                    System.out.println("Invalid categorical entries found in column \"" + c.getColumnName() + "\":");
                    AnalyzeSpark.getUnique(c.getColumnName(), inputDataSchema, parsedInputData).stream().forEach(System.out::println);
                }

                if(cq.getCountMissing() > 0) {
                    tpBuilder.transform(new ReplaceEmptyStringTransform(c.getColumnName(), "none"));
                }
            }
        }

        for(Column c : columnData.getColumns()) {
            if(!c.getType().equals("int")) {
                tpBuilder.stringToCategorical(c.getColumnName(), Arrays.asList(c.getOptions()));
                tpBuilder.categoricalToInteger(c.getColumnName());
            }
        }

        for(Column c : columnData.getColumns()) {
            if(c.getType().equals("int")) {
                tpBuilder.integerMathOp(c.getColumnName(), MathOp.Subtract, 1);
            }
        }

        TransformProcess tp = tpBuilder.build();

        //Now, let's execute the transforms we defined earlier:
        JavaRDD<List<Writable>> processedData = SparkTransformExecutor.execute(parsedInputData, tp);

        dataAnalysis = new DataAnalysis(tp.getFinalSchema(), processedData);

        //For the sake of this example, let's collect the data locally and print it:
        JavaRDD<String> processedAsString = processedData.map(new WritablesToStringFunction(","));

        processedAsString.coalesce(1).saveAsTextFile("src/main/resources/data_transformed");

        List<String> processedCollected = processedAsString.collect();

        System.out.println("\n\n---- Processed Data ----");
        for(String s : processedCollected) System.out.println(s);

        System.out.println("\n\nDONE");

        return processedData;
    }


}
