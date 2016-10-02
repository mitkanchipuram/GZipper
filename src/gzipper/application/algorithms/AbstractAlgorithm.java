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

import java.io.File;
import java.io.IOException;

/**
 * Abstract class that offers generally used attributes and methods for
 * archiving algorithms. Any class that contains an archiving algorithm will
 * extend this class.
 *
 * @author Matthias Fussenegger
 */
public abstract class AbstractAlgorithm {

    /**
     * The name of the archive to be processed.
     */
    protected String _archiveName;

    /**
     * The path of the archive to be processed.
     */
    protected final String _path;

    /**
     * True for compressing, false for extracting an archive.
     */
    protected final boolean _isCreateArchive;

    /**
     * The selected files to be put in an archive by file chooser.
     */
    protected final File[] _selectedFiles;

    /**
     * Creates a new object of the child class for archiving operations.
     *
     * @param path The path of the output directory
     * @param name The name of the target archive
     * @param files The selected files from GUI
     * @param zipMode True if zip, false if unzip
     */
    protected AbstractAlgorithm(String path, String name, File[] files, boolean zipMode) {
        _path = path;
        _archiveName = name;
        _isCreateArchive = zipMode;
        _selectedFiles = files;
    }

    /**
     * Retrieves files from a specific directory; mandatory for compression.
     *
     * @param path The path that contains the files to be compressed
     * @return And array of files from the specified path
     * @throws IOException If an error occurred
     */
    protected File[] getFiles(String path) throws IOException {
        File dir = new File(path);
        File[] files = dir.listFiles();
        return files;
    }

    /**
     * Extracts archive using defined algorithm of class to the specified path.
     *
     * @param path The absolute path of the archive
     * @param name The filename of the archive
     * @throws IOException If an error during extraction error occurred
     */
    public abstract void extract(String path, String name) throws IOException;

    /**
     * Compresses files using defined algorithm of class with default settings
     * and creates an archive to the specified path.
     *
     * @param files The files selected from the file chooser
     * @param base The root path of the specified folder
     * @throws IOException If an error during compressing occurred
     */
    public abstract void compress(File[] files, String base) throws IOException;

}
