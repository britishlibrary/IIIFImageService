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
package uk.bl.iiifimageservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.bl.iiifimageservice.domain.ImageError;
import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.util.ImageServiceException;

/**
 * More tests could be added - in particular testing http status codes.
 * 
 * @author pblake
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:image-servlet-test.xml")
public class ImageControllerTest {

    private MockMvc mockMvc;

    private TestImageController testImageController;

    @Resource
    private RequestValidator requestValidator;

    @Resource
    private ControllerHelper controllerHelper;

    @Before
    public void setup() {

        this.testImageController = new TestImageController();
        this.mockMvc = standaloneSetup(this.testImageController).setValidator(requestValidator)
                                                                .build();

    }

    @Test
    public void imageRequestTest() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(
                get("/identifier/pct:10,20,30,40/600,500/0/native.jpg").accept(MediaType.IMAGE_JPEG))
                                          .andDo(print())
                                          .andExpect(request().asyncStarted())
                                          .andExpect(request().asyncResult(new byte[] { 'a', 'b' }))
                                          .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.IMAGE_JPEG));

    }

    @Test
    public void imageRequestNoFormatTest() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(
                get("/identifier/pct:10,20,30,40/600,500/0/native").accept(MediaType.IMAGE_JPEG))
                                          .andDo(print())
                                          .andExpect(request().asyncStarted())
                                          .andExpect(request().asyncResult(new byte[] { 'a', 'b' }))
                                          .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.IMAGE_JPEG));

    }

    @Test
    public void imageRequestBadFormatTest() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(
                get("/identifier/pct:10,20,30,40/600,500/0/native.123").accept(MediaType.IMAGE_JPEG))
                                          .andDo(print())
                                          .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                    .andDo(print())
                    .andExpect(status().isUnsupportedMediaType());

    }

    private class TestImageController {

        @RequestMapping(value = { "/{identifier}/{region}/{size}/{rotation}/{quality}.{format}",
                "/{identifier}/{region}/{size}/{rotation}/{quality}**" }, method = RequestMethod.GET)
        public @ResponseBody
        Callable<byte[]> getImage(final @Valid RequestData requestData) throws Exception {

            return new Callable<byte[]>() {
                @Override
                public byte[] call() throws Exception {
                    return new byte[] { 'a', 'b' };
                }
            };
        }

        @RequestMapping(value = "/{identifier}/info", method = RequestMethod.GET)
        public @ResponseBody
        Callable<ImageMetadata> getImageMetadata(final @PathVariable String identifier) {

            return new Callable<ImageMetadata>() {
                @Override
                public ImageMetadata call() throws Exception {
                    return new ImageMetadata();
                }
            };
        }

        @ExceptionHandler
        @ResponseBody
        public ResponseEntity<String> handleException(ImageServiceException imageServiceException,
                HttpServletResponse response) {

            ImageError imageError = controllerHelper.extractErrorFrom(imageServiceException);
            String errorAsXml = controllerHelper.convertImageErrorToXml(imageError);

            return new ResponseEntity<String>(errorAsXml, controllerHelper.createExceptionHeaders(),
                    HttpStatus.valueOf(imageServiceException.getStatusCode()));

        }

    }

}
