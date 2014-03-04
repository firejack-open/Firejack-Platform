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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.core.model.registry.system.FileStoreModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public interface IFileStore extends IAliasableStore<FileStoreModel> {

    String createDirectory(String... path) throws IOException;

    void renameDirectory(String name, String... path) throws IOException;

    List<File> search(Pattern pattern, String... path) throws IOException;

    void upload(InputStream stream, String... path) throws IOException;

    InputStream download(String... path) throws IOException;

    void delete(String... path) throws IOException;

    FileInfo getInfo(String... path);

    void zip(Map<String, InputStream> streams, String... path) throws IOException;

    void updatezip(Map<String, InputStream> streams, String... path) throws IOException;

    void unzip(InputStream stream, String... path) throws IOException;

    List<FileInfo> unzipTemp(InputStream stream, String[] path) throws IOException;

}
