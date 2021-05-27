package parser.querybuilder;

import javax.persistence.metamodel.SingularAttribute;

import java.util.*;

public class FetchReceipt {

    public FetchReceipt(SingularAttribute<?, ?> ... fetchList) {
        this.fetchList = new LinkedList<>(Arrays.asList(fetchList));
    }

    // As the different singular attributes have different types they are not generalizable in a list
    private final List<SingularAttribute<?, ?>> fetchList;

    public void emplaceFront(SingularAttribute<?, ?> initial) {
        this.fetchList.add(0, initial);
    }

    public List<SingularAttribute<?, ?>> getFetchList() {
        return fetchList;
    }

}
