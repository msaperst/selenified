/*
 * Copyright 2017 Coveros, Inc.
 * 
 * This file is part of Selenified.
 * 
 * Selenified is licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy 
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on 
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations 
 * under the License.
 */

package main.java.selenified.exceptions;

import java.io.IOException;

public class InvalidTestTypeException extends IOException {

	private static final long serialVersionUID = -4707815990022887449L;

	public InvalidTestTypeException() {
		super();
	}

	public InvalidTestTypeException(String msg) {
		super(msg);
	}
}
