//
// Overview
//
For an overview of this project please visit http://sanddragon.bl.uk

//
// IIIF Server notes
//
This SanddragonImageService was developed to conform with the following API http://lib.stanford.edu/iiif/
This project was developed using the Kakadu Software kdu_expand.exe component and is intended to be a lightweight JPEG 2000 image service.

//
// SanddragonImageService Installation
//

1. Ensure you have a Java 7 JRE (http://www.java.com) and Tomcat 7 (http://tomcat.apache.org/) installed.
2. Drop the SanddragonImageService.war file into the webapps directory in your Tomcat install location. 
3. Download the Kakadu demo from the Kakadu Software website - http://www.kakadusoftware.com/executables/KDU72_Demo_Apps_for_Win32_130117.msi
4. Install the Kakadu demo and ensure that it works by using a command prompt to navigate to the installation folder and running kdu_expand.exe
5. A SanddragonImageService.properties file is embedded in the war file. To override this drop a copy in your user directory e.g. for Windows 7 this is c:\Users\[loginName]
6. Update your SanddragonImageService.properties kakadu.binary.path property to point to the location of the Kakadu installation, if needed. 
		The default is C:\Program Files (x86)\Kakadu\kdu_expand.exe
7. Update your SanddragonImageService.properties image.root.path property to point to the location of your JP2 files. The default is C:\JP2Cache
8. Temporary files are stored in the location determined by java.io.tmpdir system property e.g. C:\Dev\apache-tomcat-7.0.35\temp. 
9. There are currently two strategies for resolving the image location. When using the SimpleImageLocationStrategy the jp2 file would be located at c:\JP2Cache\imagefilename.jp2. 
		When using the DirectoryFileNoExtensionImageLocationStrategy the jp2 file would be located at c:\JP2Cache\imagefilename\imagefilename   
10. The conversion of jpg images from the output of Kadadu is configurable. It is set via a property in SanddragonImageService.properties called kakadu.extractor.strategy. The valid values are -
	a. SimpleKakaduExtractor - uses just the Kakadu binary
	b. SequentialKakaduExtractor - uses the Kakadu binary to extract .bmp and then cjpeg then converts this to .jpg
	c. PipedKakaduExtractor - pipes output from Kakadu direct to cjpeg (unix only - untested)
	d. IntegratedKakaduExtractor - for use where cjpeg is embedded in Kakadu binary (unix only - untested)
11. Multiple versions can be installed in parallel. Ensure that the name of the war file matches the name of any property overrides file 
	e.g. if the war file is called SanddragonImageService.war then it's property override file would be called SanddragonImageService.properties. 
//
// Example Usage
//

To get a full colour image at 10% of size -
http://localhost:8080/SanddragonImageService/[jp2 filename]/full/pct:10/0/color.jpg

For image metadata information - 
http://localhost:8080/SanddragonImageService/[jp2 filename]/info.xml


Further usage examples can be found at the IIIF link - http://library.stanford.edu/iiif/image-api/

If you do not have any JPEG2000 images available example JPEG2000s can be downloaded from here - http://sanddragon.bl.uk/JP2/

//
// Build instructions for developers
//

1. The application is a Maven-based application written using Java 7 and tested with Tomcat 7. 
2. Build the war file in the usual Maven fashion - mvn package
3. If you want to output jp2 images drop jai-imageio-core-standalone-1.2-pre-dr-b04-2013-04-23-sources.jar into the lib\ext directory of the Tomcat java install. For convenience, there will be a copy in the war file.
4. If any X11 errors occur when running on a unix platform try setting the CATALINA_OPTS env variable to -Djava.awt.headless=true 

//
// Known Issues
//
1. Requests for invalid regions not handled
