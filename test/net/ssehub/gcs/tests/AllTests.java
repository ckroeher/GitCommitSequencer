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

import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Definition of this test suite.
 */
@RunWith(Suite.class)
@SuiteClasses({
    CommitSequenceTests.class,
    GitCommitSequencerTests.class
    })


/**
 * This class summarizes all individual test classes into a single test suite. Further it provides global setup and
 * tear-down methods that take care of creating and deleting the test repository.
 * 
 * @author Christian Kroeher
 *
 */
public class AllTests {

    /**
     * The {@link File} denoting the test data input directory. For example, this directory contains the 
     * {@link #TESTDATA_REPOSITORY_ARCHIVE_FILE} as well as the {@link #testRepository} temporarily.
     */
    public static final File TESTDATA_INPUT_DIRECTORY = new File("./testdata/input");
    
    /**
     * The {@link File} denoting the archive file (zip), which contains the test repository for the unit tests.
     */
    public static final File TESTDATA_REPOSITORY_ARCHIVE_FILE = new File(TESTDATA_INPUT_DIRECTORY,
            "testrepository.zip");
    
    /**
     * The {@link String} denoting the name of the test repository.
     */
    public static final String TESTDATA_REPOSITORY_DIRECTORY_NAME = "TestRepository";
    
    /**
     * The {@link File} denoting the test data output directory. For example, this directory contains the output of the
     * tool.
     */
    public static final File TESTDATA_OUTPUT_DIRECTORY = new File("./testdata/output");
    
    /**
     * The {@link File} denoting the test repository created during {@link #globalSetUp()}.
     */
    private static File testRepository;
    
    /**
     * Creates the {@link #testRepository} as an input for the tests by extracting it from the
     * {@link #TESTDATA_REPOSITORY_ARCHIVE_FILE}.
     */
    @BeforeClass
    public static void globalSetUp() {
        System.out.println("#### Global Test Setup ####");
        // Create the output directory, if it does not exist
        if (!TESTDATA_OUTPUT_DIRECTORY.exists()) {
            assertTrue(TESTDATA_OUTPUT_DIRECTORY.mkdir());
        }
        // Check if the archive file containing the test repository is available
        if (TESTDATA_REPOSITORY_ARCHIVE_FILE.exists() && TESTDATA_REPOSITORY_ARCHIVE_FILE.isFile()) {
            // Check if the destination of the test repository already exists; delete it in that case
            testRepository = new File(TESTDATA_INPUT_DIRECTORY, TESTDATA_REPOSITORY_DIRECTORY_NAME);
            if (testRepository.exists() && !delete(testRepository)) {
                assertTrue(false); // Force termination, if deletion fails
            }
            // Extract the test repository
            if (extractTestRepository()) {
                // Check if the test repository exists (was extracted)
                if (testRepository.exists() && testRepository.isDirectory()) {
                    System.out.println("Extraction successful; test repository \"" + testRepository.getAbsolutePath() 
                            + "\" available");
                } else {
                    System.err.println("Test repository \"" + testRepository.getAbsolutePath() 
                            + "\" does not exist or is not a directory");
                    assertTrue(false); // Force termination, if test repository is no available after extraction
                }
            } else {
                System.err.println("Extraction of test repository from archive \"" 
                        + TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath() + "\" failed");
                assertTrue(false); // Force termination, if extraction of test repository failed
            }

        } else {
            System.err.println("Test resository archive \"" + TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath() 
                    + "\" does not exist or is not a file");
            assertTrue(false); // Force termination, if the archive file containing the test repository does not exist
        }
        System.out.println("#### Global Test Setup ####" + System.lineSeparator());
    }
    
