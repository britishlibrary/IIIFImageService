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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import uk.bl.iiifimageservice.domain.ImageError.ParameterName;
import uk.bl.iiifimageservice.domain.ImageFormat;
import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.ImageRequestMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.domain.ServerRequestData;
import uk.bl.iiifimageservice.service.kakaduextractor.KakaduParameterCalculator;
import uk.bl.iiifimageservice.util.ImageServiceException;

public abstract class AbstractImageService implements ImageService {

    private static final Logger log = LoggerFactory.getLogger(AbstractImageService.class);

    private final static String COMPLIANCE_LEVEL_URL = "http://library.stanford.edu/iiif/image-api/compliance.html#level";
    protected final static String MISSING_IMAGE_MESSAGE = "image does not exist";

    @Value("${kakadu.binary.path}")
    protected String kakaduBinaryPath;

    @Value("${bmp.delete}")
    protected boolean bmpDelete;

    @Resource
    protected FileSystemReader fileSystemReader;

    @Resource
    protected LogFileExtractor logFileExtractor;

    @Resource
    protected KakaduParameterCalculator kakaduParameterCalculator;

    @Resource
    protected ImageManipulator imageManipulator;

    @Override
    public final ImageMetadata extractImageMetadata(String identifier) throws IOException, InterruptedException {

        log.debug("Extracting metadata for jp2 file with identifier [" + identifier + "]");

        if (StringUtils.isEmpty(identifier)) {
            return null;
        }

        if (!fileSystemReader.imageFileExists(identifier)) {
            throw new ImageServiceException(MISSING_IMAGE_MESSAGE, 404, ParameterName.IDENTIFIER);
        }

        if (!fileSystemReader.logFileExists(identifier)) {
            generateKduLogFile(identifier);
        }

        ImageMetadata imageMetadata = loadAndParseLogFile(identifier);
        if (null == imageMetadata) {
            throw new ImageServiceException(MISSING_IMAGE_MESSAGE, 404, ParameterName.IDENTIFIER);
        }

        return imageMetadata;
    }

    @Override
    public ImageRequestMetadata extractImageMetadata(ServerRequestData serverRequestData) throws IOException,
            InterruptedException {

        ImageMetadata imageMetadata = extractImageMetadata(serverRequestData.getIdentifier());
        ImageRequestMetadata imageRequestMetadata = new ImageRequestMetadata(imageMetadata);

        imageRequestMetadata.setScheme(serverRequestData.getScheme());
        imageRequestMetadata.setHost(serverRequestData.getHost());
        imageRequestMetadata.setPort(serverRequestData.getPort());
        imageRequestMetadata.setContextPath(serverRequestData.getContextPath());

        return imageRequestMetadata;
    }

    @Override
    public byte[] extractImage(RequestData requestData) throws InterruptedException, IOException {

        log.debug("Simple extraction of image using parameters [" + requestData + "]");

        ImageMetadata jp2ImageMetadata = extractImageMetadata(requestData.getIdentifier());

        Path bmpFile = fileSystemReader.getOutputFilename(".bmp");

        // create .bmp file
        callShellCommand(buildExtractImageCommandString(kakaduBinaryPath, requestData, jp2ImageMetadata, bmpFile));

        log.debug("Reading in extracted file from [" + bmpFile.toString() + "]");
        BufferedImage bmpInputImage = null;

        try {
            bmpInputImage = ImageIO.read(bmpFile.toFile());
        } catch (IllegalArgumentException iae) {
            // occurs when kakadu generates file that is unreadable due to bad inputs
            // e.g. the resulting file has width or height <= 0
            throw new ImageServiceException(iae.getMessage(), 400, ParameterName.UNKNOWN);
        }
        if (bmpDelete) {
            Files.delete(bmpFile);
        }

        BufferedImage manipulatedImage = imageManipulator.changeImage(bmpInputImage, requestData, jp2ImageMetadata);

        String outputFormat = requestData.getFormat();
        if (outputFormat.toUpperCase().equals(ImageFormat.JP2.name())) {
            outputFormat = "JPEG2000";
        }

        // now write bmp in-memory image to output stream in requested format
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(manipulatedImage, outputFormat, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }

    }

    @Override
    public final String getComplianceLevelUrl() {
        return COMPLIANCE_LEVEL_URL + 2;
    }

    protected String[] buildExtractImageCommandString(String binaryPath, RequestData requestData,
            ImageMetadata imageMetadata, Path outputPath) {

        int reduce = kakaduParameterCalculator.calculateReduceParameter(imageMetadata, requestData);
        String jp2ImageFilename = fileSystemReader.getImagePathFromIdentifier(requestData.getIdentifier()).toString();

        String[] command = new String[] { binaryPath, "-resilient", "-quiet", "-reduce", String.valueOf(reduce), "-i",
                jp2ImageFilename, "-o", outputPath.toString() };

        command = addRegionCommand(imageMetadata, requestData, command);

        log.debug("Extract image shell command [" + StringUtils.join(command, " ") + "]");

        return command;

    }

    protected void callShellCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        process.waitFor();

    }

    protected String[] addRegionCommand(ImageMetadata imageMetadata, RequestData requestData, String[] command) {

        String regionCommandValue = kakaduParameterCalculator.calculateRegionExtractionParameter(imageMetadata,
                requestData);

        if (!StringUtils.isEmpty(regionCommandValue)) {
            command = Arrays.copyOf(command, command.length + 2);
            command[command.length - 2] = "-region";
            command[command.length - 1] = regionCommandValue;
        }

        return command;

    }

    private void generateKduLogFile(String identifier) throws IOException, InterruptedException {

        log.debug("Calling kdu_expand binary to extract metadata for identifier [" + identifier + "]");

        String imageFilename = fileSystemReader.getImagePathFromIdentifier(identifier).toString();
        String logFilename = fileSystemReader.getLogFileFromIdentifier(identifier).toString();

        callShellCommand(kakaduBinaryPath, "-i", imageFilename, "-record", logFilename);

    }

    private ImageMetadata loadAndParseLogFile(String identifier) throws IOException {

        String logFile = fileSystemReader.readFile(fileSystemReader.getLogFileFromIdentifier(identifier));
        if (StringUtils.isEmpty(logFile)) {
            throw new ImageServiceException("image metadata log file empty", 500, ParameterName.UNKNOWN);
        }

        log.trace("identifier [" + identifier + "] produces log file [" + logFile + "]");

        return logFileExtractor.extractImageMetadata(identifier, logFile, getComplianceLevelUrl());
    }

}
