package parser.helper;

import parser.generated.CQLParser;

import java.io.ByteArrayInputStream;

public class CQLParserSingleton {

    private static CQLParser cqlParser = null;

    // Because of certain design choices iis important to obtain a reference to the Parser
    public static CQLParser getCqlParser(ByteArrayInputStream inputStream) {
        if (cqlParser == null) {
            cqlParser = new CQLParser(inputStream);
        }else {
            cqlParser.ReInit(inputStream);
        }

        return cqlParser;
    }

}
