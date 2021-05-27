package parser.querybuilder;

import javax.persistence.metamodel.SingularAttribute;

import java.util.*;

/**
 * This class stores Lists that describe how data is fetched
 */
public class FetchReceipt implements Comparable<FetchReceipt> {

    public FetchReceipt(SingularAttribute<?, ?> ... fetchList) {
        /*
          List returned from Arrays.asList do not support modifying operations
          The emplaceFront method needs modifying operations thus this is necessary
         */
        this.fetchList = new LinkedList<>(Arrays.asList(fetchList));
    }

    /**
     * As the different singular attributes have different types they are not generalizable in a list
     */
    private final List<SingularAttribute<?, ?>> fetchList;

    /**
     * Adds an entry at the front
     * @param initial the entry to add
     */
    public void emplaceFront(SingularAttribute<?, ?> initial) {
        this.fetchList.add(0, initial);
    }

    public List<SingularAttribute<?, ?>> getFetchList() {
        return fetchList;
    }

    @Override
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();
        for (SingularAttribute<?,?> singularAttribute : fetchList) {
            stringBuilder.append(singularAttribute.getName());
            stringBuilder.append(singularAttribute.getJavaType().getName());
        }
        return stringBuilder.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FetchReceipt) {
            return this.hashCode() == obj.hashCode();
        }else {
            return super.equals(obj);
        }
    }

    @Override
    public int compareTo(FetchReceipt o) {
        if(o == null)
            return 1;
        return Integer.compare(this.hashCode(), o.hashCode());
    }
}
