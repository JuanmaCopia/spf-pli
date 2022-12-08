/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lissa.heap;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.vm.Instruction;
import lissa.bytecode.GETFIELDHeapSolving;
import lissa.heap.solving.config.ConfigParser;
import lissa.heap.solving.techniques.SolvingStrategy;
import lissa.heap.solving.techniques.SolvingStrategyFactory;

public class HeapSolvingInstructionFactory extends SymbolicInstructionFactory {

    @Override
    public Instruction getfield(String fieldName, String clsName, String fieldDescriptor) {
        if (solvingStrategy.isLazyInitializationBased())
            return new GETFIELDHeapSolving(fieldName, clsName, fieldDescriptor);
        return super.getfield(fieldName, clsName, fieldDescriptor);
    }

    static ConfigParser configParser;

    static SolvingStrategy solvingStrategy;

    public static boolean doingHeapSolving = false;

    public HeapSolvingInstructionFactory(Config conf) {
        super(conf);
        doingHeapSolving = true;
        configParser = new ConfigParser(conf);
        solvingStrategy = SolvingStrategyFactory.makeSymbolicHeapSolvingTechnique(configParser);
    }

    public static SolvingStrategy getSolvingStrategy() {
        assert (solvingStrategy != null);
        return solvingStrategy;
    }

    public static ConfigParser getConfigParser() {
        return configParser;
    }

}
