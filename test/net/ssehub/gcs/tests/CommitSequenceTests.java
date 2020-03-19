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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.gcs.core.CommitSequence;

/**
 * 
 * This class contains unit tests for the correct creation of {@link CommitSequence}s.
 * 
 * @author Christian Kroeher
 *
 */
public class CommitSequenceTests extends AbstractCommitSequenceTest {
    
    /**
     * The {@link String} denoting the commit (SHA) to start the commit sequence(s) from.
     */
    private static final String TEST_START_COMMIT = "b6d0c1b366770dee3c28ef8a01169992d85680e2";
    
    /**
     * The expected number of commit sequences in the{@link TestSequenceStorage} after {@link #setUp()}.
     */
    private static final int EXPECTED_NUMBER_OF_COMMIT_SEQUENCES = 18;

    /**
     * Prepares the unit tests in this class by calling {@link AbstractCommitSequenceTest#setUp(String)} passing
     * the {@link #TEST_START_COMMIT}.
     */
    @BeforeClass
    public static void setUp() {
        System.out.println("++++ Commit Sequence Tests ++++");
        setUp(TEST_START_COMMIT);
        System.out.println("Executing commit sequence tests");
    }
    
    /**
     * Prints the final messages to the console.
     */
    @AfterClass
    public static void tearDown() {
        System.out.println("Execution successful");
        System.out.println("++++ Commit Sequence Tests ++++");
    }
    
    /**
     * Tests whether the number of created commit sequences is equal to {@link #EXPECTED_NUMBER_OF_COMMIT_SEQUENCES}.
     */
    @Test
    public void testNumberOfCommitSequences() {
        assertEquals(EXPECTED_NUMBER_OF_COMMIT_SEQUENCES, sequenceStorage.getNumberOfCommitSequences(),
                "Number of created commit sequences is wrong");
    }
    
    /**
     * Tests whether each created commit sequence has its unique commit sequence number.
     */
    @Test
    public void testUniqueCommitSequenceNumbers() {
        int numberOfCommitSequences = sequenceStorage.getNumberOfCommitSequences();
        CommitSequence currentCommitSequence;
        CommitSequence commitSequenceToCompare;
        for (int i = 0; i < numberOfCommitSequences; i++) {
            currentCommitSequence = sequenceStorage.getCommitSequence(i);
            for (int j = 0; j < numberOfCommitSequences; j++) {
                commitSequenceToCompare = sequenceStorage.getCommitSequence(j);
                if (i != j) {
                    assertNotEquals(currentCommitSequence.getSequenceNumber(),
                            commitSequenceToCompare.getSequenceNumber(), "Commit sequence number " 
                                    + currentCommitSequence.getSequenceNumber() + " at index " + i
                                    + " equals commit sequence number " + commitSequenceToCompare.getSequenceNumber()
                                    + " at index " + j);
                }
            }
        }
    }
    
    /**
     * Tests whether each created commit sequence is unique (no duplicated commit sequences).
     */
    @Test
    public void testUniqueCommitSequences() {
        int numberOfCommitSequences = sequenceStorage.getNumberOfCommitSequences();
        CommitSequence currentCommitSequence;
        CommitSequence commitSequenceToCompare;
        for (int i = 0; i < numberOfCommitSequences; i++) {
            currentCommitSequence = sequenceStorage.getCommitSequence(i);
            for (int j = 0; j < numberOfCommitSequences; j++) {
                commitSequenceToCompare = sequenceStorage.getCommitSequence(j);
                if (i != j) {
                    assertFalse(equals(currentCommitSequence, commitSequenceToCompare), "Commit sequence " 
                            + currentCommitSequence.getSequenceNumber() + " at index " + i + " equals commit sequence " 
                            + commitSequenceToCompare.getSequenceNumber() + " at index " + j);
                }
            }
        }
    }
    
