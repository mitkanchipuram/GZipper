/*
 * Copyright (C) 2016 Matthias Fussenegger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gzipper.java.application.algorithm;

import java.io.File;
import java.io.IOException;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.compressors.CompressorException;
import org.gzipper.java.application.pojo.ArchiveInfo;

/**
 * Implementing class offers methods for compressing and extracting archives.
 *
 * @author Matthias Fussenegger
 */
public interface ArchivingAlgorithm {

    /**
     * Extracts an archive using the algorithm of the concrete class and stores
     * the files of the archive to the specified path.
     *
     * @param path The absolute path of the archive to extract.
     * @param name The filename of the archive to extract.
     * @throws java.io.IOException
     * @throws org.apache.commons.compress.archivers.ArchiveException
     * @throws org.apache.commons.compress.compressors.CompressorException
     */
    void extract(String path, String name)
            throws IOException, ArchiveException, CompressorException;

    /**
     * Extracts an archive using the algorithm of the concrete class and stores
     * the files of the archive to the path specified in {@link ArchiveInfo}.
     *
     * @param info POJO that holds information required for extraction.
     * @throws IOException
     * @throws ArchiveException
     * @throws CompressorException
     */
    void extract(ArchiveInfo info) throws IOException, ArchiveException, CompressorException;

    /**
     * Compresses files using the algorithm of the concrete class with default
     * settings and stores an archive to the specified path.
     *
     * @param files The files selected from the file chooser.
     * @param location Where to store the archive.
     * @param name The name of the archive.
     * @throws java.io.IOException
     * @throws org.apache.commons.compress.archivers.ArchiveException
     * @throws org.apache.commons.compress.compressors.CompressorException
     */
    void compress(File[] files, String location, String name)
            throws IOException, ArchiveException, CompressorException;

    /**
     * Compresses files using the algorithm of the concrete class with default
     * settings and stores an archive to the path specified in
     * {@link ArchiveInfo}.
     *
     * @param info POJO that holds information required for compression.
     * @throws IOException
     * @throws ArchiveException
     * @throws CompressorException
     */
    void compress(ArchiveInfo info) throws IOException, ArchiveException, CompressorException;

}
