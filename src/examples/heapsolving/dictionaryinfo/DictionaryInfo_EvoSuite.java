/**
 * DictionaryInfo.java 11:33:45 PM Apr 21, 2008
 *
 * <PRE>
 * Copyright (c) 2008, Jan Amoyo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 'AS IS';
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </PRE>
 */

package heapsolving.dictionaryinfo;

import java.util.HashSet;
import java.util.Set;

import heapsolving.treemap.TreeMap_EvoSuite;
import heapsolving.treemap.TreeMap_EvoSuite.Entry;

/**
 * Represents a FIX version specification. DictionaryInfo acts as a central
 * storage for all items defined in a version.
 *
 * @author jramoyo
 */
public class DictionaryInfo_EvoSuite {

    // private String version;

    // private int loadCount;

    // Default collection
//    private TreeMap<String, MessageInfo> messagesByName;
//
//    private TreeMap<Integer, MessageInfo> messagesById;

    // Default collection
    // =================== Modification =======================
    // private TreeMap<Integer, FieldInfo> fieldsByTagNumber;
    private TreeMap_EvoSuite fieldsByTagNumber;

    // private TreeMap<String, FieldInfo> fieldsByName;

    private TreeMap_EvoSuite fieldsByName;
    // ==========================================================

    // Default collection
//    private TreeMap<String, ComponentInfo> componentsByName;
//
//    private TreeMap<Integer, ComponentInfo> componentsById;

    /**
     * Creates a new DictionaryInfo
     *
     * @param version - a version
     */
//    public DictionaryInfo(String version) {
//        this.version = version;
//    }
    public DictionaryInfo_EvoSuite() {
    }

    /**
     * Returns the fields
     *
     * @return the fields
     */
    // =================== Modification =======================
//    public List<FieldInfo> getFields() {
//        if (fieldsByTagNumber != null) {
//            return new ArrayList<FieldInfo>(fieldsByTagNumber.values());
//        }
//        return null;
//    }
    // ==========================================================

    /**
     * Returns a field given a tagNumber
     *
     * @param tagNumber - a tagNumber
     * @return a field given a tagNumber
     */
    public FieldInfo_EvoSuite getField(int tagNumber) {
        if (fieldsByTagNumber != null) {
            return (FieldInfo_EvoSuite) fieldsByTagNumber.get(tagNumber);
        }
        return null;
    }

    /**
     * Returns a field given a name
     *
     * @param name - a name
     * @return a field given a name
     */
//    public FieldInfo getField(String name) {
//        if (fieldsByName != null) {
//            return (FieldInfo) fieldsByName.get(name);
//        }
//        return null;
//    }

    /**
     * Adds a field
     *
     * @param field - a field
     */
    public void addField(int tagNumber, int name) {
        FieldInfo_EvoSuite field = new FieldInfo_EvoSuite();
        field.setTagNumber(tagNumber);
        field.setName(name);

        if (fieldsByTagNumber == null) {
            fieldsByTagNumber = new TreeMap_EvoSuite();
            fieldsByName = new TreeMap_EvoSuite();
        }
        fieldsByTagNumber.put(field.getTagNumber(), field);
        fieldsByName.put(field.getName(), field);
    }

