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

import net.firejack.platform.core.model.registry.directory.DirectoryModel;

import java.util.List;

public interface IDirectoryStore extends IFieldContainerStore<DirectoryModel> {

    @Override
    void delete(DirectoryModel registryNode);

    /**
     * @return
     */
    List<DirectoryModel> findOrderedDirectoryList();

    /**
     * @param sortPosStart
     * @param sortPosEnd
     * @return
     */
    List<DirectoryModel> findOrderedDirectoryList(final Integer sortPosStart, final Integer sortPosEnd);

    /**
     * @param directoryId
     * @param newPosition
     */
    void moveDirectory(Long directoryId, Integer newPosition);

}