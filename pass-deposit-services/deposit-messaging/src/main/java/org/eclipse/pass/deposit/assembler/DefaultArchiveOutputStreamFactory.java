/*
 * Copyright 2019 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.pass.deposit.assembler;

import static java.lang.String.format;

import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class DefaultArchiveOutputStreamFactory implements ArchiveOutputStreamFactory {


    protected static final String ERR_CREATING_ARCHIVE_STREAM = "Error creating a %s archive output stream: %s";

    protected static final String ERR_NO_ARCHIVE_FORMAT = "No supported archive format was specified in the metadata " +
                                                          "builder";

    protected Map<String, Object> packageOptions;

    public DefaultArchiveOutputStreamFactory(Map<String, Object> packageOptions) {
        this.packageOptions = packageOptions;
    }

    @Override
    public ArchiveOutputStream newInstance(Map<String, Object> packageOptions, OutputStream toWrap) {
        // Wrap the output stream in an ArchiveOutputStream
        // we support zip, tar and tar.gz so far
        ArchiveOutputStream archiveOut;

        if (packageOptions.getOrDefault(PackageOptions.Archive.KEY, PackageOptions.Archive.OPTS.NONE) == PackageOptions.Archive.OPTS.TAR) {
            try {
                if (packageOptions.getOrDefault(PackageOptions.Compression.KEY, PackageOptions.Compression.OPTS.NONE) == PackageOptions.Compression.OPTS.GZIP) {
                    archiveOut = new TarArchiveOutputStream(new GzipCompressorOutputStream(toWrap));
                } else {
                    archiveOut = new TarArchiveOutputStream(toWrap);
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format(ERR_CREATING_ARCHIVE_STREAM, PackageOptions.Archive.OPTS.TAR, e.getMessage()), e);
            }
        } else if (packageOptions.getOrDefault(PackageOptions.Archive.KEY, PackageOptions.Archive.OPTS.NONE) == PackageOptions.Archive.OPTS.ZIP) {
            try {
                archiveOut = new ZipArchiveOutputStream(toWrap);
            } catch (Exception e) {
                throw new RuntimeException(String.format(ERR_CREATING_ARCHIVE_STREAM, PackageOptions.Archive.OPTS.ZIP, e.getMessage()), e);
            }
        } else {
            throw new RuntimeException(ERR_NO_ARCHIVE_FORMAT);
        }

        return archiveOut;
    }

}
