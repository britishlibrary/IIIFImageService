package uk.bl.iiifimageservice.util;

import uk.bl.iiifimageservice.domain.RequestData;

public class RequestDataGenerator {

    public static RequestData getTestRequestDataFullRegionFullSize() {

        RequestData requestData = new RequestData();

        requestData.setFormat("jpg");
        requestData.setIdentifier("1E34750D-38DB-4825-A38A-B60A345E591C");
        requestData.setQuality("color");
        requestData.setRegion("full");
        requestData.setRotation(0f);
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
