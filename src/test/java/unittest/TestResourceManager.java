package unittest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import parser.querybuilder.DataModelField;
import parser.querybuilder.FetchReceipt;
import parser.querybuilder.ResourceManager;
import persisted.Event;
import persisted.Event_;
import persisted.RecurrentEvent_;
import persisted.RecurrentRule_;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TestResourceManager {


    private ResourceManager resourceManager;

    private Join<?, ?> mockJoin;

    private Map<FetchReceipt, ResourceManager.FetchResolveResult> sourceMap;

    @BeforeEach
    public void prepareResourceManagerTest(){
        Root<Event> eventRoot = (Root<Event>) mock(Root.class);
        mockJoin = mock(Join.class);

        when(eventRoot.join(any(SingularAttribute.class), any())).thenReturn(mockJoin);
        when(mockJoin.join(any(SingularAttribute.class), eq(JoinType.LEFT))).thenReturn(mockJoin);

        CriteriaQuery<Tuple> criteriaQuery = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        // it is important to do this before each test as the cache is a state that has to be reset
        resourceManager = new ResourceManager(eventRoot, criteriaQuery, criteriaBuilder);
        sourceMap = mock(Map.class);

        resourceManager.setSourceCache(sourceMap);

    }

    private FetchReceipt generateFetchReceipt(int numOfJoins) {
        SingularAttribute<?,?>[] attributes = new SingularAttribute[numOfJoins + 1];

        for (int i = 0; i < numOfJoins + 1; i++) {
            attributes[i] = mock(SingularAttribute.class);
        }
        return new FetchReceipt(attributes);
    }

    @Test
    public void testResolveReceipt() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        // Jailbreak stuff...
        Method method = ResourceManager.class.getDeclaredMethod("resolveReceipt", FetchReceipt.class);
        method.setAccessible(true);

        FetchReceipt fetchReceipt = generateFetchReceipt(20);

        ResourceManager.FetchResolveResult result = (ResourceManager.FetchResolveResult) method.invoke(resourceManager, fetchReceipt);

        assert result.getResultBundle() == mockJoin;
        assert result.getSpecificAttribute() == fetchReceipt.getFetchList().get(fetchReceipt.getFetchList().size() - 1);
        verify(sourceMap, times(1)).put(any(), any());
    }

}
