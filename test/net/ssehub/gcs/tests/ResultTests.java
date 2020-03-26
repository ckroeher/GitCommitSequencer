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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.gcs.core.ArgumentErrorException;
import net.ssehub.gcs.core.CommitSequence;
import net.ssehub.gcs.core.GitCommitSequencer;
import net.ssehub.gcs.utilities.Logger;

/**
 * This class contains unit tests for determining whether the {@link GitCommitSequencer} produces correct results.
 * 
 * @author Christian Kroeher
 *
 */
public class ResultTests {

    /**
     * The {@link String} denoting the identifier of this class, e.g., for printing messages.
     */
    private static final String ID = ResultTests.class.getSimpleName();
    
    /**
     * The {@link String} defining the header and footer title of this test class to be printed at the beginning and the
     * end of all tests in this class.
     * 
     * @see #setUp()
     * @see #tearDown()
     */
    private static final String TEST_CLASS_TITLE = AllTests.TEST_MARKER + " " + ID + " " + AllTests.TEST_MARKER;
    
    /**
     * The {@link String} defining the constant prefix of a message that indicates the successful execution of a test in
     * this class.
     */
    private static final String TEST_PASSED_PREFIX = AllTests.TEST_PASSED_MARKER + " " + ID;
    
    /**
     * The {@link String} defining the constant prefix of a message that indicates the failed execution of a test in
     * this class.
     */
    private static final String TEST_FAILED_PREFIX = AllTests.TEST_FAILED_MARKER + " " + ID;
    
