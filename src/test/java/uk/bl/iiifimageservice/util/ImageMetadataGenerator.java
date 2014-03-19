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
package uk.bl.iiifimageservice.util;

import java.util.Arrays;

import uk.bl.iiifimageservice.domain.ImageMetadata;

public class ImageMetadataGenerator {

    public static ImageMetadata getTestImageMetadata() {

        ImageMetadata imageMetadata = new ImageMetadata();

        imageMetadata.setIdentifier("1E34750D-38DB-4825-A38A-B60A345E591C");
        imageMetadata.setWidth(4700);
        imageMetadata.setHeight(6500);
        imageMetadata.setScaleFactors(Arrays.asList(0, 1, 4, 9, 16, 25));
        imageMetadata.setTileWidth(4700);
        imageMetadata.setTileHeight(6500);
        imageMetadata.setFormats(Arrays.asList("jpg", "png", "gif"));
        imageMetadata.setQualities(Arrays.asList("native", "grey", "color", "bitonal"));

        return imageMetadata;

    }

}
