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
package net.ssehub.gcs.core;

/**
 * This class represents the specific type of exception thrown if the creation of a {@link CommitCache} failed.
 * 
 * @author Christian Kroeher
 *
 */
public class CommitCacheCreationException extends Exception {

    /**
     * The serial version UID of this class required by the extended {@link Exception}.
     */
    private static final long serialVersionUID = 2010109162946590822L;
    
    /**
     * Constructs a new {@link CommitCacheCreationException} instance.
     * 
     * @param message the description of the problem causing this exception
     */
    public CommitCacheCreationException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link CommitCacheCreationException} instance.
     * 
     * @param message the description of the problem causing this exception
     * @param cause the exception causing this exception
     */
    public CommitCacheCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}
