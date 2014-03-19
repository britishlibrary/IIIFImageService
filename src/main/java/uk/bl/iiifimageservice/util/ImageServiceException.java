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

import uk.bl.iiifimageservice.domain.ImageError.ParameterName;

public class ImageServiceException extends RuntimeException {

    private static final long serialVersionUID = 7887180486907358323L;
    private int statusCode;
    private ParameterName parameterName;

    public ImageServiceException(int statusCode, ParameterName parameterName) {
        super();
        this.statusCode = statusCode;
        this.parameterName = parameterName;
    }

    public ImageServiceException(String message, int statusCode, ParameterName parameterName) {
        super(message);
        this.statusCode = statusCode;
        this.parameterName = parameterName;
    }

    public ImageServiceException(String message, int statusCode, ParameterName parameterName, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.parameterName = parameterName;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ParameterName getParameterName() {
        return this.parameterName;
    }

}
