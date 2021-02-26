package parser.helper;

import java.util.*;

public class Utils {

    public static void printList(List list) {
        list.forEach(System.out::println);
    }

    public static Expr handleExprList(List exprList, String val) {
        Expr result = new Expr();
        Expr currExpr = result;
        if (exprList.size() > 1) {
            for(int i = 0; i < exprList.size() - 1 ; i++) {
                currExpr.right = (Expr) exprList.get(i);
                currExpr.val = val;
                if (i != exprList.size() - 2) {
                    currExpr.left = new Expr();
                    currExpr = currExpr.left;
                }
            }
            currExpr.left = (Expr) exprList.get(exprList.size() - 1);

            return result;
        } else {
            return (Expr) exprList.get(0);
        }
    }

}
