/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.firejack.platform.core.config.meta.expr;


public enum ExpressionType {
    IN,
    NOT, OR, AND, XOR,
    EQ, NEQ, GT, LT, GTE, LTE,
    PLUS, MINUS, MULTIPLY, DIVIDE,
    IS_NULL, IS_NOT_NULL,
    FUNCTION, BRACES, IDENTIFIER, VALUE;

    /**
     * @return
     */
    public boolean isBinary() {
        return !isUnary() && !this.equals(FUNCTION) && !this.equals(BRACES) && !this.equals(IN) && !this.equals(IDENTIFIER) && !this.equals(VALUE);
    }

    /**
     * @return
     */
    public boolean isNullable() {
        return this.equals(IS_NULL) || this.equals(IS_NOT_NULL);
    }

    /**
     * @return
     */
    public boolean isUnary() {
        return isNullable() || this.equals(NOT);
    }

}