package parser.querybuilder;

public class FieldHelper {

    /** null means I have none */
    public static SimpleField getSimpleField(String fieldName) {
        for (SimpleField f : SimpleField.values()) {
            if (f.name().equals(fieldName))
                return f;
        }
        return null;
    }

    /** null means I have none */
    public static ForeignField getForeignField(String fieldName) {
        for (ForeignField f : ForeignField.values()) {
            if (f.name().equals(fieldName))
                return f;
        }
        return null;
    }

}
