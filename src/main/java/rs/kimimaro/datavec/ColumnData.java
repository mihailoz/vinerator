package rs.kimimaro.datavec;

import rs.kimimaro.common.Column;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihailozdravkovic on 9/17/18.
 */
public class ColumnData {

    private List<Column> columns;

    public ColumnData() {
        columns = new ArrayList<>();
    }

    public static ColumnData readColumnsFromFile(String pathToCVS) {
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToCVS))) {
            ColumnData cd = new ColumnData();

            reader.lines().forEach(line -> {
                String[] split = line.split("\",\"");
                if (split.length == 3) {

                    String columnName = split[1];
                    String columnDescription = split[0].substring(1, split[0].length());
                    String columnType = split[2].substring(0, split[2].length() - 1).toLowerCase();

                    if(!columnType.equals("int")) {
                        String[] options = columnType.split("-");
                        Column c = cd.addColumn(columnDescription, columnName, "categorical");
                        c.setOptions(options);
                    } else {
                        cd.addColumn(columnDescription, columnName, columnType);
                    }
                }
            });
            cd.removeColumn(0);

            return cd;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return null;
    }

    public Column addColumn(String description, String name, String type) {
        Column newColumn = new Column(description, name, type);

        for (Column c : columns) {
            if(c.equals(newColumn)) {
                return c;
            }
        }

        this.columns.add(newColumn);
        return newColumn;
    }

    public boolean removeColumn(int index) {
        if(index < columns.size()) {
            columns.remove(index);
            return true;
        }
        return false;
    }

    public int size() {
        return columns.size();
    }

    public List<Column> getColumns() {
        return columns;
    }

    public String toString() {
        String s = "Column data:\n";

        for(Column c : columns) {
            s += c.getColumnDescription() + " - Short Name: " + c.getColumnName() + " - ";
            if(c.getType().equals("int")) {
                s += "integer\n";
            } else {
                for (String option : c.getOptions()) {
                    s += option + ";";
                }
                s += "\n";
            }
        }

        return s;
    }
}
