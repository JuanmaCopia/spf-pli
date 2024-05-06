package pli.heap.pathcondition;

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.string.StringComparator;
import gov.nasa.jpf.symbc.string.StringConstraint;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.StringPathCondition;

public class PathConditionUtils {

    public static PathCondition getConjunction(PathCondition pc1, PathCondition pc2) {
        PathCondition conjunction = pc1.make_copy();

        Constraint current = pc2.header;
        while (current != null) {
            Expression left = current.getLeft();
            Expression right = current.getRight();
            Comparator comp = current.getComparator();
            conjunction._addDet(comp, left, right);

            current = current.and;
        }

        // check conjunctions of string path conditions
        StringPathCondition spc1 = pc1.spc;
        StringPathCondition spc2 = pc2.spc;

        if (spc1 == null && spc2 == null)
            return conjunction;
        if (spc1 == null) {
            conjunction.spc = spc2.make_copy(conjunction);
            return conjunction;
        }
        if (spc2 == null) {
            conjunction.spc = spc1.make_copy(conjunction);
            return conjunction;
        }

        StringPathCondition spc_conjunction = spc1.make_copy(pc1);

        StringConstraint scurrent = spc2.header;
        while (scurrent != null) {
            StringExpression left = scurrent.getLeft();
            StringExpression right = scurrent.getRight();
            StringComparator comp = scurrent.getComparator();
            spc_conjunction._addDet(comp, left, right);

            scurrent = scurrent.and();
        }

        conjunction.spc = spc_conjunction.make_copy(conjunction);
        return conjunction;
    }

    public static boolean isConjunctionSAT(PathCondition pc1, PathCondition pc2) {
        assert (pc1 != null && pc2 != null);

        PathCondition conjunction = pc1.make_copy();

        Constraint current = pc2.header;
        while (current != null) {
            Expression left = current.getLeft();
            Expression right = current.getRight();
            Comparator comp = current.getComparator();
            conjunction._addDet(comp, left, right);

            current = current.and;
        }

        if (!conjunction.simplify())
            return false;

        // check conjunctions of string path conditions
        StringPathCondition spc1 = pc1.spc;
        StringPathCondition spc2 = pc2.spc;

        if (spc1 == null && spc2 == null)
            return true;
        if (spc1 == null)
            return spc2.simplify();
        if (spc2 == null)
            return spc1.simplify();

        StringPathCondition spc_conjunction = spc1.make_copy(pc1);

        StringConstraint scurrent = spc2.header;
        while (scurrent != null) {
            StringExpression left = scurrent.getLeft();
            StringExpression right = scurrent.getRight();
            StringComparator comp = scurrent.getComparator();
            spc_conjunction._addDet(comp, left, right);

            scurrent = scurrent.and();
        }

        return spc_conjunction.simplify();
    }

}
