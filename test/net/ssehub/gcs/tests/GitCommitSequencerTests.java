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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.gcs.core.ArgumentErrorException;
import net.ssehub.gcs.core.CommitSequence;
import net.ssehub.gcs.core.GitCommitSequencer;
import net.ssehub.gcs.utilities.Logger;

/**
 * 
 * This class contains unit tests for the {@link GitCommitSequencer}.
 * 
 * @author Christian Kroeher
 *
 */
public class GitCommitSequencerTests {
    
    /**
     * The identifier of this class, e.g., for printing messages.
     */
    private static final String ID = "GitCommitSequencerTests";
    
    /**
     * Prints the header of this test class.
     */
    @BeforeClass
    public static void setUp() {
        System.out.println(System.lineSeparator() + "++++ Git Commit Sequencer Tests ++++");
    }
    
    /**
     * Prints the footer of this test class.
     */
    @AfterClass
    public static void tearDown() {
        System.out.println("++++ Git Commit Sequencer Tests ++++");
    }
    
    /**
     * Calls {@link CommitSequence#resetInstanceCounter()} before each test in this class to guarantee correct numbering
     * of the created commit sequences per test.
     */
    @Before
    public void resetCommitSequenceInstanceCounter() {
        CommitSequence.resetInstanceCounter();
    }

    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with parameter <code>null</code> fails.
     */
    @Test
    public void testNoArgs() {
        try {
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(null);
            assertNull(gitCommitSequencer, "Creating sequencer with null as arguments should fail");
        } catch (ArgumentErrorException e) {
            assertNotNull(e, "Creating sequencer with null as arguments should fail");
            System.out.println("GitCommitSequencerTests - testNoArgs: " + e.getMessage());
        }
    }
    
    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with an empty args-parameter fails.
     */
    @Test
    public void testEmptyArgs() {
        try {
            String[] args = {};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer, "Creating sequencer with empty args-parameter should fail");
        } catch (ArgumentErrorException e) {
            assertNotNull(e, "Creating sequencer with empty args-parameter should fail");
            System.out.println("GitCommitSequencerTests - testEmptyArgs: " + e.getMessage());
        }
    }

    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with a single argument in args-parameter
     * fails.
     */
    @Test
    public void testSingleArgs() {
        try {
            String[] args = {""}; // Single argument
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer, "Creating sequencer with single argument in args-parameter should fail");
        } catch (ArgumentErrorException e) {
            assertNotNull(e, "Creating sequencer with single argument in args-parameter should fail");
            System.out.println("GitCommitSequencerTests - testSingleArgs: " + e.getMessage());
        }
    }
    
    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with four arguments in args-parameter fails.
     */
    @Test
    public void testFourArgs() {
        try {
            String[] args = {"", "", "", ""}; // Four arguments
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer, "Creating sequencer with four arguments in args-parameter should fail");
        } catch (ArgumentErrorException e) {
            assertNotNull(e, "Creating sequencer with four arguments in args-parameter should fail");
            System.out.println("GitCommitSequencerTests - testFourArgs: " + e.getMessage());
        }
    }
    
    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with a non-exiting repository directory in
     * args-parameter fails.
     */
    @Test
    public void testNonExistingRepositoryDirectory() {
        try {
            String[] args = {"T:\\his\\does\\not\\exist", ""};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer,
                    "Creating sequencer with a non-exiting repository directory in args-parameter should fail");
        } catch (ArgumentErrorException e) {
            assertNotNull(e,
                    "Creating sequencer with a non-exiting repository directory in args-parameter should fail");
            System.out.println("GitCommitSequencerTests - testNonExistingRepositoryDirectory: " + e.getMessage());
        }
    }
    
    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with a file as repository directory in
     * args-parameter fails.
     */
    @Test
    public void testFileAsRepositoryDirectory() {
        try {
            String[] args = {AllTests.TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath(), ""};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer,
                    "Creating sequencer with a file as repository directory in args-parameter should fail");
        } catch (ArgumentErrorException e) {
            assertNotNull(e,
                    "Creating sequencer with a file as repository directory in args-parameter should fail");
            System.out.println("GitCommitSequencerTests - testFileAsRepositoryDirectory: " + e.getMessage());
        }
    }

    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with a non-exiting output directory in
     * args-parameter fails.
     */
    @Test
    public void testNonExistingOutputDirectory() {
        try {
            String[] args = {AllTests.getTestRepository().getAbsolutePath(), "T:\\his\\does\\not\\exist"};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer,
                    "Creating sequencer with a non-exiting output directory in args-parameter should fail");
        } catch (ArgumentErrorException e) {
            assertNotNull(e,
                    "Creating sequencer with a non-exiting output directory in args-parameter should fail");
            System.out.println("GitCommitSequencerTests - testNonExistingOutputDirectory: " + e.getMessage());
        }
    }
    
    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with a file as output directory in
     * args-parameter fails.
     */
    @Test
    public void testFileAsOutputDirectory() {
        try {
            String[] args = {AllTests.getTestRepository().getAbsolutePath(),
                    AllTests.TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath()};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer,
                    "Creating sequencer with a file as output directory in args-parameter should fail");
        } catch (ArgumentErrorException e) {
            assertNotNull(e,
                    "Creating sequencer with a file as output directory in args-parameter should fail");
            System.out.println("GitCommitSequencerTests - testFileAsOutputDirectory: " + e.getMessage());
        }
    }
    
    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with a non-empty output directory in
     * args-parameter fails.
     */
    @Test
    public void testNonEmptyOutputDirectory() {
        try {
            String[] args = {AllTests.getTestRepository().getAbsolutePath(),
                    AllTests.getTestRepository().getAbsolutePath()};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer,
                    "Creating sequencer with a non-empty output directory in args-parameter should fail");
        } catch (ArgumentErrorException e) {
            assertNotNull(e,
                    "Creating sequencer with a non-empty output directory in args-parameter should fail");
            System.out.println("GitCommitSequencerTests - testNonEmptyOutputDirectory: " + e.getMessage());
        }
    }
    
    /**
     * Tests whether the {@link GitCommitSequencer} creates the expected number of commit sequences, if the HEAD commit
     * of the test repository received by {@link AllTests#getTestRepository()} is used as start commit (no user-defined
     * start commit passed as args-parameter).
     */
    @Test
    public void testSequenceCreationWithHeadAsStartCommit() {
        System.out.println("GitCommitSequencerTests - testSequenceCreationWithHeadAsStartCommit:");
        try {
            String[] args = {AllTests.getTestRepository().getAbsolutePath(),
                    AllTests.TESTDATA_OUTPUT_DIRECTORY.getAbsolutePath()};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            gitCommitSequencer.run();
            
            assertEquals(ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCES.length,
                    AllTests.TESTDATA_OUTPUT_DIRECTORY.list().length, "Wrong number of created commit sequences");
            
        } catch (ArgumentErrorException e) {
            assertNull(e, "This should not happen: " + e.getMessage());
        } finally {            
            // Delete the content of the output directory again
            assertTrue(AllTests.clearTestOutpuDirectory(), "This should not happen");
        }
    }
    
    /**
     * Tests whether the {@link GitCommitSequencer} creates the expected number of commit sequences, if a user-defined,
     * non-existing start commit is used.
     */
    @Test
    public void testSequenceCreationWithUserDefinedNonExistingStartCommit() {
        System.out.println("GitCommitSequencerTests - testSequenceCreationWithUserDefinedNonExistingStartCommit:");
        try {
            String[] args = {AllTests.getTestRepository().getAbsolutePath(),
                    AllTests.TESTDATA_OUTPUT_DIRECTORY.getAbsolutePath(), "abcdefgh"};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            gitCommitSequencer.run();
            
            assertEquals(0, AllTests.TESTDATA_OUTPUT_DIRECTORY.list().length,
                    "Wrong number of created commit sequences");
            
        } catch (ArgumentErrorException e) {
            assertNull(e, "This should not happen: " + e.getMessage());
        } finally {
            // Delete the content of the output directory again
            assertTrue(AllTests.clearTestOutpuDirectory(), "This should not happen");            
        }
    }
    
    /**
     * Tests whether the {@link GitCommitSequencer} creates the expected number of commit sequences, if a user-defined
     * and existing start commit is used.
     */
    @Test
    public void testSequenceCreationWithUserDefinedExistingStartCommit() {
        System.out.println("GitCommitSequencerTests - testSequenceCreationWithUserDefinedExistingStartCommit:");
        try {
            String[] args = {AllTests.getTestRepository().getAbsolutePath(),
                    AllTests.TESTDATA_OUTPUT_DIRECTORY.getAbsolutePath(), "2a79fe77210128198ae05d3731b8693c75fb75e0"};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            gitCommitSequencer.run();
            
            assertEquals(1, AllTests.TESTDATA_OUTPUT_DIRECTORY.list().length,
                    "Wrong number of created commit sequences");
            
        } catch (ArgumentErrorException e) {
            assertNull(e, "This should not happen: " + e.getMessage());
        } finally {
            // Delete the content of the output directory again
            assertTrue(AllTests.clearTestOutpuDirectory(), "This should not happen");            
        }
    }
    
    /**
     * Tests whether the {@link GitCommitSequencer} creates the expected commit sequences.
     */
    @Test
    public void testCorrectSequenceCreation() {
        System.out.println("GitCommitSequencerTests - testCorrectSequenceCreation:");
        try {
            String[] args = {AllTests.getTestRepository().getAbsolutePath(),
                    AllTests.TESTDATA_OUTPUT_DIRECTORY.getAbsolutePath()};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            gitCommitSequencer.run();
            
            assertTrue(checkCreatedCommitSequences(), "Wrong commit sequence(s) created");
            
        } catch (ArgumentErrorException e) {
            assertNull(e, "This should not happen: " + e.getMessage());
        } finally {
            // Delete the content of the output directory again
            assertTrue(AllTests.clearTestOutpuDirectory(), "This should not happen");            
        }
    }
    
    /**
     * Checks whether the number of created commit sequences (files in the {@link AllTests#TESTDATA_OUTPUT_DIRECTORY}
     * and their commits are correct.
     * 
     * @return <code>true</code>, if the number of created commit sequences and their commits are correct;
     *         <code>false</code> otherwise
     */
    private boolean checkCreatedCommitSequences() {
        boolean createdCommitSequencesCorrect = true;
        File[] createdCommitSequenceFiles = AllTests.TESTDATA_OUTPUT_DIRECTORY.listFiles();
        if (createdCommitSequenceFiles.length == ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCES.length) {
            int createdCommitSequenceFilesCounter = 0;
            File createdCommitSequenceFile;
            List<String> createdCommitSequence;
            String[] expectedCommitSequence;
            while (createdCommitSequencesCorrect
                    && createdCommitSequenceFilesCounter < createdCommitSequenceFiles.length) {
                // Get the current commit sequence file from output directory
                createdCommitSequenceFile = createdCommitSequenceFiles[createdCommitSequenceFilesCounter];
                // Read the content of the current commit sequence file (each line represents one commit)
                createdCommitSequence = readFile(createdCommitSequenceFile);
                /*
                 * Get the corresponding, expected commit sequence detected by the number at the end of the commit
                 * sequence file name, e.g.:
                 *     Commit sequence file name = CommitSequence_5.txt
                 *     Commit sequence number = 5
                 *     Expected commit sequence = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCES[4]
                 */
                expectedCommitSequence = getExpectedCommitSequence(createdCommitSequenceFile.getName());
                // Compare the current and expected commit sequence: equal length/size and equal commits at each index
                createdCommitSequencesCorrect = equals(expectedCommitSequence, createdCommitSequence);
                createdCommitSequenceFilesCounter++;
            }
        } else {
            createdCommitSequencesCorrect = false;
        }
        return createdCommitSequencesCorrect;
    }
    
    /**
     * Reads the content of the given file and returns a list of strings in which each string contains a single line of
     * the content of the file.
     * 
     * @param file the {@link File} the content should be read from
     * @return a {@link List} of {@link String}s representing the line-wise content of the given file; may return
     *         <code>null</code>, if the given file cannot be read    
     */
    private List<String> readFile(File file) {
        List<String> fileLines = null;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;       
        try {
            fileLines = new ArrayList<String>();
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String fileLine;
            while ((fileLine = bufferedReader.readLine()) != null) {
                fileLines.add(fileLine);
            }
        } catch (IOException | OutOfMemoryError e) {
            Logger.getInstance().logException(ID, "Reading content from file \"" + file.getAbsolutePath() + "\" failed",
                    e);
        } finally {
            // Close the readers in any case
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    Logger.getInstance().logException(ID,
                            "Closing file reader for \"" + file.getAbsolutePath() + "\" failed", e);
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Logger.getInstance().logException(ID,
                            "Closing buffered reader for \"" + file.getAbsolutePath() + "\" failed", e);
                }
            }
        }
        return fileLines;
    }
    
    /**
     * Returns the expected commit sequence from {@link ExpectedTestRepositoryCommitSequences} based on the created
     * commit sequence file name by matching the respective commit sequence number.
     * 
     * @param commitSequenceFileName the name of the file representing the created commit sequence for which the
     *        expected commit sequence shall be returned
     * @return the expected commit sequence from {@link ExpectedTestRepositoryCommitSequences} or <code>null</code>, if
     *         such a commit sequence is not available
     */
    private String[] getExpectedCommitSequence(String commitSequenceFileName) {
        String[] expectedCommitSequence = null;
        try {
            int expectedCommitSequenceNumber = Integer.parseInt(commitSequenceFileName.substring(15,
                    commitSequenceFileName.length() - 4));
            expectedCommitSequence = 
                    ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCES[expectedCommitSequenceNumber - 1];
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Logger.getInstance().logException(ID,
                    "Retrieving expected commit sequence for file \"" + commitSequenceFileName + "\" failed", e);
        }
        return expectedCommitSequence;
    }
    
    /**
     * Compares the two given commit sequences with respect to their number of elements as well as equal elements at the
     * same indexes.
     * 
     * @param expectedCommitSequence the {@link String}-array representing the expected commit sequence
     * @param createdCommitSequence the {@link List} of {@link String}s representing the created commit sequence
     * @return <code>true</code>, if the given commit sequences are equal; <code>false</code> otherwise
     */
    private boolean equals(String[] expectedCommitSequence, List<String> createdCommitSequence) {
        boolean commitSequencesEqual = true;
        if (expectedCommitSequence.length == createdCommitSequence.size()) {
            int commitCounter = 0;
            while (commitSequencesEqual && commitCounter < expectedCommitSequence.length) {
                if (!expectedCommitSequence[commitCounter].equals(createdCommitSequence.get(commitCounter))) {
                    commitSequencesEqual = false;
                }
                commitCounter++;
            }
        } else {
            commitSequencesEqual = false;
        }
        return commitSequencesEqual;
    }
}