    /**
     * The {@link FilenameFilter} to return commit sequence files only, if {@link File#list(FilenameFilter)} or 
     * {@link File#listFiles(FilenameFilter)} is used in the tests of this class.
     */
    private static final FilenameFilter COMMIT_SEQUENCE_FILE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(File dir, String name) {
            boolean acceptFile = false;
            if (name.startsWith("CommitSequence_") && name.endsWith(".txt")) {
                acceptFile = true;
            }
            return acceptFile;
        }
        
    };
    
    /**
     * Prints the header of this test class and calls {@link AllTests#createTestdata()}.
     */
    @BeforeClass
    public static void setUp() {
        System.out.println(TEST_CLASS_TITLE);
        AllTests.createTestdata();
    }
    
    /**
     * Calls {@link AllTests#deleteTestdata()} and prints the footer of this test class.
     */
    @AfterClass
    public static void tearDown() {
        AllTests.deleteTestdata();
        System.out.println(TEST_CLASS_TITLE);
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
     * Prints a new and empty line after each test in this class for better readability of the test outputs.
     */
    @After
    public void printNewLine() {
        System.out.println("");
    }
    
    /**
     * Tests whether the {@link GitCommitSequencer} creates the expected number of commit sequences, if the HEAD commit
     * of the test repository received by {@link AllTests#getTestRepository()} is used as start commit (no user-defined
     * start commit passed as args-parameter).
     */
    @Test
    public void testSequenceCreationWithHeadAsStartCommit() {
        String testIdPart = " - testSequenceCreationWithHeadAsStartCommit";
        String testSpecificMessagePart = ": Wrong number of created commit sequences";
        System.out.println(ID + testIdPart);
        
        String[] args = {AllTests.getTestRepository().getAbsolutePath(),
                AllTests.TESTDATA_OUTPUT_DIRECTORY.getAbsolutePath()};
        try {
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            gitCommitSequencer.run();
            
            // There must be all available commit sequences (files) TODO and one summary file
            assertEquals(ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCES.length,
                    AllTests.TESTDATA_OUTPUT_DIRECTORY.list().length,
                    TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            
            System.out.println(TEST_PASSED_PREFIX + testIdPart);
        } catch (ArgumentErrorException e) {
            System.out.println(TEST_FAILED_PREFIX + testIdPart);
            assertNull(e, TEST_FAILED_PREFIX + testIdPart + ": " + e.getMessage());
        } finally {
            assertTrue(AllTests.clearTestOutputDirectory(), TEST_FAILED_PREFIX
                    + ": Clearing the output directory for next test failed");
        }
    }
    
    /**
     * Tests whether the {@link GitCommitSequencer} creates the expected number of commit sequences, if a user-defined,
     * non-existing start commit is used.
     */
    @Test
    public void testSequenceCreationWithUserDefinedNonExistingStartCommit() {
        String testIdPart = " - testSequenceCreationWithUserDefinedNonExistingStartCommit";
        String testSpecificMessagePart = ": Wrong number of created commit sequences";
        System.out.println(ID + testIdPart);
        
        String[] args = {AllTests.getTestRepository().getAbsolutePath(),
                AllTests.TESTDATA_OUTPUT_DIRECTORY.getAbsolutePath(), "abcdefgh"};
        try {
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            gitCommitSequencer.run();
            
            assertEquals(0, AllTests.TESTDATA_OUTPUT_DIRECTORY.list().length,
                    TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            
            System.out.println(TEST_PASSED_PREFIX + testIdPart);
        } catch (ArgumentErrorException e) {
            System.out.println(TEST_FAILED_PREFIX + testIdPart);
            assertNull(e, TEST_FAILED_PREFIX + testIdPart + ": " + e.getMessage());
        } finally {
            assertTrue(AllTests.clearTestOutputDirectory(), TEST_FAILED_PREFIX
                    + ": Clearing the output directory for next test failed");
        }
    }
    
    /**
     * Tests whether the {@link GitCommitSequencer} creates the expected number of commit sequences, if a user-defined
     * and existing start commit is used.
     */
    @Test
    public void testSequenceCreationWithUserDefinedExistingStartCommit() {
        String testIdPart = " - testSequenceCreationWithUserDefinedExistingStartCommit";
        String testSpecificMessagePart = ": Wrong number of created commit sequences";
        System.out.println(ID + testIdPart);
        
        String[] args = {AllTests.getTestRepository().getAbsolutePath(),
                AllTests.TESTDATA_OUTPUT_DIRECTORY.getAbsolutePath(), "2a79fe77210128198ae05d3731b8693c75fb75e0"};
        try {
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            gitCommitSequencer.run();
            
            // There must be two files: one for the created commit sequence TODO and one summary file
            assertEquals(1, AllTests.TESTDATA_OUTPUT_DIRECTORY.list().length,
                    TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            
            System.out.println(TEST_PASSED_PREFIX + testIdPart);
        } catch (ArgumentErrorException e) {
            System.out.println(TEST_FAILED_PREFIX + testIdPart);
            assertNull(e, TEST_FAILED_PREFIX + testIdPart + ": " + e.getMessage());
        } finally {
            assertTrue(AllTests.clearTestOutputDirectory(), TEST_FAILED_PREFIX
                    + ": Clearing the output directory for next test failed");
        }
    }
    
    /**
     * Tests whether the {@link GitCommitSequencer} creates the expected commit sequences (correct content), if the HEAD
     * commit of the test repository received by {@link AllTests#getTestRepository()} is used as start commit (no
     * user-defined start commit passed as args-parameter).
     */
    @Test
    public void testCorrectSequenceCreation() {
        String testIdPart = " - testCorrectSequenceCreation";
        String testSpecificMessagePart = ": Wrong commit sequence(s) created";
        System.out.println(ID + testIdPart);
                
        String[] args = {AllTests.getTestRepository().getAbsolutePath(),
                AllTests.TESTDATA_OUTPUT_DIRECTORY.getAbsolutePath()};
        try {
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            gitCommitSequencer.run();
            
            assertTrue(checkCreatedCommitSequences(ID + testIdPart),
                    TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            
            System.out.println(TEST_PASSED_PREFIX + testIdPart);
        } catch (ArgumentErrorException e) {
            System.out.println(TEST_FAILED_PREFIX + testIdPart);
            assertNull(e, TEST_FAILED_PREFIX + testIdPart + ": " + e.getMessage());
        } finally {
            assertTrue(AllTests.clearTestOutputDirectory(), TEST_FAILED_PREFIX
                    + ": Clearing the output directory for next test failed");        
        }
    }
    
    /**
     * Tests whether the {@link GitCommitSequencer} creates the expected summary file (correct content), if the HEAD
     * commit of the test repository received by {@link AllTests#getTestRepository()} is used as start commit (no
     * user-defined start commit passed as args-parameter).
     */
    @Test
    public void testCorrectSummaryCreation() {
        String testIdPart = " - testCorrectSummaryCreation";
        String testSpecificMessagePart = ": Wrong summary created";
        System.out.println(ID + testIdPart);
        
        String[] args = {AllTests.getTestRepository().getAbsolutePath(),
                AllTests.TESTDATA_OUTPUT_DIRECTORY.getAbsolutePath()};
        try {
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            gitCommitSequencer.run();
            
            assertTrue(checkCreatedSummary(ID + testIdPart),
                    TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            
            System.out.println(TEST_PASSED_PREFIX + testIdPart);
        } catch (ArgumentErrorException e) {
            System.out.println(TEST_FAILED_PREFIX + testIdPart);
            assertNull(e, TEST_FAILED_PREFIX + testIdPart + ": " + e.getMessage());
        } finally {
            assertTrue(AllTests.clearTestOutputDirectory(), TEST_FAILED_PREFIX
                    + ": Clearing the output directory for next test failed");        
        }
    }
    
    /**
     * Checks whether the names and total numbers of commits for each commit sequence in the Git commit sequencer
     * summary are correct with respect to the created commit sequences (files in the
     * {@link AllTests#TESTDATA_OUTPUT_DIRECTORY}.
     * 
     * @param messagePrefix the {@link String} to print before a particular error message of this method
     * @return <code>true</code>, if the names and total numbers of commits for each commit sequence in the Git commit
     *         sequencer summary are correct; <code>false</code> otherwise
     */
    private boolean checkCreatedSummary(String messagePrefix) {
        boolean createdSummaryCorrect = true;
        // Get the Git commit sequencer summary file
        File createSummaryFile = new File(AllTests.TESTDATA_OUTPUT_DIRECTORY, "GitCommitSequencer_Summary.csv");
        if (createSummaryFile.exists()  && createSummaryFile.isFile()) {
            List<String> createSummaryFileLines = readFile(createSummaryFile);
            // Get the created commit sequences (files)
            File[] createdCommitSequenceFiles = 
                    AllTests.TESTDATA_OUTPUT_DIRECTORY.listFiles(COMMIT_SEQUENCE_FILE_FILTER);
            if (createSummaryFileLines.size() == createdCommitSequenceFiles.length) {
                int createdSummaryFileLinesCounter = 0;
                String createdSummaryFileLine;
                String[] createdSummaryFileCommitSequenceInformation;
                File createdCommitSequenceFile;
                while (createdSummaryCorrect && createdSummaryFileLinesCounter < createSummaryFileLines.size()) {
                    // Get the name of the commit sequences at the current index (line in summary file)
                    createdSummaryFileLine = createSummaryFileLines.get(createdSummaryFileLinesCounter);
                    createdSummaryFileCommitSequenceInformation = createdSummaryFileLine.trim().split(",");
                    // Get the corresponding commit sequence (file) for the commit sequence name in the summary file
                    createdCommitSequenceFile = 
                            getCreatedCommitSequenceFile(createdSummaryFileCommitSequenceInformation[0],
                                    createdCommitSequenceFiles);
                    /*
                     * Compare the name and total number of commits defined in the summary file with the created commit
                     * sequence (file) 
                     */
                    if (!compareCommitSequenceInformation(createdSummaryFileCommitSequenceInformation,
                            createdCommitSequenceFile)) {
                        System.out.println(messagePrefix + ": Summary information for commit sequence \"" 
                                + createdCommitSequenceFile.getName() + "\" not as expected");
                        createdSummaryCorrect = false;
                    }
                    
                    createdSummaryFileLinesCounter++;
                }
            } else {
                System.out.println(messagePrefix + ": Wrong number of commit sequences in summary");
                createdSummaryCorrect = false;
            }
        } else {
            System.out.println(messagePrefix + ": Summary does not exist or is not a file");
            createdSummaryCorrect = false;
        }
        return createdSummaryCorrect;
    }
    
    /**
     * Returns the {@link File} with the given name (without file extension) in the given array.
     * 
     * @param createdCommitSequenceFileName the name of the {@link File} to be returned; should never be
     *        <code>null</code>
     * @param createdCommitSequenceFiles the array of {@link File}s to search in; should never be <code>null</code>
     * @return the {@link File} with the given name or <code>null</code>, if no such file exists in the given array
     */
    private File getCreatedCommitSequenceFile(String createdCommitSequenceFileName, File[] createdCommitSequenceFiles) {
        File createdCommitSequenceFile = null;
        File currentCommitSequenceFile;
        int createdCommitSequenceFileCounter = 0;
        while (createdCommitSequenceFile == null
                && createdCommitSequenceFileCounter < createdCommitSequenceFiles.length) {
            currentCommitSequenceFile = createdCommitSequenceFiles[createdCommitSequenceFileCounter];
            if (currentCommitSequenceFile.getName().equals(createdCommitSequenceFileName + ".txt")) {
                createdCommitSequenceFile = currentCommitSequenceFile;
            }
            createdCommitSequenceFileCounter++;
        }
        return createdCommitSequenceFile;
    }
    
    /**
     * Compares the commit sequence information as provided by the given {@link String}-array (single line of the Git
     * commit sequencer summary) with the information provided by the given, respective commit sequence {@link File}.
     * 
     * @param createdSummaryFileCommitSequenceInformation the commit sequence information to compare; should never be
     *        <code>null</code>
     * @param createdCommitSequenceFile the commit sequence file to compare
     * @return <code>true</code>, if the information about the commit sequence are equal; <code>false</code> otherwise
     */
    private boolean compareCommitSequenceInformation(String[] createdSummaryFileCommitSequenceInformation,
            File createdCommitSequenceFile) {
        boolean commitSequenceInformationEqual = false;
        if (createdCommitSequenceFile != null) {
            String expectedFileName = createdSummaryFileCommitSequenceInformation[0] + ".txt";
            if (expectedFileName.equals(createdCommitSequenceFile.getName())) {
                List<String> createdCommitSequenceFileLines = readFile(createdCommitSequenceFile);
                int summaryNumberOfCommits = Integer.parseInt(createdSummaryFileCommitSequenceInformation[1]);
                if (createdCommitSequenceFileLines.size() == summaryNumberOfCommits) {
                    commitSequenceInformationEqual = true;
                }
            }
        }
        return commitSequenceInformationEqual;
    }
    
    /**
     * Checks whether the number of created commit sequences (files in the {@link AllTests#TESTDATA_OUTPUT_DIRECTORY}
     * and their commits are correct.
     * 
     * @param messagePrefix the {@link String} to print before a particular error message of this method
     * @return <code>true</code>, if the number of created commit sequences and their commits are correct;
     *         <code>false</code> otherwise
     */
    private boolean checkCreatedCommitSequences(String messagePrefix) {
        boolean createdCommitSequencesCorrect = true;
        String[][] expectedCommitSequences = ExpectedTestRepositoryCommitSequences.COMMIT_SEQUENCES;
        List<String[]> createdCommitSequences = getCreatedCommitSequences();
        if (expectedCommitSequences.length == createdCommitSequences.size()) {
            int commitSequencesCounter = 0;
            while (createdCommitSequencesCorrect && commitSequencesCounter < expectedCommitSequences.length) {
                if (!contains(createdCommitSequences, expectedCommitSequences[commitSequencesCounter])) {
                    System.out.println(messagePrefix + ": Expected commit sequence " + commitSequencesCounter 
                            + " not created");
                    createdCommitSequencesCorrect = false;
                }
                commitSequencesCounter++;
            }
            /*
             * As the contains-method above also removes the created commit sequence from the list of all created
             * commit sequences, if it matches an expected sequences, the final check is that all expected commits were
             * contained in that list and the list is empty (there were no additional/unexpected sequences created).
             */
            createdCommitSequencesCorrect = createdCommitSequencesCorrect && createdCommitSequences.isEmpty();
        } else {
            System.out.println(messagePrefix + ": Wrong number of created commit sequences");
            createdCommitSequencesCorrect = false;
        }
        return createdCommitSequencesCorrect;
    }
    
    /**
     * Returns the {@link List} of all commit sequences created and saved as individual files in the 
     * {@link AllTests#TESTDATA_OUTPUT_DIRECTORY} by the {@link GitCommitSequencer}. An individual commit sequence in
     * that list is represented by a {@link String}-array, which contains the lines of the created commit sequences
     * file (each commit of that sequence as individual entry in the array).
     * 
     * @return the {@link List} of all commit sequences currently available as individual files in the
     *         {@link AllTests#TESTDATA_OUTPUT_DIRECTORY} or <code>null</code>, if that directory does not exist or the
     *         contained commit sequence files could not be retrieved 
     */
    private List<String[]> getCreatedCommitSequences() {
        List<String[]> createdCommitSequences = null;
        File outputDirectory = AllTests.TESTDATA_OUTPUT_DIRECTORY;
        if (outputDirectory.exists() && outputDirectory.isDirectory()) {
            File[] createdCommitSequenceFiles = outputDirectory.listFiles(COMMIT_SEQUENCE_FILE_FILTER);
            if (createdCommitSequenceFiles != null) {
                createdCommitSequences = new ArrayList<String[]>();
                List<String> createdCommitSequenceFileLines;
                for (int i = 0; i < createdCommitSequenceFiles.length; i++) {
                    createdCommitSequenceFileLines = readFile(createdCommitSequenceFiles[i]);
                    createdCommitSequences.add(createdCommitSequenceFileLines.toArray(new String[0]));
                }
            }
        }
        return createdCommitSequences;
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
     * Checks whether the given {@link List} of created commit sequences contains a sequence that matches the given
     * {@link String}-array denoting an expected commit sequence. The actual matching occurs in the
     * {@link #equals(String[], String[])}-method and results in the removal of the matching sequence from the list of
     * created commit sequences.
     * 
     * @param createdCommitSequences the {@link List} of created commit sequences in which the expected sequence should
     *        be found
     * @param expectedCommitSequence the {@link String}-array representing an expected commit sequence to be found in
     *        the list of created commit sequences
     * @return <code>true</code>, if the list of created commit sequences contains the expected sequences;
     *         <code>false</code> otherwise
     */
    private boolean contains(List<String[]> createdCommitSequences, String[] expectedCommitSequence) {
        boolean expectedCommitSequenceFound = false;
        int createdCommitSequencesCounter = 0;
        while (!expectedCommitSequenceFound && createdCommitSequencesCounter < createdCommitSequences.size()) {
            if (equals(createdCommitSequences.get(createdCommitSequencesCounter), expectedCommitSequence)) {
                createdCommitSequences.remove(createdCommitSequencesCounter);
                expectedCommitSequenceFound = true;
            }
            createdCommitSequencesCounter++;
        }
        return expectedCommitSequenceFound;
    }
    
    /**
     * Compares the two given commit sequences with respect to their number of elements as well as equal elements at the
     * same indexes.
     * 
     * @param createdCommitSequence the {@link String}-array representing a commit sequence created by the 
     *        {@link GitCommitSequencer}
     * @param expectedCommitSequence the {@link String}-array representing the expected commit sequence as defined in
     *        {@link ExpectedTestRepositoryCommitSequences}
     * @return <code>true</code>, if the given commit sequences are equal; <code>false</code> otherwise
     */
    private boolean equals(String[] createdCommitSequence, String[] expectedCommitSequence) {
        boolean commitSequencesEqual = true;
        if (expectedCommitSequence.length == createdCommitSequence.length) {
            int commitCounter = 0;
            while (commitSequencesEqual && commitCounter < expectedCommitSequence.length) {
                if (!expectedCommitSequence[commitCounter].equals(createdCommitSequence[commitCounter])) {
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
