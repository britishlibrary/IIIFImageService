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

import uk.bl.iiifimageservice.domain.RequestData;

public class RequestDataGenerator {

    public static RequestData getTestRequestDataFullRegionFullSize() {

        RequestData requestData = new RequestData();

        requestData.setFormat("jpg");
        requestData.setIdentifier("1E34750D-38DB-4825-A38A-B60A345E591C");
        requestData.setQuality("color");
        requestData.setRegion("full");
        requestData.setRotation(0);
        requestData.setSize("full");

        return requestData;

    }

    public static RequestData getTestRequestDataFullRegionSizeSetByWidth() {

        RequestData requestData = getTestRequestDataFullRegionFullSize();
        requestData.setSize("600,");

        return requestData;

    }

    public static RequestData getTestRequestDataFullRegionSizeSetByHeight() {

        RequestData requestData = getTestRequestDataFullRegionFullSize();
        requestData.setSize(",1000");

        return requestData;

    }

    public static RequestData getTestRequestDataFullRegionSizeSetByPixels() {

        RequestData requestData = getTestRequestDataFullRegionFullSize();
        requestData.setSize("600,1000");

        return requestData;

    }

    public static RequestData getTestRequestDataFullRegionSizeSetByPercent() {

        RequestData requestData = getTestRequestDataFullRegionFullSize();
        requestData.setSize("pct:20");

        return requestData;

    }

    public static RequestData getTestRequestDataPixelRegionFullSize() {

        RequestData requestData = getTestRequestDataFullRegionFullSize();
        requestData.setRegion("1000,1100,1200,1300");

        return requestData;

    }

    public static RequestData getTestRequestDataPixelRegionSizeSetByWidth() {

        RequestData requestData = getTestRequestDataFullRegionFullSize();
        requestData.setRegion("1000,1100,1200,1300");
        requestData.setSize("600,");

        return requestData;

    }

    public static RequestData getTestRequestDataPixelRegionSizeSetByHeight() {

        RequestData requestData = getTestRequestDataFullRegionFullSize();
        requestData.setRegion("1000,1100,1200,1300");
        requestData.setSize(",600");

        return requestData;

    }

    public static RequestData getTestRequestDataPixelRegionSizeSetByPixels() {

        RequestData requestData = getTestRequestDataFullRegionFullSize();
        requestData.setRegion("1000,1100,1200,1300");
        requestData.setSize("600,1300");

        return requestData;

    }

    public static RequestData getTestRequestDataPercentRegionFullSize() {

        RequestData requestData = getTestRequestDataFullRegionFullSize();
        requestData.setRegion("pct:10,20,30,40");

        return requestData;

    }

}
