package rs.kimimaro;

import rs.kimimaro.MLP.MLPHappinessClassifier;
import rs.kimimaro.datavec.ColumnData;
import rs.kimimaro.datavec.DataVec;

import java.io.File;
import java.io.IOException;

/**
 * Created by mihailozdravkovic on 9/15/18.
 */
public class Application {

    public static void main(String[] args) {
        if(!checkProgramArgs(args)) {
            System.out.println("Exiting.");
            return;
        }

        ColumnData columnData = ColumnData.readColumnsFromFile(args[1]);
        DataVec dataVec = new DataVec(columnData);

        try {
            new MLPHappinessClassifier(dataVec.loadDataFromFile(args[0]), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkProgramArgs(String[] args) {
        if(args.length != 2) {
            System.out.println("This program takes two arguments:\n" +
                    "Argument 1: path to responses CSV file\n" +
                    "Argument 2: path to columns CSV file");
            return false;
        }

        String responsesFilePath = args[0];
        String columnsFilePath = args[1];

        File responsesFile = new File(responsesFilePath);

        if(!responsesFile.exists()) {
            System.out.println("ERROR: '" + responsesFilePath + "' " +
                    "no such file.");
            return false;
        }

        File castFile = new File(columnsFilePath);

        if(!castFile.exists()) {
            System.out.println("ERROR: '" + columnsFilePath + "' " +
                    "no such file.");
            return false;
        }

        return true;
    }
}
