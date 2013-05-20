package uk.bl.iiifimageservice.service;

import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import uk.bl.iiifimageservice.service.FileSystemReader;

@Ignore
public class FileSystemReaderTest {

    private FileSystemReader fileSystemReader = new FileSystemReader();

    @Test
    @Ignore
    public void testReadLogFile() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testGetImageFileFromIdentifier() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testGetImageFilenameFromIdentifier() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testImageFileExists() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testGetLogFileFromIdentifier() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testGetLogFilenameFromIdentifier() {
        fail("Not yet implemented");
    }

    @Test
    public void testLogFileExists() {

        fileSystemReader.logFileExists("WorkingLogFile");

        fail("Not yet implemented");
    }

}
