/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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
