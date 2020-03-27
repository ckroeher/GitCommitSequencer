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
package net.ssehub.gcs.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.ssehub.gcs.utilities.Logger.MessageType;

/**
 * This class provides utility methods for opening and closing {@link FileChannel}s as well as writing to {@link File}s
 * using such a channel.
 * 
 * @author Christian Kroeher
 *
 */
public class FileUtilities {
    
    /**
     * The identifier of this class, e.g., for printing messages.
     */
    private static final String ID = "FileUtilities";
    
    /**
     * The {@link Logger} for pretty-printing messages to the console.
     */
    private Logger logger = Logger.getInstance();
    
    /**
     * The {@link RandomAccessFile} used to open the {@link #outputFileChannel} in {@link #openFileChannel()}.
     * 
     * @see #openFileChannel()
     * @see #toFileChannel(String)
     * @see #closeFileChannel()
     */
    private RandomAccessFile outputFileStream;
    
    /**
     * The {@link FileChannel} used to write the commits of this sequence to the {@link #outputFile}.
     * 
     * @see #openFileChannel()
     * @see #toFileChannel(String)
     * @see #closeFileChannel()
     */
    private FileChannel outputFileChannel;
    
    /**
     * Constructs a new {@link FileUtilities} instance.
     */
    public FileUtilities() {
        outputFileStream = null;
        outputFileChannel = null;
    }

    /**
     * Opens a {@link FileChannel} based on the given output file and saves it internally as 
     * {@link #outputFileChannel} for {@link #write(String)} and {@link #closeFileChannel()}. If the
     * {@link #outputFileChannel} is not <code>null</code>, the {@link #closeFileChannel()}-method is called before
     * opening a new channel. 
     * 
     * @param outputFile the {@link File} for which the file channel shall returned
     * @return <code>true</code>, if opening the file channel was successful; <code>false</code> otherwise 
     * @see #write(String)
     * @see #closeFileChannel()
     */
    public boolean openFileChannel(File outputFile) {
        boolean fileChannelOpen = false;
        if (outputFile != null) {
            // If a file channel already exists, close it before creating a new one
            if (outputFileChannel != null) {
                closeFileChannel(); // TODO What happens if closing fails here?
            }
            try {
                outputFileStream = new RandomAccessFile(outputFile.getAbsolutePath(), "rw");
                outputFileChannel = outputFileStream.getChannel();
                fileChannelOpen = outputFileStream != null && outputFileChannel != null;
            } catch (FileNotFoundException e) {
                logger.logException(ID, "Opening file channel for output file \"" + outputFile.getAbsolutePath()
                        + "\" failed", e);
            }          
        } else {
            logger.log(ID, "Opening file channel for output file failed", "No output file available",
                    MessageType.ERROR);
        }
        return fileChannelOpen;
    }
    
    /**
     * Write the given content via the current {@link #outputFileChannel}.
     * 
     * @param content the {@link String} representing the (file) content to be written
     * @return <code>true</code>, if writing the content was successful; <code>false</code> otherwise
     * @see #openFileChannel(File)
     * @see #closeFileChannel()
     */
    public boolean write(String content) {
        boolean contentWrittenSuccessfully = false;
        if (outputFileStream != null && outputFileChannel != null) {            
            byte[] contentBytes = content.getBytes();
            ByteBuffer contentByteBuffer = ByteBuffer.allocate(contentBytes.length);
            contentByteBuffer.put(contentBytes);
            contentByteBuffer.flip();
            try {
                if (outputFileChannel.write(contentByteBuffer) == contentBytes.length) {
                    contentWrittenSuccessfully = true;
                } // TODO What happens if writing fails here?
            } catch (IOException e) {
                logger.logException(ID, "Writing content to file failed", e);
            }
        } else {
            logger.log(ID, "File stream or file channel not available", "Call \"openFileStream(File)\" before writing",
                    MessageType.ERROR);
        }
        return contentWrittenSuccessfully;
    }
    
    /**
     * Closes the {@link #outputFileStream} and the {@link #outputFileChannel} of this {@link FileUtilities} instance
     * and sets their values to <code>null</code>.
     * 
     * @return <code>true</code>, if closing was successful; <code>false</code> otherwise
     * @see #openFileChannel(File)
     * @see #write(String)
     */
    public boolean closeFileChannel() {
        if (outputFileStream != null) {
            try {
                outputFileStream.close();
                outputFileStream = null;
            } catch (IOException e) {
                logger.logException(ID, "Closing the file stream failed", e);
            }
        }
        if (outputFileChannel != null) {
            try {
                outputFileChannel.close();
                outputFileChannel = null;
            } catch (IOException e) {
                logger.logException(ID, "Closing the file channel failed", e);
            }
        }
        return (outputFileStream == null && outputFileChannel == null);
    }
    
}
