
package com.sf.integration.expressservice.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.Response;
import javax.xml.ws.ResponseWrapper;
import java.util.concurrent.Future;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.1 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "IService", targetNamespace = "http://service.expressservice.integration.sf.com/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface IService {


    /**
     * 
     * @param arg0
     * @return
     *     returns javax.xml.ws.Response<com.sf.integration.expressservice.service.SfexpressServiceResponse>
     */
    @WebMethod(operationName = "sfexpressService")
    @RequestWrapper(localName = "sfexpressService", targetNamespace = "http://service.expressservice.integration.sf.com/", className = "com.sf.integration.expressservice.service.SfexpressService")
    @ResponseWrapper(localName = "sfexpressServiceResponse", targetNamespace = "http://service.expressservice.integration.sf.com/", className = "com.sf.integration.expressservice.service.SfexpressServiceResponse")
    public Response<SfexpressServiceResponse> sfexpressServiceAsync(
            @WebParam(name = "arg0", targetNamespace = "")
            String arg0);

    /**
     * 
     * @param arg0
     * @param asyncHandler
     * @return
     *     returns java.util.concurrent.Future<? extends java.lang.Object>
     */
    @WebMethod(operationName = "sfexpressService")
    @RequestWrapper(localName = "sfexpressService", targetNamespace = "http://service.expressservice.integration.sf.com/", className = "com.sf.integration.expressservice.service.SfexpressService")
    @ResponseWrapper(localName = "sfexpressServiceResponse", targetNamespace = "http://service.expressservice.integration.sf.com/", className = "com.sf.integration.expressservice.service.SfexpressServiceResponse")
    public Future<?> sfexpressServiceAsync(
            @WebParam(name = "arg0", targetNamespace = "")
            String arg0,
            @WebParam(name = "asyncHandler", targetNamespace = "")
            AsyncHandler<SfexpressServiceResponse> asyncHandler);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "sfexpressService", targetNamespace = "http://service.expressservice.integration.sf.com/", className = "com.sf.integration.expressservice.service.SfexpressService")
    @ResponseWrapper(localName = "sfexpressServiceResponse", targetNamespace = "http://service.expressservice.integration.sf.com/", className = "com.sf.integration.expressservice.service.SfexpressServiceResponse")
    public String sfexpressService(
            @WebParam(name = "arg0", targetNamespace = "")
            String arg0);

}