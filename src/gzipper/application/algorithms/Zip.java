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
package gzipper.application.algorithms;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

/**
 * Offers algorithms to compress and decompress ZIP archives.
 *
 * @author Matthias Fussenegger
 */
public class Zip extends AbstractAlgorithm {

    /**
     * Creates a new object for zip/unzip operations on zip-archives
     *
     * @param path The path of the output directory
     * @param name The name of the target archive
     * @param files The selected files from GUI
     */
    public Zip(String path, String name, File[] files) {
        super(path, name, ".zip", files);
    }

    @Override
    protected ArchiveInputStream getInputStream() throws IOException {
        return new ZipArchiveInputStream(
                new BufferedInputStream(
                        new FileInputStream(Path + Name)));
    }

    @Override
    protected ArchiveOutputStream getOutputStream() throws IOException {
        return new ZipArchiveOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(Path + Name + ArchiveType)));
    }

}
