/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.sql.rowset.serial;

import java.sql.Struct;
import java.sql.ResultSet;
import java.sql.SQLData;
import java.sql.SQLException;
import java.util.Map;

public  class   SerialStruct implements  Struct, Cloneable, java.io.Serializable
{
    public  SerialStruct(SQLData in, Map<String,Class<?>> map) throws SerialException {}
    public  SerialStruct(Struct in, Map<String,Class<?>> map) throws SerialException {}

    public  Object[] 	getAttributes() throws SerialException { return null; }
    public  Object[] 	getAttributes(Map<String,Class<?>> map) throws SerialException { return null; }
    public  String 	getSQLTypeName() throws SerialException { return null; }
}

