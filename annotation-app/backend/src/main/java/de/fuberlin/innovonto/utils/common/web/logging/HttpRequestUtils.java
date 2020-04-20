package de.fuberlin.innovonto.utils.common.web.logging;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class HttpRequestUtils {

    private HttpRequestUtils() {
    }

    public static String getIP(HttpServletRequest request) {
        final String forwardedIp = request.getHeader("X-FORWARDED-FOR");
        return (forwardedIp == null) ? getRemoteAddr(request) : forwardedIp;
    }

    public static String getRemoteAddr(HttpServletRequest request) {
        final String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
        if (ipFromHeader != null && ipFromHeader.length() > 0) {
            return ipFromHeader;
        }
        return request.getRemoteAddr();
    }

    public static String getParameters(HttpServletRequest request) {
        final StringBuilder posted = new StringBuilder();
        final Enumeration<?> e = request.getParameterNames();
        if (e != null) {
            posted.append("?");
            while (e.hasMoreElements()) {
                if (posted.length() > 1) {
                    posted.append("&");
                }
                String curr = (String) e.nextElement();
                posted.append(curr).append("=");
                if (curr.contains("password")
                        || curr.contains("pass")
                        || curr.contains("pwd")) {
                    posted.append("*****");
                } else {
                    posted.append(request.getParameter(curr));
                }
            }
        }
        return posted.toString();
    }
}
