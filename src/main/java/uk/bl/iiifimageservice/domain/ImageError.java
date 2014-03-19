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
package uk.bl.iiifimageservice.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "error")
@XmlType(propOrder = { "parameterName", "message" })
public class ImageError {

    public ImageError() {
    }

    public ImageError(ParameterName parameterName, String message) {
        this.parameterName = parameterName;
        this.message = message;
    }

    public enum ParameterName {
        // @formatter:off
        @XmlEnumValue("unknown") UNKNOWN, 
        @XmlEnumValue("identifier") IDENTIFIER, 
        @XmlEnumValue("region") REGION, 
        @XmlEnumValue("size") SIZE, 
        @XmlEnumValue("rotation") ROTATION, 
        @XmlEnumValue("quality") QUALITY, 
        @XmlEnumValue("format") FORMAT
        // @formatter:on
    }

    private ParameterName parameterName;

    private String message;

    @XmlElement(name = "parameter")
    public ParameterName getParameterName() {
        return parameterName;
    }

    public void setParameterName(ParameterName parameterName) {
        this.parameterName = parameterName;
    }

    @XmlElement(name = "text")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error [parameterName=" + parameterName + ", message=" + message + "]";
    }

}