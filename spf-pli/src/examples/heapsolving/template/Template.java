/*
 * Copyright(C) 2008-2009 Dmitriy Kumshayev. <dq@mail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package heapsolving.template;

import korat.finitization.IFinitization;
import korat.finitization.IObjSet;
import korat.finitization.impl.FinitizationFactory;
import pli.SymHeap;

//import java.util.List;
//import java.util.Map;

//import org.apache.log4j.Logger;
//import org.apache.poi.hssf.usermodel.HSSFCell;
//import org.apache.poi.hssf.usermodel.HSSFSheet;

public class Template {
//	private static final Logger logger = Logger.getLogger(Template.class);
    // protected final String name;
//	protected final HSSFSheet sheet;
    public LinkedList parameters = new LinkedList();
    public HashMap parametersByName = new HashMap();
//	private final Map<Integer,Map<Integer,Parameter>> paramsByRowCol = new HashMap<Integer,Map<Integer,Parameter>>();

//    public Template(String name) {
//        this.name = name;
//    }

    public Template() {
    }

//	public Template(String name,HSSFSheet sheet)
//	{
//		this.name = name;
//		this.sheet = sheet;
//	}

//    public String getName() {
//        return name;
//    }

    public Parameter getParameter(Integer name) {
        return (Parameter) parametersByName.get(name);
    }

//    public void setParameters(Parameter[] parameters) {
//        for (Parameter p : parameters) {
//            addParameter(p);
//        }
//    }

    public int getParametersNumber() {
        return parameters.size();
    }

//	protected void createParameter(String paramName, int r, int c)
//	{
//		boolean isNumber = Character.isDigit(paramName.charAt(0));
//
//		Parameter param = (isNumber ? this
//				.getParameter(Integer.parseInt(paramName)) : this
//				.getParameter(paramName));
//
//		if (param != null)
//		{
//			param.setColumn(c);
//			param.setRow(r);
//		}
//		else if (isNumber)
//		{
//			param = new Parameter();
//			param.setColumn(c);
//			param.setRow(r);
//			param.setIndex(Integer.parseInt(paramName));
//			param.setName("#" + paramName);
//			this.addParameter(param);
//		}
//		else
//		{
//			logger.warn("Missing parameter: " + paramName + "@(" + r + "," + c + ")");
//		}
//
//		if (param != null)
//		{
//			int relRow = param.getRow();
//			int relCol = param.getColumn();
//			Map<Integer, Parameter> paramsByCol = paramsByRowCol.get(relRow);
//			if (paramsByCol == null)
//			{
//				paramsByCol = new HashMap<Integer, Parameter>();
//				paramsByRowCol.put(relRow, paramsByCol);
//			}
//			paramsByCol.put(relCol, param);
//
//		}
//	}

//	public Parameter getParameter(int r, int c)
//	{
//		Map<Integer,Parameter> paramsByCol = paramsByRowCol.get(r);
//		if( paramsByCol!=null )
//		{
//			return paramsByCol.get(c);
//		}
//		return null;
//	}

    public void addParameter(int pname, int index, int row, int column) {
        Parameter parameter = new Parameter();
        parameter.setName(pname);
        parameter.setIndex(index);
        parameter.setRow(row);
        parameter.setColumn(column);

        parameters.add(parameter);
        int name = parameter.getName();
        // Parameter name is optional
        if (!parametersByName.containsKey(name)) {
            parametersByName.put(name, parameter);
        } else {
            // logger.warn("Duplicate parameter: "+name);
        }
    }
//
//    public void addParameter(Parameter parameter) {
//        parameters.add(parameter);
//        Integer name = parameter.getName();
//        // Parameter name is optional
//        if (name != null) {
//            if (!parametersByName.containsKey(name)) {
//                parametersByName.put(name, parameter);
//            } else {
//                // logger.warn("Duplicate parameter: "+name);
//            }
//        }
//    }

//    public Parameter getParameter(int idx) {
//        return idx >= 1 && idx <= parameters.size() ? (Parameter) parameters.get(idx - 1) : null;
//    }

//	public abstract int height();
//
//	public abstract int width();

    // public abstract Reference absoluteReference(int r, int c);

//	public abstract int getRowHeight(int r);

    // public abstract HSSFCell getCell(int r, int c);

    public boolean isRowBroken(int r) {
        return false;
    }

//	public int getParameterIndex(int r, int c)
//	{
//		int idx = -1;
//		Parameter p  = getParameter(r,c);
//		if( p!= null)
//		{
//			idx = p.getIndex();
//		}
//		return idx;
//	}

    public boolean repOKSymSolve() {
        if (parameters == null || parametersByName == null)
            return false;
        if (!parameters.repOKSymSolve())
            return false;
        if (!parametersByName.repOKSymSolve())
            return false;
        return true;
    }

    public boolean repOKSymbolicExecution() {
        if (!parameters.repOKSymbolicExecution())
            return false;
        if (!parametersByName.repOKSymbolicExecution())
            return false;
        return true;
    }

    public boolean repOKComplete() {
        return repOKSymSolve() && repOKSymbolicExecution();
    }

    public static void runRepOK() {
        Template toBuild = new Template();
        SymHeap.buildSolutionHeap(toBuild);
        SymHeap.handleRepOKResult(toBuild, toBuild.repOKSymbolicExecution());
    }

    public static void runRepOKComplete() {
        Template toBuild = new Template();
        SymHeap.buildPartialHeapInput(toBuild);
        SymHeap.handleRepOKResult(toBuild, toBuild.repOKComplete());
    }

    public static IFinitization finTemplate(int nodesNum) {
        IFinitization f = FinitizationFactory.create(Template.class);

        IObjSet ll = f.createObjSet(LinkedList.class, 1, true);
        f.set(Template.class, "parameters", ll);

        IObjSet hmap = f.createObjSet(HashMap.class, 1, true);
        f.set(Template.class, "parametersByName", hmap);

        IObjSet nodes = f.createObjSet(LinkedList.Entry.class, nodesNum, true);
        f.set(LinkedList.class, "header", nodes);
        f.set(LinkedList.class, "size", f.createIntSet(0, nodesNum));
        f.set(LinkedList.Entry.class, "element", f.createObjSet(Parameter.class, nodesNum, true));
        f.set(LinkedList.Entry.class, "next", nodes);
        f.set(LinkedList.Entry.class, "previous", nodes);

        f.set(HashMap.class, "size", f.createIntSet(0, nodesNum));
        IObjSet entries = f.createObjSet(HashMap.Entry.class, nodesNum, true);
        f.set(HashMap.class, "e0", entries);
        f.set(HashMap.class, "e1", entries);
        f.set(HashMap.class, "e2", entries);
        f.set(HashMap.class, "e3", entries);
        f.set(HashMap.class, "e4", entries);
        f.set(HashMap.class, "e5", entries);
        f.set(HashMap.class, "e6", entries);
        f.set(HashMap.class, "e7", entries);
        f.set(HashMap.class, "e8", entries);
        f.set(HashMap.class, "e9", entries);
        f.set(HashMap.class, "e10", entries);
        f.set(HashMap.class, "e11", entries);
        f.set(HashMap.class, "e12", entries);
        f.set(HashMap.class, "e13", entries);
        f.set(HashMap.class, "e14", entries);
        f.set(HashMap.class, "e15", entries);

        f.set(HashMap.Entry.class, "key", f.createIntSet(0, nodesNum * HashMap.DEFAULT_INITIAL_CAPACITY));
        f.set(HashMap.Entry.class, "value", f.createObjSet(Parameter.class, nodesNum, true));
        f.set(HashMap.Entry.class, "hash", f.createIntSet(0, nodesNum * HashMap.DEFAULT_INITIAL_CAPACITY));
        f.set(HashMap.Entry.class, "next", entries);

        return f;
    }

}