package rs.kimimaro.datavec;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.nd4j.linalg.io.ClassPathResource;

import java.io.IOException;

public class WineDataVec implements Runnable {

    private int numLinesToSkip = 0;
    private char delimiter = ',';
    private String filePath;


    public void run() {
        try {
            RecordReader recordReader = new CSVRecordReader(numLinesToSkip, delimiter);
            recordReader.initialize(new FileSplit(new ClassPathResource(filePath).getFile()));

            int labelIndex = 0;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