    /**
     * Extracts the content of the test repository archive denoted by {@link #TESTDATA_REPOSITORY_ARCHIVE_FILE} to the 
     * {@link #TESTDATA_INPUT_DIRECTORY}. The result is the availability of the {@link #testRepository}.
     *  
     * @return <code>true</code>, if extracting each entry in the {@link #TESTDATA_REPOSITORY_ARCHIVE_FILE} was
     *         successful; <code>false</code> otherwise
     */
    private static boolean extractTestRepository() {
        boolean extractedSuccessful = true;
        FileInputStream fileInputStream = null;
        ZipInputStream zipInputStream = null;
        try {
            fileInputStream = new FileInputStream(TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath());
            zipInputStream = new ZipInputStream(fileInputStream);
            ZipEntry zipEntry;
            System.out.println("Extracting test repository from archive \"" 
                    + TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath() + "\"");
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!extract(zipEntry, zipInputStream, TESTDATA_INPUT_DIRECTORY)) {
                    System.err.println("Extraction of \"" + zipEntry.getName() 
                            + "\" from test repository archive failed");
                    extractedSuccessful = false;
                }
            }
        } catch (FileNotFoundException | SecurityException e) {
            extractedSuccessful = false;
            System.err.println("Creation of file input stream for file \"" 
                    + TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath() + "\" failed");
            e.printStackTrace();
        } catch (IOException e) {
            extractedSuccessful = false;
            System.err.println("Reading entry from archive \"" + TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath()
                    + "\" failed");
            e.printStackTrace();
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    extractedSuccessful = false;
                    System.err.println("Closing zip input stream for file \"" 
                            + TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath() + "\" failed");
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    extractedSuccessful = false;
                    System.err.println("Closing file input stream for file \"" 
                            + TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath() + "\" failed");
                    e.printStackTrace();
                }
            }
        }
        return extractedSuccessful;
    }
    
    /**
     * Extracts the given {@link ZipEntry} via the given {@link ZipInputStream} to the given destination {@link File}
     * (directory).
     * 
     * @param zipEntry the {@link ZipEntry} to extract
     * @param zipInputStream the {@link ZipInputStream} of the given zip entry; should never be <code>null</code>
     * @param destinationDirectory the {@link File} denoting the destination directory for extracting the given zip
     *        entry; should never be <code>null</code> and must always be an existing directory
     * @return <code>true</code>, if extracting the given zip entry was successful; <code>false</code> otherwise
     */
    private static boolean extract(ZipEntry zipEntry, ZipInputStream zipInputStream, File destinationDirectory) {
        boolean extractedSuccessful = false;
        if (zipEntry != null) {
            File zipEntryFile = new File(destinationDirectory, zipEntry.getName());
            if (zipEntry.isDirectory()) {
                extractedSuccessful = zipEntryFile.mkdir();
            } else {
                FileOutputStream zipEntryFileOutputStream = null;
                BufferedOutputStream zipEntryBufferedOutputStream = null;
                try {
                    zipEntryFileOutputStream = new FileOutputStream(zipEntryFile);
                    zipEntryBufferedOutputStream = new BufferedOutputStream(zipEntryFileOutputStream);
                    byte[] bytesIn = new byte[4096];
                    int read = 0;
                    while ((read = zipInputStream.read(bytesIn)) != -1) {
                        zipEntryBufferedOutputStream.write(bytesIn, 0, read);
                    }
                    extractedSuccessful = true;
                } catch (IOException e) {
                    System.err.println("Writing archive entry \"" + zipEntryFile.getAbsolutePath() + "\" failed");
                    e.printStackTrace();
                } finally {
                    if (zipEntryBufferedOutputStream != null) {                        
                        try {
                            zipEntryBufferedOutputStream.close();
                        } catch (IOException e) {
                            System.err.println("Closing buffered output stream for archive entry \"" 
                                    + zipEntryFile.getAbsolutePath() + "\" failed");
                            e.printStackTrace();
                        }
                    }
                    if (zipEntryFileOutputStream != null) {
                        try {
                            zipEntryFileOutputStream.close();
                        } catch (IOException e) {
                            System.err.println("Closing file output stream for archive entry \""
                                    + zipEntryFile.getAbsolutePath() + "\" failed");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return extractedSuccessful;
    }
    
    /**
     * Deletes the {@link #testRepository} after all tests are done.
     */
    @AfterClass
    public static void globalTearDown() {
        System.out.println(System.lineSeparator() + "#### Global Test Teardown ####");
        if (testRepository != null && testRepository.exists()) {
            System.out.println("Deleting \"" + testRepository.getAbsolutePath() + "\"");
            if (delete(testRepository)) {
                System.out.println("Deletion successful");
            } else {
                System.err.println("Deletion failed");
            }
        } else {
            System.err.println("Deleting test repository failed; repository does not exist");
        }
        System.out.println("#### Global Test Teardown ####");
    }
    
    /**
     * Deletes the given {@link File} and all nested elements recursively.
     * 
     * @param file the {@link File} to delete; should never be <code>null</code>
     * @return <code>true</code>, if deleting the given file and all nested elements was successful; <code>false</code>
     *         otherwise
     */
    private static boolean delete(File file) {
        boolean deletionSuccessful = false;
        if (file.isDirectory()) {
            File[] nestedFiles = file.listFiles();
            for (int i = 0; i < nestedFiles.length; i++) {
                deletionSuccessful = delete(nestedFiles[i]);
                if (!deletionSuccessful) {
                    System.err.println("Cannot delete \"" + nestedFiles[i].getAbsolutePath() + "\"");
                }
            }
        }
        deletionSuccessful = file.delete();
        return deletionSuccessful;
    }
    
    /**
     * Returns the {@link #testRepository}.
     * 
     * @return the {@link File} denoting the test repository; may be <code>null</code>, if {@link #globalSetUp()} fails
     *         unexpectedly
     */
    public static File getTestRepository() {
        return testRepository;
    }
    
    /**
     * Deletes all files and directories in the {@link #TESTDATA_OUTPUT_DIRECTORY}.
     * 
     * @return <code>true</code>, if deletion was successful; <code>false</code> otherwise
     */
    public static boolean clearTestOutpuDirectory() {
        boolean clearingTestOutputDirectorySuccessful = true;
        File[] testOutputDirectoryFiles = TESTDATA_OUTPUT_DIRECTORY.listFiles();
        int testOutputDirectoryFilesCounter = 0;
        while (clearingTestOutputDirectorySuccessful 
                && testOutputDirectoryFilesCounter < testOutputDirectoryFiles.length) {
            clearingTestOutputDirectorySuccessful = delete(testOutputDirectoryFiles[testOutputDirectoryFilesCounter]);
            testOutputDirectoryFilesCounter++;
        }
        return clearingTestOutputDirectorySuccessful;
    }
}
