/**
* Copyright (c) 2014, The British Library Board
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
*   in the documentation and/or other materials provided with the distribution.
* Neither the name of The British Library nor the names of its contributors may be used to endorse or promote products
*   derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
*   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
*   IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
*   OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
*   OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
*   OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
*   EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package uk.bl.iiifimageservice.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.bl.iiifimageservice.service.imagelocation.ImageLocationStrategy;

/**
 * Interacts with file system. Checks for existence of and reads files.
 * 
 * @author pblake
 * 
 */
@Service
public class FileSystemReader {

    private static final Logger log = LoggerFactory.getLogger(FileSystemReader.class);

    @Resource(name = "imageLocationStrategyName")
    private ImageLocationStrategy imageLocationStrategy;

    public Path getImagePathFromIdentifier(String identifier) {
        return imageLocationStrategy.getImagePath(identifier);
    }

    public boolean imageFileExists(String identifier) {
        Path path = getImagePathFromIdentifier(identifier);
        log.debug("Expected image location [" + path.toString() + "]");
        return Files.exists(path) && !Files.isDirectory(path);
    }

    public Path getLogFileFromIdentifier(String identifier) {
        Path path = imageLocationStrategy.getLogPath(identifier);
        log.debug("Log file location [" + path.toString() + "]");
        return path;
    }

    public boolean logFileExists(String identifier) {
        Path path = getLogFileFromIdentifier(identifier);
        return Files.exists(path) && !Files.isDirectory(path);
    }

    public Path getOutputFilename(String extension) {
        Path outputFilename = imageLocationStrategy.getExtractedImagePath(UUID.randomUUID()
                                                                              .toString(), extension);
        log.debug("UUID generated output path [" + outputFilename.toString() + "]");
        return outputFilename;
    }

    public String readFile(Path path) throws IOException {

        String logFileContents = null;
        logFileContents = new String(Files.readAllBytes(path));

        return logFileContents;
    }

}
