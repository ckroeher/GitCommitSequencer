/*
 * Copyright 2020 University of Hildesheim, Software Systems Engineering
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.ssehub.gcs.tests;

import java.util.ArrayList;
import java.util.List;

import net.ssehub.gcs.core.CommitSequence;
import net.ssehub.gcs.core.ISequenceStorage;

/**
 * 
 * This class implements the {@link ISequenceStorage} for storing the created {@link CommitSequence}s during unit tests.
 * 
 * @author Christian Kroeher
 *
 */
public class TestSequenceStorage implements ISequenceStorage {
    
    /**
     * The {@link List} of {@link CommitSequence}s created during a particular (set of) unit test(s).
     */
    private List<CommitSequence> commitSequenceList;
    
    /**
     * Constructs a new {@link TestSequenceStorage} instance.
     */
    protected TestSequenceStorage() {
        commitSequenceList = new ArrayList<CommitSequence>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void add(CommitSequence commitSequence) {
        commitSequenceList.add(commitSequence);
    }

    /**
     * Returns the number of commit sequences in this storage.
     * @return the number of commit sequences in this storage; at least <i>0</i>
     */
    public int getNumberOfCommitSequences() {
        return commitSequenceList.size();
    }
}
