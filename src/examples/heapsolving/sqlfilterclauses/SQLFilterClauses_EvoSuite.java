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

/**
 * This class contains the information used to build the Where and Order By
 * clauses used by the database query triggered by selecting the Contents tab in
 * the Session internal frame.
 *
 */
public class SQLFilterClauses_EvoSuite implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The container for the SQL filter information */
    private HashMapHMap_EvoSuite _sqlClauseInformation;

    /**
     * Creates a new instance of SQLFilterClauses.
     */
    public SQLFilterClauses_EvoSuite() {
        _sqlClauseInformation = new HashMapHMap_EvoSuite();
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
        HashMapStr_EvoSuite filterData = _sqlClauseInformation.get(tableName);
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
        HashMapStr_EvoSuite filterData = _sqlClauseInformation.get(tableName);
        if (filterData == null) {
            filterData = new HashMapStr_EvoSuite();
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

}