    /**
     * Tests whether commit sequence 1 is correct.
     */
    @Test
    public void testCommitSequence1() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_1;
        CommitSequence actualCommitSequence = getCommitSequence(1);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 2 is correct.
     */
    @Test
    public void testCommitSequence2() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_2;
        CommitSequence actualCommitSequence = getCommitSequence(2);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 3 is correct.
     */
    @Test
    public void testCommitSequence3() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_3;
        CommitSequence actualCommitSequence = getCommitSequence(3);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 4 is correct.
     */
    @Test
    public void testCommitSequence4() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_4;
        CommitSequence actualCommitSequence = getCommitSequence(4);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 5 is correct.
     */
    @Test
    public void testCommitSequence5() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_5;
        CommitSequence actualCommitSequence = getCommitSequence(5);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 6 is correct.
     */
    @Test
    public void testCommitSequence6() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_6;
        CommitSequence actualCommitSequence = getCommitSequence(6);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 7 is correct.
     */
    @Test
    public void testCommitSequence7() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_7;
        CommitSequence actualCommitSequence = getCommitSequence(7);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 8 is correct.
     */
    @Test
    public void testCommitSequence8() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_8;
        CommitSequence actualCommitSequence = getCommitSequence(8);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 9 is correct.
     */
    @Test
    public void testCommitSequence9() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_9;
        CommitSequence actualCommitSequence = getCommitSequence(9);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 10 is correct.
     */
    @Test
    public void testCommitSequence10() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_10;
        CommitSequence actualCommitSequence = getCommitSequence(10);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 11 is correct.
     */
    @Test
    public void testCommitSequence11() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_11;
        CommitSequence actualCommitSequence = getCommitSequence(11);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 12 is correct.
     */
    @Test
    public void testCommitSequence12() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_12;
        CommitSequence actualCommitSequence = getCommitSequence(12);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 13 is correct.
     */
    @Test
    public void testCommitSequence13() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_13;
        CommitSequence actualCommitSequence = getCommitSequence(13);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 14 is correct.
     */
    @Test
    public void testCommitSequence14() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_14;
        CommitSequence actualCommitSequence = getCommitSequence(14);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 15 is correct.
     */
    @Test
    public void testCommitSequence15() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_15;
        CommitSequence actualCommitSequence = getCommitSequence(15);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 16 is correct.
     */
    @Test
    public void testCommitSequence16() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_16;
        CommitSequence actualCommitSequence = getCommitSequence(16);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 17 is correct.
     */
    @Test
    public void testCommitSequence17() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_17;
        CommitSequence actualCommitSequence = getCommitSequence(17);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Tests whether commit sequence 18 is correct.
     */
    @Test
    public void testCommitSequence18() {
        String[] expectedCommitSequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCE_18;
        CommitSequence actualCommitSequence = getCommitSequence(18);
        assertEquals(expectedCommitSequence.length, actualCommitSequence.size(), "Wrong number of commits in sequence");
        
        for (int i = 0; i < expectedCommitSequence.length; i++) {
            assertEquals(expectedCommitSequence[i], actualCommitSequence.get(i), "Wrong commit at index " + i);
        }
    }
    
    /**
     * Checks whether the given {@link CommitSequence}s are equal, which is the case, if both contain the same commits
     * (SHAs) in the same order.
     * 
     * @param commitSequence1 the {@link CommitSequence} to compare to the second one; should never be <code>null</code>
     * @param commitSequence2 the {@link CommitSequence} to compare to the first one; should never be <code>null</code>
     * @return <code>true</code>, if both commit sequences contain the same commits (SHAs) in the same order;
     *         <code>false</code> otherwise
     */
    private boolean equals(CommitSequence commitSequence1, CommitSequence commitSequence2) {
        boolean commitSequencesEqual = true;
        if (commitSequence1.size() != commitSequence2.size()) {
            commitSequencesEqual = false;
        } else {
            int commitCounter = 0;
            while (commitSequencesEqual && commitCounter < commitSequence1.size()) {
                if (!commitSequence1.get(commitCounter).equals(commitSequence2.get(commitCounter))) {
                    commitSequencesEqual = false;
                }
                commitCounter++;
            }
        }
        return commitSequencesEqual;
    }
    
    /**
     * Returns the {@link CommitSequence} with the given sequence number available in the {@link TestSequenceStorage}.
     * 
     * @param sequenceNumber the sequence number of the {@link CommitSequence} to return; must be greater than <i>0</i>
     *        and less than or equal to {@link TestSequenceStorage#getNumberOfCommitSequences()}.
     * @return the {@link CommitSequence} with the given sequence number or <code>null</code>, if the given sequence
     *         number is out of range
     */
    private CommitSequence getCommitSequence(int sequenceNumber) {
        CommitSequence commitSequence = null;
        int numberOfAvailableCommitSequences = sequenceStorage.getNumberOfCommitSequences();
        if (sequenceNumber > 0 && sequenceNumber <= numberOfAvailableCommitSequences) {
            int availableCommitSequencesCounter = 0;
            CommitSequence currentCommitSequence;
            while (commitSequence == null && availableCommitSequencesCounter < numberOfAvailableCommitSequences) {
                currentCommitSequence = sequenceStorage.getCommitSequence(availableCommitSequencesCounter);
                if (currentCommitSequence.getSequenceNumber() == sequenceNumber) {
                    commitSequence = currentCommitSequence;
                }
                availableCommitSequencesCounter++;
            }
        }
        return commitSequence;
    }
}
