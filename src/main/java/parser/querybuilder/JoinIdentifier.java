package parser.querybuilder;

import javax.persistence.metamodel.SingularAttribute;

public class JoinIdentifier {

    private final String entityAName;
    private final String entityBName;

    private JoinIdentifier(String entityAName, String entityBName) {
        this.entityAName = entityAName;
        this.entityBName = entityBName;
    }

    public static <X, T> JoinIdentifier fromSingularAttribute(SingularAttribute<X, T> singularAttribute) {
        return new JoinIdentifier(singularAttribute.getDeclaringType().getClass().getSimpleName(),
                                  singularAttribute.getType().getClass().getSimpleName());
    }

    //Idea from effective Java : Item 9
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + entityBName.hashCode();
        result = 31 * result + entityAName.hashCode();
        return result;
    }
}
