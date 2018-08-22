/*
 * Copyright 2017-2018 Baidu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.openrasp.request;

import com.baidu.openrasp.tool.Reflection;
import com.baidu.openrasp.transformer.CustomClassTransformer;
import com.baidu.openrasp.tool.model.ApplicationModel;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by zhuming01 on 6/15/17.
 * All rights reserved
 * javax.servlet.http.HttpServletRequest类请求的统一格式接口
 */
public final class HttpServletRequest extends AbstractRequest {
    private static final Map<String, String[]> EMPTY_PARAM = new HashMap<String, String[]>();
    private static final Pattern PATTERN = Pattern.compile("\\d+(\\.\\d+)*");

    /**
     * 请求实体
     *
     * @param request 类型为javax.servlet.http.HttpServletRequest的请求实体
     */
    public HttpServletRequest(Object request) {
        super(request);
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getLocalAddr()
     */
    @Override
    public String getLocalAddr() {
        return Reflection.invokeStringMethod(request, "getLocalAddr", EMPTY_CLASS);
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getMethod()
     */
    @Override
    public String getMethod() {
        return Reflection.invokeStringMethod(request, "getMethod", EMPTY_CLASS);
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getProtocol()
     */
    @Override
    public String getProtocol() {
        return Reflection.invokeStringMethod(request, "getProtocol", EMPTY_CLASS);
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getAuthType()
     */
    @Override
    public String getAuthType() {
        return Reflection.invokeStringMethod(request, "getAuthType", EMPTY_CLASS);
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getContextPath()
     */
    @Override
    public String getContextPath() {
        return Reflection.invokeStringMethod(request, "getContextPath", EMPTY_CLASS);
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getRemoteAddr()
     */
    @Override
    public String getRemoteAddr() {
        return Reflection.invokeStringMethod(request, "getRemoteAddr", EMPTY_CLASS);
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getRequestURI()
     */
    @Override
    public String getRequestURI() {
        return Reflection.invokeStringMethod(request, "getRequestURI", EMPTY_CLASS);
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getRequestURL()
     */
    @Override
    public StringBuffer getRequestURL() {
        Object ret = Reflection.invokeMethod(request, "getRequestURL", EMPTY_CLASS);
        return ret != null ? (StringBuffer) ret : null;
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getServerName()
     */
    @Override
    public String getServerName() {
        return Reflection.invokeStringMethod(request, "getServerName", EMPTY_CLASS);
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getParameter(String)
     */
    @Override
    public String getParameter(String key) {
        if (!canGetParameter) {
            if (!setCharacterEncodingFromConfig()) {
                return null;
            }
        }
        return Reflection.invokeStringMethod(request, "getParameter", STRING_CLASS, key);
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getParameterNames()
     */
    @Override
    public Enumeration<String> getParameterNames() {
        if (!canGetParameter) {
            if (!setCharacterEncodingFromConfig()) {
                return null;
            }
        }
        Object ret = Reflection.invokeMethod(request, "getParameterNames", EMPTY_CLASS);
        return ret != null ? (Enumeration) ret : null;
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getParameterMap()
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> res = new HashMap<String, String[]>();
        if (!canGetParameter) {
            if (!setCharacterEncodingFromConfig()) {
                res = EMPTY_PARAM;
            }
        } else {
            res = (Map<String, String[]>) Reflection.invokeMethod(request, "getParameterMap", EMPTY_CLASS);
        }

        ClassLoader loader = CustomClassTransformer.getClassLoader("org.apache.commons.fileupload.servlet.ServletFileUpload");
        boolean isMultipartContent = false;
        if (loader != null) {
            try {
                isMultipartContent = (Boolean) Reflection.invokeStaticMethod(
                        "org.apache.commons.fileupload.servlet.ServletFileUpload",
                        "isMultipartContent",
                        new Class[]{loader.loadClass("javax.servlet.http.HttpServletRequest")}, request);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        if (isMultipartContent) {
            mergeMap(fileUploadCache, res);
        }

        return res;
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getHeader(String)
     */
    @Override
    public String getHeader(String key) {
        return Reflection.invokeStringMethod(request, "getHeader", STRING_CLASS, key);
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getHeaderNames()
     */
    @Override
    public Enumeration<String> getHeaderNames() {
        Object ret = Reflection.invokeMethod(request, "getHeaderNames", EMPTY_CLASS);
        return ret != null ? (Enumeration<String>) ret : null;
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getQueryString()
     */
    @Override
    public String getQueryString() {
        return Reflection.invokeStringMethod(request, "getQueryString", EMPTY_CLASS);
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getServerContext()
     */
    @Override
    public Map<String, String> getServerContext() {
        return ApplicationModel.getApplicationInfo();
    }

    /**
     * 根据服务器信息获取服务器类型
     *
     * @param serverInfo 服务器信息
     * @return 服务器类型
     */
    public static String extractType(String serverInfo) {
        if (serverInfo == null) {
            return null;
        }
        serverInfo = serverInfo.toLowerCase();
        if (serverInfo.contains("tomcat")) return "Tomcat";
        if (serverInfo.contains("jboss")) return "JBoss";
        if (serverInfo.contains("jetty")) return "Jetty";
        return serverInfo;
    }

    /**
     * (none-javadoc)
     *
     * @see AbstractRequest#getAppBasePath()
     */
    @Override
    public String getAppBasePath() {
        try {
            String realPath = Reflection.invokeStringMethod(request, "getRealPath", new Class[]{String.class}, "/");
            return realPath == null ? "" : realPath;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void mergeMap(Map<String, String[]> src, Map<String, String[]> dst) {
        for (Map.Entry<String, String[]> entry : src.entrySet()) {
            if (dst.containsKey(entry.getKey())) {
                dst.put(entry.getKey(), mergeArray(dst.get(entry.getKey()), entry.getValue()));
            } else {
                dst.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public String[] mergeArray(String[] s1, String[] s2) {
        int str1Length = s1.length;
        int str2length = s2.length;
        s1 = Arrays.copyOf(s1, str1Length + str2length);
        System.arraycopy(s2, 0, s1, str1Length, str2length);
        return s1;
    }
}
