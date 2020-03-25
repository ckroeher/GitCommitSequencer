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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.gcs.core.ArgumentErrorException;
import net.ssehub.gcs.core.GitCommitSequencer;

/**
 * This class contains unit tests for the argument handling of the {@link GitCommitSequencer}.
 * 
 * @author Christian Kroeher
 *
 */
public class ArgumentTests {
    
    /**
     * The {@link String} denoting the identifier of this class, e.g., for printing messages.
     */
    private static final String ID = ArgumentTests.class.getSimpleName();
    
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
     * Prints the header of this test class.
     */
    @BeforeClass
    public static void setUp() {
        System.out.println(TEST_CLASS_TITLE);
    }
    
    /**
     * Prints the footer of this test class.
     */
    @AfterClass
    public static void tearDown() {
        System.out.println(TEST_CLASS_TITLE + System.lineSeparator());
    }

    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with parameter <code>null</code> fails.
     */
    @Test
    public void testNoArgs() {
        String testIdPart = " - testNoArgs: ";
        String testSpecificMessagePart = "Creating sequencer with null as arguments should fail";
        try {
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(null);
            assertNull(gitCommitSequencer, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
        } catch (ArgumentErrorException e) {
            assertNotNull(e, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            System.out.println(TEST_PASSED_PREFIX + testIdPart + e.getMessage());
        }
    }
    
    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with an empty args-parameter fails.
     */
    @Test
    public void testEmptyArgs() {
        String testIdPart = " - testEmptyArgs: ";
        String testSpecificMessagePart = "Creating sequencer with empty args-parameter should fail";
        try {
            String[] args = {};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
        } catch (ArgumentErrorException e) {
            assertNotNull(e, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            System.out.println(TEST_PASSED_PREFIX + testIdPart + e.getMessage());
        }
    }

    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with a single argument in args-parameter
     * fails.
     */
    @Test
    public void testSingleArgs() {
        String testIdPart = " - testSingleArgs: ";
        String testSpecificMessagePart = "Creating sequencer with single argument in args-parameter should fail";
        try {
            String[] args = {""}; // Single argument
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
        } catch (ArgumentErrorException e) {
            assertNotNull(e, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            System.out.println(TEST_PASSED_PREFIX + testIdPart + e.getMessage());
        }
    }
    
    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with four arguments in args-parameter fails.
     */
    @Test
    public void testFourArgs() {
        String testIdPart = " - testFourArgs: ";
        String testSpecificMessagePart = "Creating sequencer with four arguments in args-parameter should fail";
        try {
            String[] args = {"", "", "", ""}; // Four arguments
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
        } catch (ArgumentErrorException e) {
            assertNotNull(e, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            System.out.println(TEST_PASSED_PREFIX + testIdPart + e.getMessage());
        }
    }
    
    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with a non-exiting repository directory in
     * args-parameter fails.
     */
    @Test
    public void testNonExistingRepositoryDirectory() {
        String testIdPart = " - testNonExistingRepositoryDirectory: ";
        String testSpecificMessagePart = 
                "Creating sequencer with a non-exiting repository directory in args-parameter should fail";
        try {
            String[] args = {"T:\\his\\does\\not\\exist", ""};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
        } catch (ArgumentErrorException e) {
            assertNotNull(e, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            System.out.println(TEST_PASSED_PREFIX + testIdPart + e.getMessage());
        }
    }
    
    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with a file as repository directory in
     * args-parameter fails.
     */
    @Test
    public void testFileAsRepositoryDirectory() {
        String testIdPart = " - testFileAsRepositoryDirectory: ";
        String testSpecificMessagePart = 
                "Creating sequencer with a file as repository directory in args-parameter should fail";
        try {
            String[] args = {AllTests.TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath(), ""};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
        } catch (ArgumentErrorException e) {
            assertNotNull(e, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            System.out.println(TEST_PASSED_PREFIX + testIdPart + e.getMessage());
        }
    }

    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with a non-exiting output directory in
     * args-parameter fails.
     */
    @Test
    public void testNonExistingOutputDirectory() {
        String testIdPart = " - testNonExistingOutputDirectory: ";
        String testSpecificMessagePart = 
                "Creating sequencer with a non-exiting output directory in args-parameter should fail";
        try {
            String[] args = {AllTests.TESTDATA_INPUT_DIRECTORY.getAbsolutePath(), "T:\\his\\does\\not\\exist"};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
        } catch (ArgumentErrorException e) {
            assertNotNull(e, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            System.out.println(TEST_PASSED_PREFIX + testIdPart + e.getMessage());
        }
    }
    
    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with a file as output directory in
     * args-parameter fails.
     */
    @Test
    public void testFileAsOutputDirectory() {
        String testIdPart = " - testFileAsOutputDirectory: ";
        String testSpecificMessagePart = 
                "Creating sequencer with a file as output directory in args-parameter should fail";
        try {
            String[] args = {AllTests.TESTDATA_INPUT_DIRECTORY.getAbsolutePath(),
                    AllTests.TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath()};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
        } catch (ArgumentErrorException e) {
            assertNotNull(e, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            System.out.println(TEST_PASSED_PREFIX + testIdPart + e.getMessage());
        }
    }
    
    /**
     * Tests whether the creation of a {@link GitCommitSequencer} instance with a non-empty output directory in
     * args-parameter fails.
     */
    @Test
    public void testNonEmptyOutputDirectory() {
        String testIdPart = " - testNonEmptyOutputDirectory: ";
        String testSpecificMessagePart = 
                "Creating sequencer with a non-empty output directory in args-parameter should fail";
        try {
            String[] args = {AllTests.TESTDATA_INPUT_DIRECTORY.getAbsolutePath(),
                    AllTests.TESTDATA_INPUT_DIRECTORY.getAbsolutePath()};
            GitCommitSequencer gitCommitSequencer = new GitCommitSequencer(args);
            assertNull(gitCommitSequencer, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
        } catch (ArgumentErrorException e) {
            assertNotNull(e, TEST_FAILED_PREFIX + testIdPart + testSpecificMessagePart);
            System.out.println(TEST_PASSED_PREFIX + testIdPart + e.getMessage());
        }
    }
    
}
