package unittest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import parser.querybuilder.Order;
import parser.querybuilder.OrderType;
import parser.querybuilder.ResourceManager;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.*;

public class TestOrder {

    private ResourceManager resourceManager;

    private CriteriaBuilder criteriaBuilder;

    private CriteriaQuery criteriaQuery;

    private Order order;

    private javax.persistence.criteria.Order criteriaOrderAsc;
    private javax.persistence.criteria.Order criteriaOrderDesc;

    @BeforeEach
    public void prepareOrderTest() {
        this.resourceManager = mock(ResourceManager.class);

        this.criteriaBuilder = mock(CriteriaBuilder.class);

        this.criteriaOrderAsc = mock(javax.persistence.criteria.Order.class);
        this.criteriaOrderDesc = mock(javax.persistence.criteria.Order.class);

        when(this.criteriaOrderAsc.isAscending()).thenReturn(true);
        when(this.criteriaOrderDesc.isAscending()).thenReturn(false);

        this.criteriaQuery = mock(CriteriaQuery.class);

        when(this.criteriaBuilder.asc(any(Expression.class))).thenReturn(this.criteriaOrderAsc);
        when(this.criteriaBuilder.desc(any(Expression.class))).thenReturn(this.criteriaOrderDesc);

        when(this.resourceManager.getCriteriaBuilder()).thenReturn(this.criteriaBuilder);
        when(this.resourceManager.getCriteriaQuery()).thenReturn(this.criteriaQuery);

        this.order = new Order(this.resourceManager);
    }

    private List<Expression<?>> generateExpressions(int numberOfExpressions) {
        List<Expression<?>> expressions = new LinkedList<>();
        for(int i = 0; i < numberOfExpressions; i++) {
            expressions.add(mock(Expression.class));
        }

        return expressions;
    }

    private List<OrderType> generateOrderTypeList(int numberOfOrderTypes, OrderType orderType) {
        List<OrderType> orderTypes = new LinkedList<>();
        for(int i = 0; i < numberOfOrderTypes; i++){
            orderTypes.add(orderType);
        }
        return orderTypes;
    }

    private Field getJailbrokenFields() throws NoSuchFieldException {
        Field fields = Order.class.getDeclaredField("fields");
        fields.setAccessible(true);
        return fields;
    }

    private Field getJailbrokenorderTypes() throws NoSuchFieldException {
        Field orderTypes = Order.class.getDeclaredField("orderTypes");
        orderTypes.setAccessible(true);
        return orderTypes;
    }

    @Test
    void testFinalizeOrderSameNumberAsc() throws NoSuchFieldException, IllegalAccessException {
        final ArgumentCaptor<List<javax.persistence.criteria.Order>> captor = ArgumentCaptor.forClass(List.class);
        // Jailbreak stuff...
        Field fields = getJailbrokenFields();
        Field orderTypes = getJailbrokenorderTypes();

        List<Expression<?>> expressions = generateExpressions(20);
        List<OrderType> orderTypesList = generateOrderTypeList(20, OrderType.ASCENDING);

        fields.set(order, expressions);
        orderTypes.set(order, orderTypesList);

        order.finalizeOrder();

        verify(criteriaQuery, times(1)).orderBy(captor.capture());

        List<javax.persistence.criteria.Order> orders = captor.getValue();

        assert orders.size() == 20;
        assert orders.stream().allMatch(javax.persistence.criteria.Order::isAscending);
    }

    @Test
    void testFinalizeOrderSameNumberDesc() throws NoSuchFieldException, IllegalAccessException {
        final ArgumentCaptor<List<javax.persistence.criteria.Order>> captor = ArgumentCaptor.forClass(List.class);
        // Jailbreak stuff...
        Field fields = getJailbrokenFields();
        Field orderTypes = getJailbrokenorderTypes();

        List<Expression<?>> expressions = generateExpressions(20);
        List<OrderType> orderTypesList = generateOrderTypeList(20, OrderType.DESCENDING);

        fields.set(order, expressions);
        orderTypes.set(order, orderTypesList);

        order.finalizeOrder();

        verify(criteriaQuery, times(1)).orderBy(captor.capture());

        List<javax.persistence.criteria.Order> orders = captor.getValue();

        assert orders.size() == 20;
        assert orders.stream().noneMatch(javax.persistence.criteria.Order::isAscending);
    }

    @Test
    void testFinalizeOrderMoreExpressions() throws NoSuchFieldException, IllegalAccessException {
        final ArgumentCaptor<List<javax.persistence.criteria.Order>> captor = ArgumentCaptor.forClass(List.class);
        // Jailbreak stuff...
        Field fields = getJailbrokenFields();
        Field orderTypes = getJailbrokenorderTypes();

        List<Expression<?>> expressions = generateExpressions(25);
        List<OrderType> orderTypesList = generateOrderTypeList(20, OrderType.DESCENDING);

        fields.set(order, expressions);
        orderTypes.set(order, orderTypesList);

        assertThrows(IndexOutOfBoundsException.class, () ->{
            order.finalizeOrder();
        });
    }

    @Test
    void testFinalizeOrderMoreorderTypes() throws NoSuchFieldException, IllegalAccessException {
        final ArgumentCaptor<List<javax.persistence.criteria.Order>> captor = ArgumentCaptor.forClass(List.class);
        // Jailbreak stuff...
        Field fields = getJailbrokenFields();
        Field orderTypes = getJailbrokenorderTypes();

        List<Expression<?>> expressions = generateExpressions(20);
        List<OrderType> orderTypesList = generateOrderTypeList(25, OrderType.ASCENDING);

        fields.set(order, expressions);
        orderTypes.set(order, orderTypesList);

        order.finalizeOrder();

        verify(criteriaQuery, times(1)).orderBy(captor.capture());

        List<javax.persistence.criteria.Order> orders = captor.getValue();

        assert orders.size() == 20;
        assert orders.stream().allMatch(javax.persistence.criteria.Order::isAscending);
    }


}
