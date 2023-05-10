package heapsolving.sqlfilterclauses;

/*
 * Copyright (C) 2003-2004 Maury Hammel
 * mjhammel@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.io.Serializable;

import korat.finitization.IFinitization;
import korat.finitization.IObjSet;
import korat.finitization.impl.FinitizationFactory;
import lissa.SymHeap;

/**
 * This class contains the information used to build the Where and Order By
 * clauses used by the database query triggered by selecting the Contents tab in
 * the Session internal frame.
 *
 */
public class SQLFilterClauses implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The container for the SQL filter information */
    HashMapHMap _sqlClauseInformation;

    /**
     * Creates a new instance of SQLFilterClauses.
     */
    public SQLFilterClauses() {
        _sqlClauseInformation = new HashMapHMap();
    }

    /**
     * Return the value of the SQL filter information
     *
     * @param clauseName The name of the clause for which the information is
     *                   required.
     * @param tableName  The database table name for which the information is
     *                   required.
     *
     * @return A string value containing the requested information
     */
    public String get(int clauseName, int tableName) {
        HashMapStr filterData = _sqlClauseInformation.get(tableName);
        return (filterData == null) ? null : filterData.get(clauseName);
    }

    /**
     * Update (or create) SQL Filter information for a session.
     *
     * @param clauseName        The name of the clause for which the information
     *                          pertains.
     * @param tableName         The name of the database table for the filter
     *                          information.
     *
     * @param clauseInformation The SQL filter information to be saved.
     */
    public void put(int clauseName, int tableName, String clauseInformation) {
        HashMapStr filterData = _sqlClauseInformation.get(tableName);
        if (filterData == null) {
            filterData = new HashMapStr();
        }
        filterData.put(clauseName, clauseInformation);
        _sqlClauseInformation.put(tableName, filterData);
    }

    public boolean repOKSymSolve() {
        if (_sqlClauseInformation == null)
            return false;
        if (!_sqlClauseInformation.repOKSymSolve())
            return false;
        return true;
    }

    public boolean repOKSymbolicExecution() {
        if (!_sqlClauseInformation.repOKSymbolicExecution())
            return false;

        return true;
    }

    public boolean repOKComplete() {
        return repOKSymSolve() && repOKSymbolicExecution();
    }

    public static void runRepOK() {
        SQLFilterClauses toBuild = new SQLFilterClauses();
        SymHeap.buildSolutionHeap(toBuild);
        SymHeap.handleRepOKResult(toBuild, toBuild.repOKSymbolicExecution());
    }

    public static void runRepOKComplete() {
        SQLFilterClauses toBuild = new SQLFilterClauses();
        SymHeap.buildPartialHeapInput(toBuild);
        SymHeap.handleRepOKResult(toBuild, toBuild.repOKComplete());
    }

    public static IFinitization finSQLFilterClauses(int nodesNum) {
        IFinitization f = FinitizationFactory.create(SQLFilterClauses.class);

        IObjSet hashmap = f.createObjSet(HashMapHMap.class, 1, true);
        f.set(SQLFilterClauses.class, "_sqlClauseInformation", hashmap);

        IObjSet entries = f.createObjSet(HashMapHMap.Entry.class, nodesNum, true);
        f.set(HashMapHMap.class, "size", f.createIntSet(0, nodesNum));
        f.set(HashMapHMap.class, "e0", entries);
        f.set(HashMapHMap.class, "e1", entries);
        f.set(HashMapHMap.class, "e2", entries);
        f.set(HashMapHMap.class, "e3", entries);
        f.set(HashMapHMap.class, "e4", entries);
        f.set(HashMapHMap.class, "e5", entries);
        f.set(HashMapHMap.class, "e6", entries);
        f.set(HashMapHMap.class, "e7", entries);

        IObjSet subhashmaps = f.createObjSet(HashMapStr.class, nodesNum, true);
        f.set(HashMapHMap.Entry.class, "key", f.createIntSet(0, nodesNum * HashMapHMap.DEFAULT_INITIAL_CAPACITY));
        f.set(HashMapHMap.Entry.class, "value", subhashmaps);
        f.set(HashMapHMap.Entry.class, "hash", f.createIntSet(0, nodesNum * HashMapHMap.DEFAULT_INITIAL_CAPACITY));
        f.set(HashMapHMap.Entry.class, "next", entries);

        IObjSet entries2 = f.createObjSet(HashMapStr.Entry.class, nodesNum, true);
        f.set(HashMapStr.class, "size", f.createIntSet(0, nodesNum));
        f.set(HashMapStr.class, "e0", entries2);
        f.set(HashMapStr.class, "e1", entries2);
        f.set(HashMapStr.class, "e2", entries2);
        f.set(HashMapStr.class, "e3", entries2);
        f.set(HashMapStr.class, "e4", entries2);
        f.set(HashMapStr.class, "e5", entries2);
        f.set(HashMapStr.class, "e6", entries2);
        f.set(HashMapStr.class, "e7", entries2);

        f.set(HashMapStr.Entry.class, "key", f.createIntSet(0, nodesNum * HashMapStr.DEFAULT_INITIAL_CAPACITY));
        f.set(HashMapStr.Entry.class, "value", f.createRandomStringSet(nodesNum, 1, 2));
        f.set(HashMapStr.Entry.class, "hash", f.createIntSet(0, nodesNum * HashMapStr.DEFAULT_INITIAL_CAPACITY));
        f.set(HashMapStr.Entry.class, "next", entries2);
        return f;
    }

}
