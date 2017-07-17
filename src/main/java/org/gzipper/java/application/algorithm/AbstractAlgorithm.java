/*
 * Copyright (C) 2017 Matthias Fussenegger
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.gzipper.java.application.pojo.ArchiveInfo;
import org.gzipper.java.application.util.FileUtils;
import org.gzipper.java.i18n.I18N;
import org.gzipper.java.util.Log;

/**
 * Abstract class that offers generally used attributes and methods for
 * archiving algorithms. Any class that represents an archiving algorithm will
 * derive from this class.
 *
 * @author Matthias Fussenegger
 */
public abstract class AbstractAlgorithm implements ArchivingAlgorithm {

    /**
     * True if this algorithm is performing an operation, false otherwise.
     */
    protected volatile boolean _interrupt = false;

    /**
     * Object used to update the progress of the algorithm.
     */
    protected AlgorithmProgress _algorithmProgress;

    /**
     * The compression level. Will only be considered if supported by algorithm.
     */
    protected int _compressionLevel;

    /**
     * Type of the archive stream.
     */
    protected final String _archiveType;

    /**
     * Type of compressor stream.
     */
    protected final String _compressionType;

    /**
     * Factory to create archive streams.
     */
    protected final ArchiveStreamFactory _archiveStreamFactory;

    /**
     * Factory to create compressor streams.
     */
    protected final CompressorStreamFactory _compressorStreamFactory;

    /**
     * Creates a new instance for archiving operations.
     *
     * @param archiveType the type of the archive.
     * @param compressionType the type of the compressor stream.
     */
    protected AbstractAlgorithm(String archiveType, String compressionType) {
        _archiveType = archiveType;
        _compressionType = compressionType;
        _archiveStreamFactory = new ArchiveStreamFactory();
        _compressorStreamFactory = new CompressorStreamFactory();
    }

    @Override
    public void extract(String location, String fullname)
            throws IOException, ArchiveException, CompressorException {

        File archive = new File(fullname);
        initAlgorithmProgress(archive);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(archive));
        CompressorInputStream cis = makeCompressorInputStream(bis);

