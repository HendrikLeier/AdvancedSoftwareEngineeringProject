package parser.querybuilder;


import javax.persistence.criteria.Expression;
import javax.persistence.metamodel.SingularAttribute;
import java.util.*;

public class Order {

    private final ResourceManager resourceManager;

    private final List<Expression<?>> fields;
    private final List<OrderType> orderTypes;

    public Order(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.fields = new LinkedList<>();
        this.orderTypes = new LinkedList<>();
    }

    public void finalizeOrder() {
        List<javax.persistence.criteria.Order> orders = new LinkedList<>();
        for (int i = 0; i < fields.size(); i++) {
            switch (orderTypes.get(i)) {
                case ASCENDING: orders.add(resourceManager.getCriteriaBuilder().asc(fields.get(i))); break;
                case DESCENDING: orders.add(resourceManager.getCriteriaBuilder().desc(fields.get(i))); break;
            }
        }

        resourceManager.getCriteriaQuery().orderBy(orders);
    }

    public void addOrder(String fieldName, OrderType orderType) throws FieldException {
        Expression<?> field = resourceManager.getReferencedField(fieldName);
        fields.add(field);
        orderTypes.add(orderType);
    }




}
