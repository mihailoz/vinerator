package rs.kimimaro.common;

import org.jetbrains.annotations.NotNull;

/**
 * Created by mihailozdravkovic on 9/17/18.
 */
public class Column implements Comparable<Column> {
    private String columnDescription;
    private String columnName;
    private String type;
    private String[] options;

    public Column(String desc, String name, String type) {
        columnDescription = desc;
        columnName = name;
        this.type = type;
    }

    public String getColumnDescription() {
        return columnDescription;
    }

    public void setColumnDescription(String columnDescription) {
        this.columnDescription = columnDescription;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    @Override
    public int compareTo(@NotNull Column o) {
        if(this.columnName != null) {
            return this.columnName.compareTo(o.getColumnName());
        } else {
            if(o.getColumnName() == null)
                return 0;
            else
                return 1;
        }

    }
}
