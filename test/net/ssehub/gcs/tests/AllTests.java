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

import static org.junit.Assert.assertNull;
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
    CommitSequenceTests.class
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
        
        // Check if the archive file containing the test repository is available
        assertTrue("\"" + TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath() + "\" does not exist or is not a file",
                (TESTDATA_REPOSITORY_ARCHIVE_FILE.exists() && TESTDATA_REPOSITORY_ARCHIVE_FILE.isFile()));
        
        // Check if the destination of the rest repository already exists; delete it in that case
        testRepository = new File(TESTDATA_INPUT_DIRECTORY, TESTDATA_REPOSITORY_DIRECTORY_NAME);
        if (testRepository.exists()) {
            delete(testRepository);
        }
       
        // Extract the directory (repository) and its files to the destination ("testRepository")
        FileInputStream fileInputStream = null;
        ZipInputStream zipInputStream = null;
        try {
            fileInputStream = new FileInputStream(TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath());
            zipInputStream = new ZipInputStream(fileInputStream);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                assertTrue("Extraction of \"" + zipEntry.getName() + "\" from \"" 
                        + TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath() + "\" failed", 
                        extract(zipEntry, zipInputStream, TESTDATA_INPUT_DIRECTORY));
            }
        } catch (FileNotFoundException | SecurityException e) {
            assertNull("Could not create file input stream for file \"" 
                    + TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath() + "\"", e);
        } catch (IOException e) {
            assertNull("Could not read entry from zip file \"" + TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath()
                    + "\"", e);
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    assertNull("Could not close zip input stream for file \"" 
                            + TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath() + "\"", e);
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    assertNull("Could not close file input stream for file \"" 
                            + TESTDATA_REPOSITORY_ARCHIVE_FILE.getAbsolutePath() + "\"", e);
                }
            }
        }
        
        // Check if the test repository exists (was extracted)
        assertTrue("\"" + testRepository.getAbsolutePath() + "\" does not exist or is not a directory",
                (testRepository.exists() && testRepository.isDirectory()));
        
        System.out.println("#### Global Test Setup ####");
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
                    e.printStackTrace();
                } finally {
                    if (zipEntryBufferedOutputStream != null) {                        
                        try {
                            zipEntryBufferedOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (zipEntryFileOutputStream != null) {
                        try {
                            zipEntryFileOutputStream.close();
                        } catch (IOException e) {
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
        if (testRepository != null && testRepository.exists()) {
            delete(testRepository);
        }
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
}
