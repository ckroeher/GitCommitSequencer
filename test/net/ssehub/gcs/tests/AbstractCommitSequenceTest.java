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

import java.io.File;

import net.ssehub.gcs.core.CommitSequence;

/**
 * This abstract class provides common attributes and methods used by the specific test classes in this package.
 * 
 * @author Christian Kroeher
 *
 */
public abstract class AbstractCommitSequenceTest {
    
    /**
     * The {@link TestSequenceStorage} for storing the created {@link CommitSequence}s during
     * {@link #setUp(String, String)}.
     */
    protected static TestSequenceStorage sequenceStorage;
    
    /**
     * Calls the {@link CommitSequence} with the given parameters and the {@link #sequenceStorage} of this class.
     * 
     * @param startCommit the {@link String} denoting the commit (SHA) to start the commit sequence(s) from; should
     *        never be <code>null</code> nor <i>blank</i> 
     */
    protected static void setUp(String startCommit) {
        File testRepositoryDirectory = AllTests.getTestRepository();
        if (testRepositoryDirectory != null 
                && testRepositoryDirectory.exists() 
                && testRepositoryDirectory.isDirectory()) {
            sequenceStorage = new TestSequenceStorage();
            CommitSequence commitSequence = new CommitSequence(testRepositoryDirectory, sequenceStorage);
            commitSequence.run(startCommit);
        } else {
            System.err.println("\"" + testRepositoryDirectory.getAbsolutePath() 
                    + "\" does not exist or is not a directory");
        }
    }

}
