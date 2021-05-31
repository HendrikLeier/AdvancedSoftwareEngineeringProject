package parser.querybuilder;

public class CQLFactory {

    public CQLFactory(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    private final ResourceManager resourceManager;

    public LogicSelector makeLogicSelector(int type) {
        if(type == 1) {
            return new FilterSelector(resourceManager);
        }else if (type == 2) {
            return new WithClauseSelector(resourceManager);
        }else throw new RuntimeException("Unknown LogicSelector Type Specified");
    }

    public Group makeGroup() {
        return new Group(resourceManager, makeLogicSelector(2));
    }

    public Order makeOrder() {
        return new Order(resourceManager);
    }

    public Result makeResult() {
        return new Result(resourceManager);
    }

    public ResultField makeResultField() {
        return new ResultField(resourceManager);
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}
