package com.health;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.Enumeration;

public class Prefilter extends ZuulFilter {

    private final static Logger log = LoggerFactory.getLogger(Prefilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        String SALTOLINEA = System.lineSeparator();
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        StringBuffer strLog = new StringBuffer();
        strLog.append("................ RECIBIDA PETICION EN /api ......  " + SALTOLINEA);
        strLog.append("Metodo: " + request.getMethod() + SALTOLINEA);
        strLog.append("URL: " + request.getRequestURL() + SALTOLINEA);
        strLog.append("Host Remoto: " + request.getRemoteHost() + SALTOLINEA);
        strLog.append("----- MAP ----" + SALTOLINEA);
        request.getParameterMap().forEach((key, value) -> {
            for (int n = 0; n < value.length; n++) {
                strLog.append("Clave:" + key + " Valor: " + value[n] + SALTOLINEA);
            }
        });
        strLog.append(SALTOLINEA + "----- Headers ----" + SALTOLINEA);
        Enumeration < String > nameHeaders = request.getHeaderNames();
        while (nameHeaders.hasMoreElements()) {
            String name = nameHeaders.nextElement();
            Enumeration < String > valueHeaders = request.getHeaders(name);
            while (valueHeaders.hasMoreElements()) {
                String value = valueHeaders.nextElement();
                strLog.append("Clave:" + name + " Valor: " + value + SALTOLINEA);
            }
        }
        try {
            strLog.append(SALTOLINEA + "----- BODY ----" + SALTOLINEA);
            BufferedReader reader = request.getReader();
            if (reader != null) {
                char[] linea = new char[100];
                int nCaracteres;
                while ((nCaracteres = reader.read(linea, 0, 100)) > 0) {
                    strLog.append(linea);
                    if (nCaracteres != 100)
                        break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        log.info(strLog.toString());
         return null;
    }
}
