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

import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.gcs.core.CommitSequence;

/**
 * 
 * This class contains unit tests for the correct creation of {@link CommitSequence}s.
 * @author Christian Kroeher
 *
 */
public class CommitSequenceTests extends AbstractCommitSequenceTest {
    
    /**
     * The {@link String} denoting the commit (SHA) to start the commit sequence(s) from.
     */
    private static final String TEST_START_COMMIT = "b6d0c1b366770dee3c28ef8a01169992d85680e2";

    /**
     * Prepares the unit tests in this class by calling {@link AbstractCommitSequenceTest#setUp(String)} passing
     * the {@link #TEST_START_COMMIT}.
     */
    @BeforeClass
    public static void setUp() {
        setUp(TEST_START_COMMIT);
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