    /**
     * Adds a field
     *
     * @param field - a field
     */
    public void addField(FieldInfo_EvoSuite field) {
        if (fieldsByTagNumber == null) {
            fieldsByTagNumber = new TreeMap_EvoSuite();
            fieldsByName = new TreeMap_EvoSuite();
        }
        fieldsByTagNumber.put(field.getTagNumber(), field);
        fieldsByName.put(field.getName(), field);
    }

//    /**
//     * Returns the components
//     *
//     * @return the components
//     */
//    public List<ComponentInfo> getComponents() {
//        if (componentsByName != null) {
//            return new ArrayList<ComponentInfo>(componentsByName.values());
//        }
//        return null;
//    }
//
//    /**
//     * Returns a component given an id
//     *
//     * @param id - an id
//     * @return a component given an id
//     */
//    public ComponentInfo getComponent(int id) {
//        if (componentsById != null) {
//            return componentsById.get(id);
//        }
//        return null;
//    }
//
//    /**
//     * Returns a component given a name
//     *
//     * @param name - a name
//     * @return a component given a name
//     */
//    public ComponentInfo getComponent(String name) {
//        if (componentsByName != null) {
//            return componentsByName.get(name);
//        }
//        return null;
//    }
//
//    /**
//     * Adds a component
//     *
//     * @param component
//     */
//    public void addComponent(ComponentInfo component) {
//        if (componentsByName == null) {
//            componentsByName = new TreeMap<String, ComponentInfo>();
//            componentsById = new TreeMap<Integer, ComponentInfo>();
//        }
//        componentsByName.put(component.getName(), component);
//        componentsById.put(component.getId(), component);
//    }
//
//    /**
//     * Replace the field by a group. This is used by FPL parsers which cannot
//     * tell if a field is a group just from Fields.xml
//     *
//     * @param field - a field
//     * @param group - a group
//     */
//    public void replaceAsGroup(FieldInfo field, GroupInfo group) {
//        fieldsByTagNumber.put(field.getTagNumber(), group);
//        fieldsByName.put(field.getName(), group);
//    }
//
//    /**
//     * Returns a message given an id
//     *
//     * @param id - an id
//     * @return a message given an id
//     */
//    public MessageInfo getMessage(int id) {
//        if (messagesById != null) {
//            return messagesById.get(id);
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * Returns a message given a name
//     *
//     * @param name - a name
//     * @return a message given a name
//     */
//    public MessageInfo getMessage(String name) {
//        if (messagesByName != null) {
//            return messagesByName.get(name);
//        } else {
//            return null;
//        }
//    }
//
//    public List<MessageInfo> getMessages() {
//        return new ArrayList<MessageInfo>(messagesById.values());
//    }
//
//    /**
//     * Adds a message
//     *
//     * @param message - a message
//     */
//    public void addMessage(MessageInfo message) {
//        if (messagesByName == null) {
//            messagesByName = new TreeMap<String, MessageInfo>();
//            messagesById = new TreeMap<Integer, MessageInfo>();
//        }
//        messagesById.put(message.getId(), message);
//        messagesByName.put(message.getName(), message);
//    }

//    /**
//     * Returns the version
//     *
//     * @return the version
//     */
//    public String getVersion() {
//        return version;
//    }
//
//    /**
//     * Modifies the version
//     *
//     * @param version - the version to set
//     */
//    public void setVersion(String version) {
//        this.version = version;
//    }
//
//    /**
//     * Increments the loadCount
//     */
//    public void incrementLoadCount() {
//        loadCount++;
//    }
//
//    /**
//     * Returns whether the dictionary is loaded
//     *
//     * @return whether the dictionary is loaded
//     */
//    public boolean isLoaded() {
//        return loadCount == 5;
//    }

    public boolean repOKSymSolve() {
        if (fieldsByTagNumber == null || fieldsByName == null)
            return false;
        if (fieldsByTagNumber == fieldsByName)
            return false;

        Set<Entry> visited = new HashSet<>();
        if (!fieldsByTagNumber.isBinTreeWithParentReferences(visited))
            return false;
        if (!fieldsByName.isBinTreeWithParentReferences(visited))
            return false;
        if (!fieldsByTagNumber.isWellColored())
            return false;
        if (!fieldsByName.isWellColored())
            return false;
        return true;
    }

    public boolean repOKSymbolicExecution() {
        if (!fieldsByTagNumber.isSorted())
            return false;
        if (!fieldsByName.isSorted())
            return false;
        return true;
    }

    public boolean repOKComplete() {
        return repOKSymSolve() && repOKSymbolicExecution();
    }

}
