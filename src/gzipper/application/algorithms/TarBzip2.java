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

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * Offers algorithms to compress and decompress TAR+BZIP2 archives.
 *
 * @author Matthias Fussenegger
 */
public class TarBzip2 extends AbstractAlgorithm {

    private TarBzip2() {
        super(ArchiveStreamFactory.TAR, CompressorStreamFactory.BZIP2);
    }

    public static TarBzip2 getInstance() {
        return TarBzip2Holder.INSTANCE;
    }

    private static class TarBzip2Holder {

        private static final TarBzip2 INSTANCE = new TarBzip2();
    }
}