        try (ArchiveInputStream ais = cis != null
                ? makeArchiveInputStream(cis)
                : makeArchiveInputStream(bis)) {

            ArchiveEntry entry = ais.getNextEntry();

            int startIndex = fullname.lastIndexOf(File.separator) + 1;
            final File outputFolder = new File(location + fullname.substring(
                    startIndex, fullname.indexOf('.', startIndex)));

            if (!outputFolder.exists()) {
                outputFolder.mkdir(); // create output folder of archive
            }

            while (!_interrupt && entry != null) {

                final String entryName = entry.getName();
                final File newFile = new File(outputFolder.getAbsolutePath()
                        + File.separator + entryName);

                // check if entry contains a directory
                if (entryName.indexOf('/') > -1) {
                    if (!newFile.getParentFile().exists()) {
                        newFile.getParentFile().mkdirs(); // also creates parent directories
                    }
                }

                if (!entry.isDirectory()) {
                    // create new output stream and write bytes to file
                    try (BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(newFile))) {
                        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                        int readBytes;
                        while (!_interrupt && (readBytes = ais.read(buffer)) != -1) {
                            bos.write(buffer, 0, readBytes);
                            updateProgress(ais.getBytesRead());
                        }
                    } catch (IOException ex) {
                        if (!_interrupt) {
                            Log.e(ex.getLocalizedMessage(), ex);
                            Log.e("{0}\n{1}", new Object[]{
                                I18N.getString("errorWritingFile.text"), newFile.getPath()
                            });
                        }
                    }
                }
                if (!_interrupt) {
                    entry = ais.getNextEntry();
                }
            }
        }
    }

    @Override
    public void extract(ArchiveInfo info) throws IOException, ArchiveException, CompressorException {
        extract(info.getOutputPath(), info.getArchiveName());
    }

    @Override
    public void compress(File[] files, String location, String name)
            throws IOException, ArchiveException, CompressorException {

        initAlgorithmProgress(files);
        String fullname = FileUtils.combinePathAndFilename(location, name);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fullname));

        CompressorOutputStream cos = makeCompressorOutputStream(bos);
        try (ArchiveOutputStream aos = cos != null
                ? makeArchiveOutputStream(cos)
                : makeArchiveOutputStream(bos)) {
            compress(files, "", aos);
        }
    }

    @Override
    public void compress(ArchiveInfo info) throws IOException, ArchiveException, CompressorException {
        final File[] files = new File[info.getFiles().size()];
        _compressionLevel = info.getLevel();
        compress(info.getFiles().toArray(files), info.getOutputPath(), info.getArchiveName());
    }

    /**
     * Private method for compression and recursive call.
     *
     * @param files the files to compress.
     * @param base the base path to store files to.
     * @param aos the output stream to use.
     * @throws IOException if an I/O error occurs.
     */
    private void compress(File[] files, String base, ArchiveOutputStream aos) throws IOException {

        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int readBytes;

        if (files.length > 0) {
            for (int i = 0; !_interrupt && i < files.length; ++i) {
                // create next file and define entry name based on folder level
                final File newFile = files[i];
                String entryName = base + newFile.getName();
                // start compressing the file
                if (newFile.isFile()) {
                    try (BufferedInputStream buf = new BufferedInputStream(
                            new FileInputStream(newFile))) {
                        // create next archive entry and put it on output stream
                        ArchiveEntry entry = aos.createArchiveEntry(newFile, entryName);
                        aos.putArchiveEntry(entry);
                        // write bytes to file
                        while (!_interrupt && (readBytes = buf.read(buffer)) != -1) {
                            aos.write(buffer, 0, readBytes);
                            updateProgress(aos.getBytesWritten());
                        }
                        aos.closeArchiveEntry();
                    } catch (IOException ex) {
                        if (!_interrupt) {
                            Log.e(ex.getLocalizedMessage(), ex);
                            Log.e("{0}\n{1}", new Object[]{
                                I18N.getString("errorReadingFile.text"), newFile.getPath()
                            });
                        }
                    }
                } else { // child is a directory
                    File[] children = getFiles(newFile.getAbsolutePath());
                    compress(children, entryName + "/", aos);
                }
            }
        }
    }

    /**
     * Retrieves files from a specific directory; mandatory for compression.
     *
     * @param path the path that contains the files to be compressed.
     * @return an array of files from the specified path.
     * @throws IOException if an I/O error occurs.
     */
    private File[] getFiles(String path) throws IOException {
        final File dir = new File(path);
        File[] files = dir.listFiles();
        return files;
    }

    /**
     * Initializes {@link #_algorithmProgress} with the specified files.
     *
     * @param files the files to be used for initialization.
     */
    protected void initAlgorithmProgress(File... files) {
        _algorithmProgress = new AlgorithmProgress(files);
    }

    /**
     * Updates the progress of the current operation and logs an informational
     * message to the UI if a threshold has been exceeded. This will prevent the
     * UI from being flooded.
     *
     * @param readBytes the amount of bytes read so far.
     */
    protected void updateProgress(long readBytes) {
        float progress = _algorithmProgress.getProgress(),
                newProgress = _algorithmProgress.updateProgress(readBytes);
        if (newProgress > progress) {
            Log.i(I18N.getString("progress.text"), Float.toString(newProgress), true);
        }
    }

    /**
     * Creates a new instance of an {@link ArchiveInputStream}. This can be used
     * so that specific algorithms can e.g. skip the archive stream.
     *
     * @param stream the {@link InputStream} being used when creating a new
     * {@link ArchiveInputStream}.
     * @return new instance of {@link ArchiveInputStream}.
     * @throws ArchiveException if an error related to the archiver occurs.
     */
    protected ArchiveInputStream makeArchiveInputStream(InputStream stream)
            throws ArchiveException {
        return _archiveStreamFactory.createArchiveInputStream(_archiveType, stream);
    }

    /**
     * Creates a new instance of an {@link CompressorInputStream}. This can be
     * used so that specific algorithms can e.g. apply individual parameters.
     *
     * @param stream the {@link InputStream} being used when creating a new
     * {@link CompressorInputStream}.
     * @return new instance of {@link CompressorInputStream}.
     * @throws CompressorException if an error related to the compressor occurs.
     */
    protected CompressorInputStream makeCompressorInputStream(InputStream stream)
            throws CompressorException {
        return _compressorStreamFactory.createCompressorInputStream(_compressionType, stream);
    }

    /**
     * Creates a new instance of an {@link ArchiveOutputStream}. This can be
     * used so that specific algorithms can e.g. skip the archive stream.
     *
     * @param stream the {@link OutputStream} being used when creating a new
     * {@link ArchiveOutputStream}.
     * @return new instance of {@link ArchiveOutputStream}.
     * @throws IOException if an I/O error occurs.
     * @throws ArchiveException if an error related to the archiver occurs.
     */
    protected ArchiveOutputStream makeArchiveOutputStream(OutputStream stream)
            throws IOException, ArchiveException {
        return _archiveStreamFactory.createArchiveOutputStream(_archiveType, stream);
    }

    /**
     * Creates a new instance of an {@link CompressorOutputStream}. This can be
     * used so that specific algorithms can e.g. apply individual parameters.
     *
     * @param stream the {@link OutputStream} being used when creating a new
     * {@link CompressorOutputStream}.
     * @return new instance of {@link CompressorOutputStream}.
     * @throws IOException if an I/O error occurs.
     * @throws CompressorException if an error related to the compressor occurs.
     */
    protected CompressorOutputStream makeCompressorOutputStream(OutputStream stream)
            throws IOException, CompressorException {
        return _compressorStreamFactory.createCompressorOutputStream(_compressionType, stream);
    }

    @Override
    public void interrupt() {
        _interrupt = true;
    }
}
