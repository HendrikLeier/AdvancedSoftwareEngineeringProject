package parser.querybuilder;

public class FieldHelper {

    /** null means I have none */
    public static DataModelField getDataModelField(String fieldName) {
        for (DataModelField f : DataModelField.values()) {
            if (f.name().equals(fieldName))
                return f;
        }
        return null;
    }


}
