package rs.kimimaro.datavec;

import org.apache.spark.api.java.JavaRDD;
import org.datavec.api.transform.TransformProcess;
import org.datavec.spark.transform.SparkTransformExecutor;
import org.datavec.spark.transform.misc.WritablesToStringFunction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihailozdravkovic on 9/19/18.
 */
public class TransformData extends Data {

    public TransformData() {
        this.setColumnNames(new ArrayList<String>());
    }

    public static TransformData create(DataVec dataVec, String pathToFile) throws Exception{
        File file = new File(pathToFile);

        BufferedReader br = new BufferedReader(new FileReader(file));

        TransformData md = new TransformData();

        md.setLabelIndex(Integer.parseInt(br.readLine()));

        String line;
        while((line = br.readLine()) != null) {
            md.getColumnNames().add(line);
        }

        md.setNumberOfClasses(md.getColumnNames().size());
        md.setLabelName(md.getColumnNames().get(md.getLabelIndex()));

        TransformProcess tp = new TransformProcess.Builder(dataVec.getDataSchema())
                .removeAllColumnsExceptFor(md.getColumnNames())
                .build();

        md.setData(SparkTransformExecutor.execute(dataVec.getData(), tp));

        JavaRDD<String> processedAsString = md.getData().map(new WritablesToStringFunction(","));

        File outputFile = new File("src/main/resources/data_transformed");

        if(outputFile.exists()) {
            String[]entries = outputFile.list();

            for(String s: entries){
                File currentFile = new File(outputFile.getPath(), s);
                currentFile.delete();
            }

            outputFile.delete();
        }

        processedAsString.coalesce(1).saveAsTextFile("src/main/resources/data_transformed");

        List<String> processedCollected = processedAsString.collect();

        System.out.println("\n\n---- Processed Data ----");
        for(String s : processedCollected) System.out.println(s);

        return md;
    }
}
