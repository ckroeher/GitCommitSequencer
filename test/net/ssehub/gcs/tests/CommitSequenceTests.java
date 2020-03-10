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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.gcs.core.CommitSequence;

/**
 * 
 * This class contains unit tests for the correct creation of {@link CommitSequence}s.
 * @author Christian Kroeher
 *
 */
public class CommitSequenceTests {
    
    /**
     * The {@link String} denoting the root directory of the repository from which commit sequences shall be created in
     * the tests defined in this class.
     */
    private static final String TEST_REPOSITORY_DIRECTORY_STRING = "./testdata";
    
    /**
     * The {@link String} denoting the commit (SHA) to start the commit sequence(s) from.
     */
    private static final String TEST_START_COMMIT = "1";

    /**
     * The {@link TestSequenceStorage} for storing the created {@link CommitSequence}s during the tests in this class.
     */
    private static TestSequenceStorage sequenceStorage;
    
    /**
     * Prepares the unit tests in this class by creating the commit sequences and storing them into the
     * {@link #sequenceStorage}.
     */
    @BeforeClass
    public static void setUp() {
        File testRepositoryDirectory = new File(TEST_REPOSITORY_DIRECTORY_STRING);
        if (testRepositoryDirectory.exists() && testRepositoryDirectory.isDirectory()) {
            sequenceStorage = new TestSequenceStorage();
            CommitSequence commitSequence = new CommitSequence(testRepositoryDirectory, sequenceStorage);
            commitSequence.run(TEST_START_COMMIT);
        } else {
            System.err.println("\"" + TEST_REPOSITORY_DIRECTORY_STRING + "\" does not exist or is not a directory");
        }
    }
    
    /**
     * Tests whether the number of created commit sequences is correct.
     */
    @Test
    public void testNumberOfCommitSequences() {
        int expectedNumberOfCommitSequences = 2;
        assertEquals(expectedNumberOfCommitSequences, sequenceStorage.getNumberOfCommitSequences(),
                "Number of created commit sequences is wrong");
    }
}
